package com.example

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.FoodStopDatabase
import com.example.data.OrderHistoryRepository
import com.example.model.FoodStopRepository
import com.example.ui.components.CategoryCard
import com.example.ui.components.CategoryDetailSection
import com.example.ui.components.FavoriteHeartPink
import com.example.ui.components.FindUsAndQrFooterSection
import com.example.ui.components.FoodStopHeader
import com.example.ui.components.FullMenuSection
import com.example.ui.components.MyFavoritesSection
import com.example.ui.components.OrderHistorySection
import com.example.ui.components.OrderReviewBottomSheet
import com.example.ui.components.QrEnlargeModalDialog
import com.example.ui.components.StickyOrderNowBar
import com.example.ui.theme.DarkTextOnGold
import com.example.ui.theme.DeepJungleBlack
import com.example.ui.theme.FoodStopTheme
import com.example.ui.theme.HeaderEmeraldEnd
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LightChampagneGold
import com.example.ui.theme.LuxuryGold
import com.example.ui.theme.MutedGoldText
import com.example.ui.theme.PlayfairDisplayFontFamily
import com.example.ui.theme.RichEmeraldBg
import com.example.ui.theme.SurfaceEmerald
import com.example.ui.theme.WarmCreamText
import com.example.viewmodel.AppTab
import com.example.viewmodel.MenuViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(DeepJungleBlack.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(DeepJungleBlack.toArgb())
        )
        setContent {
            val appContext = LocalContext.current.applicationContext
            val repository = remember(appContext) {
                val db = FoodStopDatabase.getInstance(appContext)
                OrderHistoryRepository(db.orderHistoryDao(), db.favoriteItemDao())
            }
            val menuViewModel: MenuViewModel = viewModel(
                factory = MenuViewModel.provideFactory(repository)
            )
            FoodStopTheme {
                FoodStopApp(viewModel = menuViewModel)
            }
        }
    }
}

