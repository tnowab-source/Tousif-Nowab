package com.example.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.OrderHistoryEntity
import com.example.data.OrderHistoryRepository
import com.example.model.FoodStopRepository
import com.example.model.MenuCategory
import com.example.model.MenuItem
import com.example.model.PriceRangeFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppTab {
    HOME,
    FULL_MENU,
    FAVORITES,
    ORDER_HISTORY,
    VISIT_AND_QR
}

enum class OrderStatus(val id: String, val stepIndex: Int) {
    PREPARING("preparing", 0),
    OUT_FOR_DELIVERY("out_for_delivery", 1),
    DELIVERED("delivered", 2);

    fun next(): OrderStatus = when (this) {
        PREPARING -> OUT_FOR_DELIVERY
        OUT_FOR_DELIVERY -> DELIVERED
        DELIVERED -> PREPARING
    }

    companion object {
        fun fromId(id: String): OrderStatus =
            entries.find { it.id == id } ?: PREPARING
    }
}

data class QrModalData(
    val title: String,
    val subtitle: String,
    val url: String,
    val isWhatsApp: Boolean
)

data class FoodStopUiState(
    val currentTab: AppTab = AppTab.HOME,
    val selectedCategoryId: String? = null,
    val searchQuery: String = "",
    val activeCategoryFilterId: String? = null,
    val activePriceFilter: PriceRangeFilter? = null,
    val currentOrderStatus: OrderStatus = OrderStatus.PREPARING,
    val favoriteItemIds: Set<String> = emptySet(),
    val cartQuantities: Map<String, Int> = emptyMap(),
    val itemInstructions: Map<String, String> = emptyMap(),
    val orderNote: String = "",
    val isOrderSheetVisible: Boolean = false,
    val activeQrModal: QrModalData? = null,
    val dailySpecialDayOfYear: Int = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR),
    val dailySpecialOffset: Int = 0
) {
    val selectedCategory: MenuCategory?
        get() = selectedCategoryId?.let { FoodStopRepository.getCategoryById(it) }

    val dailySpecialPair: Pair<MenuCategory, MenuItem>
        get() = FoodStopRepository.getDailySpecial(dailySpecialDayOfYear, dailySpecialOffset)

    val totalCartItems: Int
        get() = cartQuantities.values.sum()

    val totalCartPrice: Int
        get() = cartQuantities.entries.sumOf { (itemId, count) ->
            val item = FoodStopRepository.allItems.find { it.id == itemId }
            (item?.price ?: 0) * count
        }

    val cartItemsDetailed: List<Pair<MenuItem, Int>>
        get() = cartQuantities.entries.mapNotNull { (itemId, count) ->
            if (count <= 0) null
            else FoodStopRepository.allItems.find { it.id == itemId }?.let { it to count }
        }

    val favoriteItemsWithCategory: List<Pair<MenuCategory, MenuItem>>
        get() = FoodStopRepository.categories.flatMap { category ->
            category.items
                .filter { it.id in favoriteItemIds }
                .map { category to it }
        }
}