@Composable
fun FoodStopApp(
    viewModel: MenuViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val orderHistory by viewModel.orderHistory.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Handle system Back navigation when inside a category, active home search/filter, or non-home tab
    BackHandler(
        enabled = uiState.selectedCategoryId != null ||
            uiState.currentTab != AppTab.HOME ||
            uiState.searchQuery.isNotEmpty() ||
            uiState.activePriceFilter != null
    ) {
        when {
            uiState.selectedCategoryId != null -> viewModel.closeCategory()
            uiState.currentTab != AppTab.HOME -> viewModel.selectTab(AppTab.HOME)
            uiState.searchQuery.isNotEmpty() || uiState.activePriceFilter != null -> {
                viewModel.clearSearchAndFilters()
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = DeepJungleBlack,
        topBar = {
            FoodStopHeader(
                onLogoClick = {
                    viewModel.closeCategory()
                    viewModel.selectTab(AppTab.HOME)
                },
                onWhatsAppClick = {
                    viewModel.recordCurrentOrderToHistory(uiState.selectedCategory)
                    openExternalUrl(context, viewModel.buildWhatsAppOrderUrl())
                },
                onInstagramClick = {
                    openExternalUrl(context, FoodStopRepository.INSTAGRAM_URL)
                }
            )
        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                StickyOrderNowBar(
                    totalItems = uiState.totalCartItems,
                    totalPrice = uiState.totalCartPrice,
                    onReviewOrder = { viewModel.setOrderSheetVisible(true) },
                    onOrderNowWhatsApp = {
                        viewModel.recordCurrentOrderToHistory(uiState.selectedCategory)
                        val url = viewModel.buildWhatsAppOrderUrl(uiState.selectedCategory)
                        openExternalUrl(context, url)
                    }
                )

                NavigationBar(
                    containerColor = HeaderEmeraldEnd,
                    contentColor = WarmCreamText,
                    tonalElevation = 0.dp,
                    modifier = Modifier.testTag("bottom_navigation_bar")
                ) {
                    NavigationBarItem(
                        selected = uiState.currentTab == AppTab.HOME,
                        onClick = {
                            viewModel.closeCategory()
                            viewModel.selectTab(AppTab.HOME)
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = stringResource(R.string.nav_home)
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(R.string.nav_home),
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DarkTextOnGold,
                            selectedTextColor = LuxuryGold,
                            indicatorColor = LuxuryGold,
                            unselectedIconColor = MutedGoldText,
                            unselectedTextColor = MutedGoldText
                        ),
                        modifier = Modifier.testTag("nav_tab_home")
                    )

                    NavigationBarItem(
                        selected = uiState.currentTab == AppTab.FULL_MENU,
                        onClick = { viewModel.selectTab(AppTab.FULL_MENU) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.RestaurantMenu,
                                contentDescription = stringResource(R.string.nav_menu)
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(R.string.nav_menu),
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DarkTextOnGold,
                            selectedTextColor = LuxuryGold,
                            indicatorColor = LuxuryGold,
                            unselectedIconColor = MutedGoldText,
                            unselectedTextColor = MutedGoldText
                        ),
                        modifier = Modifier.testTag("nav_tab_menu")
                    )

                    NavigationBarItem(
                        selected = uiState.currentTab == AppTab.FAVORITES,
                        onClick = { viewModel.selectTab(AppTab.FAVORITES) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (uiState.favoriteItemIds.isNotEmpty()) {
                                        Badge(
                                            containerColor = FavoriteHeartPink,
                                            contentColor = Color.White
                                        ) {
                                            Text(text = uiState.favoriteItemIds.size.toString())
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = stringResource(R.string.nav_favorites)
                                )
                            }
                        },
                        label = {
                            Text(
                                text = stringResource(R.string.nav_favorites),
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DarkTextOnGold,
                            selectedTextColor = LuxuryGold,
                            indicatorColor = LuxuryGold,
                            unselectedIconColor = MutedGoldText,
                            unselectedTextColor = MutedGoldText
                        ),
                        modifier = Modifier.testTag("nav_tab_favorites")
                    )

                    NavigationBarItem(
                        selected = uiState.currentTab == AppTab.ORDER_HISTORY,
                        onClick = { viewModel.selectTab(AppTab.ORDER_HISTORY) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (orderHistory.isNotEmpty()) {
                                        Badge(
                                            containerColor = LuxuryGold,
                                            contentColor = DarkTextOnGold
                                        ) {
                                            Text(text = orderHistory.size.toString())
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = stringResource(R.string.nav_history)
                                )
                            }
                        },
                        label = {
                            Text(
                                text = stringResource(R.string.nav_history),
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DarkTextOnGold,
                            selectedTextColor = LuxuryGold,
                            indicatorColor = LuxuryGold,
                            unselectedIconColor = MutedGoldText,
                            unselectedTextColor = MutedGoldText
                        ),
                        modifier = Modifier.testTag("nav_tab_history")
                    )

                    NavigationBarItem(
                        selected = uiState.currentTab == AppTab.VISIT_AND_QR,
                        onClick = { viewModel.selectTab(AppTab.VISIT_AND_QR) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.QrCode2,
                                contentDescription = stringResource(R.string.nav_info)
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(R.string.nav_info),
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DarkTextOnGold,
                            selectedTextColor = LuxuryGold,
                            indicatorColor = LuxuryGold,
                            unselectedIconColor = MutedGoldText,
                            unselectedTextColor = MutedGoldText
                        ),
                        modifier = Modifier.testTag("nav_tab_info")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF163026), RichEmeraldBg, DeepJungleBlack)
                    )
                )
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 600.dp)
                    .testTag("main_scroll_container"),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    com.example.ui.components.OrderStatusIndicatorWidget(
                        currentStatus = uiState.currentOrderStatus,
                        totalCartItems = uiState.totalCartItems,
                        totalCartPrice = uiState.totalCartPrice,
                        onSelectStatus = { viewModel.setOrderStatus(it) },
                        onCycleStatus = { viewModel.cycleOrderStatus() }
                    )
                }

                item {
                    AnimatedContent(
                        targetState = Pair(uiState.currentTab, uiState.selectedCategoryId),
                        transitionSpec = {
                            (fadeIn(animationSpec = tween(260)) +
                                slideInVertically(animationSpec = tween(260)) { it / 12 })
                                .togetherWith(fadeOut(animationSpec = tween(180)))
                        },
                        label = "screen_transition"
                    ) { (tab, selectedCategoryId) ->
                        when (tab) {
                            AppTab.HOME -> {
                                val selectedCategory = selectedCategoryId?.let {
                                    FoodStopRepository.getCategoryById(it)
                                }
                                if (selectedCategory == null) {
                                    HomeScreenSection(
                                        searchQuery = uiState.searchQuery,
                                        activePriceFilter = uiState.activePriceFilter,
                                        dailySpecialPair = uiState.dailySpecialPair,
                                        isTodayDefault = uiState.dailySpecialOffset == 0,
                                        onShuffleSpecial = { viewModel.shuffleDailySpecial() },
                                        onResetToTodaySpecial = { viewModel.resetDailySpecialToToday() },
                                        favoritePairs = uiState.favoriteItemsWithCategory,
                                        favoriteItemIds = uiState.favoriteItemIds,
                                        itemInstructions = uiState.itemInstructions,
                                        onItemInstructionChange = { id, text ->
                                            viewModel.updateItemInstruction(id, text)
                                        },
                                        onTogglePresetInstruction = { id, preset ->
                                            viewModel.appendPresetInstruction(id, preset)
                                        },
                                        onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                                        onSelectPriceFilter = { viewModel.setPriceFilter(it) },
                                        onResetSearchAndFilters = { viewModel.clearSearchAndFilters() },
                                        onToggleFavorite = { viewModel.toggleFavorite(it.id) },
                                        onAddAllFavoritesToCart = { viewModel.addAllFavoritesToCart() },
                                        onClearAllFavorites = { viewModel.clearAllFavorites() },
                                        cartQuantities = uiState.cartQuantities,
                                        onOpenCategory = { viewModel.openCategory(it) },
                                        onIncrementItem = { viewModel.incrementItem(it) },
                                        onDecrementItem = { viewModel.decrementItem(it) },
                                        onDirectItemWhatsApp = { item ->
                                            viewModel.recordSingleItemOrderToHistory(item)
                                            openExternalUrl(
                                                context,
                                                viewModel.buildSingleItemWhatsAppUrl(item)
                                            )
                                        },
                                        onSwitchToFullMenu = { viewModel.selectTab(AppTab.FULL_MENU) }
                                    )
                                } else {
                                    CategoryDetailSection(
                                        category = selectedCategory,
                                        cartQuantities = uiState.cartQuantities,
                                        itemInstructions = uiState.itemInstructions,
                                        onItemInstructionChange = { id, text ->
                                            viewModel.updateItemInstruction(id, text)
                                        },
                                        onTogglePresetInstruction = { id, preset ->
                                            viewModel.appendPresetInstruction(id, preset)
                                        },
                                        favoriteItemIds = uiState.favoriteItemIds,
                                        onToggleFavorite = { viewModel.toggleFavorite(it.id) },
                                        onBackToCategories = { viewModel.closeCategory() },
                                        onIncrementItem = { viewModel.incrementItem(it) },
                                        onDecrementItem = { viewModel.decrementItem(it) },
                                        onDirectItemWhatsApp = { item ->
                                            viewModel.recordSingleItemOrderToHistory(item)
                                            openExternalUrl(
                                                context,
                                                viewModel.buildSingleItemWhatsAppUrl(item)
                                            )
                                        },
                                        onOrderCategoryWhatsApp = { category ->
                                            viewModel.recordCurrentOrderToHistory(category)
                                            openExternalUrl(
                                                context,
                                                viewModel.buildWhatsAppOrderUrl(category)
                                            )
                                        }
                                    )
                                }
                            }

                            AppTab.FULL_MENU -> {
                                FullMenuSection(
                                    searchQuery = uiState.searchQuery,
                                    activeCategoryFilterId = uiState.activeCategoryFilterId,
                                    activePriceFilter = uiState.activePriceFilter,
                                    dailySpecialPair = uiState.dailySpecialPair,
                                    isTodayDefault = uiState.dailySpecialOffset == 0,
                                    onShuffleSpecial = { viewModel.shuffleDailySpecial() },
                                    onResetToTodaySpecial = { viewModel.resetDailySpecialToToday() },
                                    onOpenCategory = { viewModel.openCategory(it) },
                                    cartQuantities = uiState.cartQuantities,
                                    itemInstructions = uiState.itemInstructions,
                                    onItemInstructionChange = { id, text ->
                                        viewModel.updateItemInstruction(id, text)
                                    },
                                    onTogglePresetInstruction = { id, preset ->
                                        viewModel.appendPresetInstruction(id, preset)
                                    },
                                    favoriteItemIds = uiState.favoriteItemIds,
                                    onToggleFavorite = { viewModel.toggleFavorite(it.id) },
                                    onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                                    onSelectCategoryFilter = { viewModel.setCategoryFilter(it) },
                                    onSelectPriceFilter = { viewModel.setPriceFilter(it) },
                                    onResetAllFilters = { viewModel.clearSearchAndFilters() },
                                    onIncrementItem = { viewModel.incrementItem(it) },
                                    onDecrementItem = { viewModel.decrementItem(it) },
                                    onDirectItemWhatsApp = { item ->
                                        viewModel.recordSingleItemOrderToHistory(item)
                                        openExternalUrl(
                                            context,
                                            viewModel.buildSingleItemWhatsAppUrl(item)
                                        )
                                    }
                                )
                            }

                            AppTab.FAVORITES -> {
                                MyFavoritesSection(
                                    favoritePairs = uiState.favoriteItemsWithCategory,
                                    favoriteItemIds = uiState.favoriteItemIds,
                                    cartQuantities = uiState.cartQuantities,
                                    itemInstructions = uiState.itemInstructions,
                                    onItemInstructionChange = { id, text ->
                                        viewModel.updateItemInstruction(id, text)
                                    },
                                    onTogglePresetInstruction = { id, preset ->
                                        viewModel.appendPresetInstruction(id, preset)
                                    },
                                    onToggleFavorite = { viewModel.toggleFavorite(it.id) },
                                    onIncrementItem = { viewModel.incrementItem(it) },
                                    onDecrementItem = { viewModel.decrementItem(it) },
                                    onDirectItemWhatsApp = { item ->
                                        viewModel.recordSingleItemOrderToHistory(item)
                                        openExternalUrl(
                                            context,
                                            viewModel.buildSingleItemWhatsAppUrl(item)
                                        )
                                    },
                                    onAddAllFavoritesToCart = { viewModel.addAllFavoritesToCart() },
                                    onClearAllFavorites = { viewModel.clearAllFavorites() }
                                )
                            }

                            AppTab.ORDER_HISTORY -> {
                                OrderHistorySection(
                                    orders = orderHistory,
                                    onReorderToCart = { pastOrder ->
                                        viewModel.loadPastOrderIntoCart(pastOrder)
                                    },
                                    onWhatsAppRepeatOrder = { pastOrder ->
                                        openExternalUrl(
                                            context,
                                            viewModel.buildWhatsAppUrlForPastOrder(pastOrder)
                                        )
                                    },
                                    onCycleOrderStatus = { pastOrder ->
                                        viewModel.cyclePastOrderStatus(pastOrder)
                                    },
                                    onDeleteOrder = { orderId ->
                                        viewModel.deletePastOrder(orderId)
                                    },
                                    onClearAllHistory = {
                                        viewModel.clearAllOrderHistory()
                                    }
                                )
                            }

                            AppTab.VISIT_AND_QR -> {
                                // Dedicated Restaurant Details & QR view
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text(
                                        text = FoodStopRepository.RESTAURANT_NAME,
                                        fontFamily = PlayfairDisplayFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 22.sp,
                                        color = WarmCreamText,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Text(
                                        text = stringResource(R.string.home_cooking_promise),
                                        fontFamily = InterFontFamily,
                                        fontSize = 13.sp,
                                        color = MutedGoldText,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }

                // Always include Restaurant Details (Google Maps) & QR Codes footer section
                item {
                    FindUsAndQrFooterSection(
                        onOpenDirections = {
                            openExternalUrl(context, FoodStopRepository.GOOGLE_MAPS_DIRECTIONS_URL)
                        },
                        onOpenWhatsAppDirect = {
                            viewModel.recordCurrentOrderToHistory(uiState.selectedCategory)
                            openExternalUrl(context, viewModel.buildWhatsAppOrderUrl())
                        },
                        onOpenInstagramDirect = {
                            openExternalUrl(context, FoodStopRepository.INSTAGRAM_URL)
                        },
                        onShowQrModal = { modalData ->
                            viewModel.showQrModal(modalData)
                        }
                    )
                }
            }
        }
    }

    // Order review bottom sheet
    if (uiState.isOrderSheetVisible && uiState.totalCartItems > 0) {
        OrderReviewBottomSheet(
            cartItemsDetailed = uiState.cartItemsDetailed,
            totalPrice = uiState.totalCartPrice,
            itemInstructions = uiState.itemInstructions,
            onItemInstructionChange = { id, text -> viewModel.updateItemInstruction(id, text) },
            onToggleItemPreset = { id, preset -> viewModel.appendPresetInstruction(id, preset) },
            orderNote = uiState.orderNote,
            onOrderNoteChange = { viewModel.updateOrderNote(it) },
            onIncrementItem = { viewModel.incrementItem(it) },
            onDecrementItem = { viewModel.decrementItem(it) },
            onClearCart = { viewModel.clearCart() },
            onDismiss = { viewModel.setOrderSheetVisible(false) },
            onConfirmWhatsAppOrder = {
                viewModel.recordCurrentOrderToHistory()
                val url = viewModel.buildWhatsAppOrderUrl()
                viewModel.setOrderSheetVisible(false)
                openExternalUrl(context, url)
            }
        )
    }

    // Enlarge QR code modal dialog
    uiState.activeQrModal?.let { qrModal ->
        QrEnlargeModalDialog(
            modalData = qrModal,
            onDismiss = { viewModel.showQrModal(null) },
            onOpenUrl = { url -> openExternalUrl(context, url) }
        )
    }
}

@Composable
private fun HomeScreenSection(
    searchQuery: String,
    activePriceFilter: com.example.model.PriceRangeFilter?,
    dailySpecialPair: Pair<com.example.model.MenuCategory, com.example.model.MenuItem>,
    isTodayDefault: Boolean,
    onShuffleSpecial: () -> Unit,
    onResetToTodaySpecial: () -> Unit,
    favoritePairs: List<Pair<com.example.model.MenuCategory, com.example.model.MenuItem>>,
    favoriteItemIds: Set<String>,
    itemInstructions: Map<String, String>,
    onItemInstructionChange: (String, String) -> Unit,
    onTogglePresetInstruction: (String, String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSelectPriceFilter: (com.example.model.PriceRangeFilter?) -> Unit,
    onResetSearchAndFilters: () -> Unit,
    onToggleFavorite: (com.example.model.MenuItem) -> Unit,
    onAddAllFavoritesToCart: () -> Unit,
    onClearAllFavorites: () -> Unit,
    cartQuantities: Map<String, Int>,
    onOpenCategory: (String) -> Unit,
    onIncrementItem: (com.example.model.MenuItem) -> Unit,
    onDecrementItem: (com.example.model.MenuItem) -> Unit,
    onDirectItemWhatsApp: (com.example.model.MenuItem) -> Unit,
    onSwitchToFullMenu: () -> Unit
) {
    val categories = FoodStopRepository.categories
    val normalizedQuery = searchQuery.trim().lowercase()
    val isFilteringActive = normalizedQuery.isNotEmpty() || activePriceFilter != null

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Search Bar at the top of the Home Screen
        androidx.compose.material3.OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.search_menu_placeholder),
                    fontFamily = InterFontFamily,
                    fontSize = 13.sp,
                    color = MutedGoldText.copy(alpha = 0.65f)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = LuxuryGold
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    androidx.compose.material3.IconButton(
                        onClick = { onSearchQueryChange("") },
                        modifier = Modifier.testTag("home_search_clear_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(R.string.clear_search),
                            tint = LuxuryGold
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                focusedBorderColor = LuxuryGold,
                unfocusedBorderColor = LuxuryGold.copy(alpha = 0.4f),
                focusedContainerColor = SurfaceEmerald,
                unfocusedContainerColor = com.example.ui.theme.SurfaceEmeraldDark,
                focusedTextColor = WarmCreamText,
                unfocusedTextColor = com.example.ui.theme.SoftCreamText,
                cursorColor = LuxuryGold
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("home_search_input")
        )

        // Price Range Filter Bar (All, Budget, Standard, Premium)
        com.example.ui.components.PriceRangeFilterBar(
            activePriceFilter = activePriceFilter,
            onSelectPriceFilter = onSelectPriceFilter
        )

        if (isFilteringActive) {
            // Filter menu items by name and price range across all categories
            val matchingPairs = categories.flatMap { category ->
                category.items
                    .filter { item ->
                        val matchesQuery = normalizedQuery.isEmpty() ||
                            item.name.lowercase().contains(normalizedQuery)
                        val matchesPrice = activePriceFilter == null ||
                            activePriceFilter.matches(item.price)
                        matchesQuery && matchesPrice
                    }
                    .map { item -> category to item }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_search_results_section"),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.search_results_heading, matchingPairs.size),
                        fontFamily = PlayfairDisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = WarmCreamText
                    )

                    Surface(
                        onClick = onResetSearchAndFilters,
                        shape = RoundedCornerShape(50),
                        color = LuxuryGold.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, LuxuryGold.copy(alpha = 0.35f)),
                        modifier = Modifier.testTag("home_reset_filters_button")
                    ) {
                        Text(
                            text = stringResource(R.string.reset_filters),
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            color = LightChampagneGold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                }

                if (matchingPairs.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = SurfaceEmerald.copy(alpha = 0.85f),
                        border = BorderStroke(1.dp, LuxuryGold.copy(alpha = 0.28f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_search_empty_state")
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.no_search_results_title),
                                fontFamily = PlayfairDisplayFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = WarmCreamText,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = stringResource(R.string.no_search_results_subtitle),
                                fontFamily = InterFontFamily,
                                fontSize = 12.sp,
                                color = MutedGoldText,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    matchingPairs.forEach { (category, item) ->
                        com.example.ui.components.MenuItemRowCard(
                            item = item,
                            category = category,
                            quantityInCart = cartQuantities[item.id] ?: 0,
                            itemInstruction = itemInstructions[item.id] ?: "",
                            onItemInstructionChange = { onItemInstructionChange(item.id, it) },
                            onTogglePresetInstruction = { onTogglePresetInstruction(item.id, it) },
                            isFavorite = item.id in favoriteItemIds,
                            onToggleFavorite = { onToggleFavorite(item) },
                            onIncrement = { onIncrementItem(item) },
                            onDecrement = { onDecrementItem(item) },
                            onDirectWhatsAppOrder = { onDirectItemWhatsApp(item) }
                        )
                    }
                }
            }
        } else {
            // Daily Special Highlight Section at the top of the Home Menu
            val (_, specialItem) = dailySpecialPair
            com.example.ui.components.DailySpecialHighlightCard(
                dailySpecialPair = dailySpecialPair,
                isTodayDefault = isTodayDefault,
                quantityInCart = cartQuantities[specialItem.id] ?: 0,
                itemInstruction = itemInstructions[specialItem.id] ?: "",
                onItemInstructionChange = { onItemInstructionChange(specialItem.id, it) },
                onTogglePresetInstruction = { onTogglePresetInstruction(specialItem.id, it) },
                isFavorite = specialItem.id in favoriteItemIds,
                onToggleFavorite = onToggleFavorite,
                onIncrementItem = onIncrementItem,
                onDecrementItem = onDecrementItem,
                onDirectWhatsAppOrder = onDirectItemWhatsApp,
                onOpenCategory = onOpenCategory,
                onShuffleSpecial = onShuffleSpecial,
                onResetToTodaySpecial = onResetToTodaySpecial
            )

            // Dedicated My Favorites section on Home screen when user has favorited dishes
            if (favoritePairs.isNotEmpty()) {
                MyFavoritesSection(
                    favoritePairs = favoritePairs,
                    favoriteItemIds = favoriteItemIds,
                    cartQuantities = cartQuantities,
                    itemInstructions = itemInstructions,
                    onItemInstructionChange = onItemInstructionChange,
                    onTogglePresetInstruction = onTogglePresetInstruction,
                    onToggleFavorite = onToggleFavorite,
                    onIncrementItem = onIncrementItem,
                    onDecrementItem = onDecrementItem,
                    onDirectItemWhatsApp = onDirectItemWhatsApp,
                    onAddAllFavoritesToCart = onAddAllFavoritesToCart,
                    onClearAllFavorites = onClearAllFavorites
                )
            }

            // Hero Header ("AUTHENTIC • HOMEMADE", "Select a Category", "Tap to explore...")
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(LuxuryGold.copy(alpha = 0.14f))
                        .border(1.dp, LuxuryGold.copy(alpha = 0.35f), RoundedCornerShape(50))
                        .padding(horizontal = 14.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = stringResource(R.string.hero_badge_authentic),
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp,
                        letterSpacing = 2.sp,
                        color = LuxuryGold
                    )
                }

                Text(
                    text = stringResource(R.string.hero_heading_categories),
                    fontFamily = PlayfairDisplayFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    letterSpacing = 0.8.sp,
                    color = WarmCreamText,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = stringResource(R.string.hero_subheading_explore),
                    fontFamily = InterFontFamily,
                    fontSize = 12.sp,
                    color = MutedGoldText.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )
            }

            // 2x2 Category Grid matching the HTML 2-column grid
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                categories.chunked(2).forEach { rowCategories ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        rowCategories.forEach { category ->
                            val selectedInCat = category.items.sumOf { cartQuantities[it.id] ?: 0 }
                            CategoryCard(
                                category = category,
                                selectedCountInCategory = selectedInCat,
                                onClick = { onOpenCategory(category.id) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowCategories.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // Tagline Banner + View Full Menu & Prices button
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SurfaceEmerald.copy(alpha = 0.8f),
                border = BorderStroke(1.dp, LuxuryGold.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.home_cooking_promise),
                        fontFamily = InterFontFamily,
                        fontStyle = FontStyle.Italic,
                        fontSize = 12.sp,
                        color = MutedGoldText,
                        textAlign = TextAlign.Center
                    )

                    Surface(
                        onClick = onSwitchToFullMenu,
                        shape = RoundedCornerShape(50),
                        color = LuxuryGold.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, LuxuryGold.copy(alpha = 0.55f)),
                        modifier = Modifier.testTag("browse_full_menu_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.RestaurantMenu,
                                contentDescription = null,
                                tint = LuxuryGold,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.view_full_menu),
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = LightChampagneGold
                            )
                        }
                    }
                }
            }
        }
    }
}

fun openExternalUrl(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "Opening: $url", Toast.LENGTH_SHORT).show()
    }
}