class MenuViewModel(
    private val orderHistoryRepository: OrderHistoryRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(FoodStopUiState())
    val uiState: StateFlow<FoodStopUiState> = _uiState.asStateFlow()

    val orderHistory: StateFlow<List<OrderHistoryEntity>> =
        orderHistoryRepository?.allOrders?.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        ) ?: MutableStateFlow(emptyList())

    init {
        orderHistoryRepository?.allFavoriteItemIds?.let { favFlow ->
            viewModelScope.launch {
                favFlow.collect { idList ->
                    _uiState.update { it.copy(favoriteItemIds = idList.toSet()) }
                }
            }
        }
    }

    fun toggleFavorite(itemId: String) {
        val isCurrentlyFav = itemId in _uiState.value.favoriteItemIds
        _uiState.update { state ->
            val updated = if (isCurrentlyFav) {
                state.favoriteItemIds - itemId
            } else {
                state.favoriteItemIds + itemId
            }
            state.copy(favoriteItemIds = updated)
        }
        orderHistoryRepository?.let { repo ->
            viewModelScope.launch {
                if (isCurrentlyFav) {
                    repo.removeFavorite(itemId)
                } else {
                    repo.addFavorite(itemId)
                }
            }
        }
    }

    fun clearAllFavorites() {
        _uiState.update { it.copy(favoriteItemIds = emptySet()) }
        orderHistoryRepository?.let { repo ->
            viewModelScope.launch {
                repo.clearAllFavorites()
            }
        }
    }

    fun addAllFavoritesToCart() {
        val favIds = _uiState.value.favoriteItemIds
        if (favIds.isEmpty()) return
        _uiState.update { state ->
            val updated = state.cartQuantities.toMutableMap()
            favIds.forEach { id ->
                updated[id] = (updated[id] ?: 0) + 1
            }
            state.copy(
                cartQuantities = updated,
                isOrderSheetVisible = true
            )
        }
    }

    fun selectTab(tab: AppTab) {
        _uiState.update {
            it.copy(
                currentTab = tab,
                selectedCategoryId = if (tab != AppTab.HOME) null else it.selectedCategoryId
            )
        }
    }

    fun openCategory(categoryId: String) {
        _uiState.update {
            it.copy(
                currentTab = AppTab.HOME,
                selectedCategoryId = categoryId
            )
        }
    }

    fun closeCategory() {
        _uiState.update {
            it.copy(selectedCategoryId = null)
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setCategoryFilter(categoryId: String?) {
        _uiState.update {
            val next = if (it.activeCategoryFilterId == categoryId) null else categoryId
            it.copy(activeCategoryFilterId = next)
        }
    }

    fun setPriceFilter(priceFilter: PriceRangeFilter?) {
        _uiState.update {
            val next = if (it.activePriceFilter == priceFilter) null else priceFilter
            it.copy(activePriceFilter = next)
        }
    }

    fun clearSearchAndFilters() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                activeCategoryFilterId = null,
                activePriceFilter = null
            )
        }
    }

    fun setOrderStatus(status: OrderStatus) {
        _uiState.update { it.copy(currentOrderStatus = status) }
    }

    fun cycleOrderStatus() {
        _uiState.update { it.copy(currentOrderStatus = it.currentOrderStatus.next()) }
    }

    fun shuffleDailySpecial() {
        _uiState.update {
            it.copy(dailySpecialOffset = (it.dailySpecialOffset + 1) % FoodStopRepository.allItems.size)
        }
    }

    fun resetDailySpecialToToday() {
        _uiState.update { it.copy(dailySpecialOffset = 0) }
    }

    fun incrementItem(item: MenuItem) {
        _uiState.update { state ->
            val current = state.cartQuantities[item.id] ?: 0
            val updated = state.cartQuantities.toMutableMap()
            updated[item.id] = current + 1
            state.copy(cartQuantities = updated)
        }
    }

    fun decrementItem(item: MenuItem) {
        _uiState.update { state ->
            val current = state.cartQuantities[item.id] ?: 0
            val updated = state.cartQuantities.toMutableMap()
            val updatedInstructions = state.itemInstructions.toMutableMap()
            if (current <= 1) {
                updated.remove(item.id)
                updatedInstructions.remove(item.id)
                val shouldCloseSheet = updated.isEmpty() && state.isOrderSheetVisible
                state.copy(
                    cartQuantities = updated,
                    itemInstructions = updatedInstructions,
                    isOrderSheetVisible = if (shouldCloseSheet) false else state.isOrderSheetVisible
                )
            } else {
                updated[item.id] = current - 1
                state.copy(cartQuantities = updated)
            }
        }
    }

    fun updateItemInstruction(itemId: String, instruction: String) {
        _uiState.update { state ->
            val updated = state.itemInstructions.toMutableMap()
            if (instruction.isBlank()) {
                updated.remove(itemId)
            } else {
                updated[itemId] = instruction
            }
            state.copy(itemInstructions = updated)
        }
    }

    fun appendPresetInstruction(itemId: String, preset: String) {
        _uiState.update { state ->
            val existing = state.itemInstructions[itemId]?.trim().orEmpty()
            val existingParts = existing.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            val nextInstruction = if (existingParts.any { it.equals(preset, ignoreCase = true) }) {
                existingParts.filterNot { it.equals(preset, ignoreCase = true) }.joinToString(", ")
            } else {
                (existingParts + preset).joinToString(", ")
            }
            val updated = state.itemInstructions.toMutableMap()
            if (nextInstruction.isBlank()) {
                updated.remove(itemId)
            } else {
                updated[itemId] = nextInstruction
            }
            state.copy(itemInstructions = updated)
        }
    }

    fun clearCart() {
        _uiState.update {
            it.copy(
                cartQuantities = emptyMap(),
                itemInstructions = emptyMap(),
                orderNote = "",
                isOrderSheetVisible = false
            )
        }
    }

    fun updateOrderNote(note: String) {
        _uiState.update { it.copy(orderNote = note) }
    }

    fun setOrderSheetVisible(visible: Boolean) {
        _uiState.update { it.copy(isOrderSheetVisible = visible) }
    }

    fun showQrModal(data: QrModalData?) {
        _uiState.update { it.copy(activeQrModal = data) }
    }

    /**
     * Records the current order into local Room storage when placed.
     */
    fun recordCurrentOrderToHistory(fallbackCategory: MenuCategory? = null) {
        val repo = orderHistoryRepository ?: return
        val state = _uiState.value
        val selectedList = state.cartItemsDetailed

        val entity = if (selectedList.isNotEmpty()) {
            val summary = selectedList.joinToString(", ") { (item, count) ->
                val qtySuffix = item.qty?.let { " ($it)" } ?: ""
                val itemNote = state.itemInstructions[item.id]?.trim()?.takeIf { it.isNotEmpty() }
                val noteBracket = itemNote?.let { " [$it]" } ?: ""
                "${item.name}$qtySuffix × $count$noteBracket"
            }
            val encoded = selectedList.joinToString("|") { (item, count) ->
                "${item.id}:$count"
            }
            OrderHistoryEntity(
                itemsSummary = summary,
                encodedItems = encoded,
                totalItemsCount = state.totalCartItems,
                totalAmount = state.totalCartPrice,
                orderNote = state.orderNote.trim(),
                statusId = state.currentOrderStatus.id
            )
        } else if (fallbackCategory != null && fallbackCategory.items.isNotEmpty()) {
            val firstItem = fallbackCategory.items.first()
            val qtySuffix = firstItem.qty?.let { " ($it)" } ?: ""
            OrderHistoryEntity(
                itemsSummary = "${fallbackCategory.name} Inquiry (${firstItem.name}$qtySuffix)",
                encodedItems = "${firstItem.id}:1",
                totalItemsCount = 1,
                totalAmount = firstItem.price,
                orderNote = "",
                statusId = state.currentOrderStatus.id
            )
        } else {
            null
        }

        if (entity != null) {
            viewModelScope.launch {
                repo.insert(entity)
            }
        }
    }

    fun recordSingleItemOrderToHistory(item: MenuItem) {
        val repo = orderHistoryRepository ?: return
        val qtySuffix = item.qty?.let { " ($it)" } ?: ""
        val itemNote = _uiState.value.itemInstructions[item.id]?.trim()?.takeIf { it.isNotEmpty() }
        val noteBracket = itemNote?.let { " [$it]" } ?: ""
        val entity = OrderHistoryEntity(
            itemsSummary = "${item.name}$qtySuffix × 1$noteBracket",
            encodedItems = "${item.id}:1",
            totalItemsCount = 1,
            totalAmount = item.price,
            orderNote = itemNote ?: "",
            statusId = _uiState.value.currentOrderStatus.id
        )
        viewModelScope.launch {
            repo.insert(entity)
        }
    }

    fun loadPastOrderIntoCart(order: OrderHistoryEntity) {
        val parsedMap = mutableMapOf<String, Int>()
        order.encodedItems.split("|").forEach { entry ->
            val parts = entry.split(":")
            if (parts.size == 2) {
                val itemId = parts[0]
                val count = parts[1].toIntOrNull() ?: 0
                if (count > 0 && FoodStopRepository.allItems.any { it.id == itemId }) {
                    parsedMap[itemId] = count
                }
            }
        }
        if (parsedMap.isNotEmpty()) {
            _uiState.update {
                it.copy(
                    cartQuantities = parsedMap,
                    orderNote = order.orderNote,
                    currentOrderStatus = OrderStatus.fromId(order.statusId),
                    isOrderSheetVisible = true
                )
            }
        }
    }

    fun cyclePastOrderStatus(order: OrderHistoryEntity) {
        val repo = orderHistoryRepository ?: return
        val nextStatus = OrderStatus.fromId(order.statusId).next()
        viewModelScope.launch {
            repo.updateStatus(order.id, nextStatus.id)
        }
    }

    fun deletePastOrder(orderId: Int) {
        val repo = orderHistoryRepository ?: return
        viewModelScope.launch {
            repo.deleteById(orderId)
        }
    }

    fun clearAllOrderHistory() {
        val repo = orderHistoryRepository ?: return
        viewModelScope.launch {
            repo.clearAll()
        }
    }

    fun buildWhatsAppUrlForPastOrder(order: OrderHistoryEntity): String {
        val message = buildString {
            appendLine("Hello *${FoodStopRepository.RESTAURANT_NAME}*! 👋")
            appendLine("I would like to reorder (#FS-${100 + order.id}):")
            appendLine()
            appendLine("• ${order.itemsSummary}")
            appendLine("*Total Amount: ₹${order.totalAmount}*")
            if (order.orderNote.isNotBlank()) {
                appendLine("Note: ${order.orderNote}")
            }
            append("Please confirm my order.")
        }
        return "${FoodStopRepository.WHATSAPP_BASE_URL}?text=${Uri.encode(message)}"
    }

    fun buildWhatsAppOrderUrl(fallbackCategory: MenuCategory? = null): String {
        val state = _uiState.value
        val selectedList = state.cartItemsDetailed

        val message = if (selectedList.isNotEmpty()) {
            buildString {
                appendLine("Hello *${FoodStopRepository.RESTAURANT_NAME}*! 👋")
                appendLine("I would like to place an order:")
                appendLine()
                selectedList.forEachIndexed { index, (item, count) ->
                    val qtySuffix = item.qty?.let { " ($it)" } ?: ""
                    val lineTotal = item.price * count
                    appendLine("${index + 1}. *${item.name}*$qtySuffix × $count — ₹$lineTotal (₹${item.price} each)")
                    val specificInstruction = state.itemInstructions[item.id]?.trim()
                    if (!specificInstruction.isNullOrEmpty()) {
                        appendLine("   ↳ _Instruction: ${specificInstruction}_")
                    }
                }
                appendLine()
                appendLine("*Total Amount: ₹${state.totalCartPrice}*")
                if (state.orderNote.isNotBlank()) {
                    appendLine("Order Note: ${state.orderNote.trim()}")
                }
                append("Please confirm my order.")
            }
        } else if (fallbackCategory != null) {
            val itemsPreview = fallbackCategory.items.joinToString("\n") { item ->
                val qtyInfo = item.qty?.let { " ($it)" } ?: ""
                "• ${item.name}$qtyInfo - ₹${item.price}"
            }
            "Hi ${FoodStopRepository.RESTAURANT_NAME}, I would like to order from ${fallbackCategory.name}:\n\n$itemsPreview"
        } else {
            "Hi ${FoodStopRepository.RESTAURANT_NAME}, I would like to place an order!"
        }

        val encoded = Uri.encode(message)
        return "${FoodStopRepository.WHATSAPP_BASE_URL}?text=$encoded"
    }

    fun buildSingleItemWhatsAppUrl(item: MenuItem): String {
        val state = _uiState.value
        val qtyInfo = item.qty?.let { " ($it)" } ?: ""
        val specificInstruction = state.itemInstructions[item.id]?.trim()
        val message = buildString {
            appendLine("Hello *${FoodStopRepository.RESTAURANT_NAME}*! 👋")
            appendLine("I would like to order:")
            appendLine("• *${item.name}*$qtyInfo — ₹${item.price}")
            if (!specificInstruction.isNullOrEmpty()) {
                appendLine("  ↳ _Instruction: ${specificInstruction}_")
            }
            append("Please confirm availability.")
        }
        return "${FoodStopRepository.WHATSAPP_BASE_URL}?text=${Uri.encode(message)}"
    }

    companion object {
        fun provideFactory(repository: OrderHistoryRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MenuViewModel(repository) as T
                }
            }
    }
}
