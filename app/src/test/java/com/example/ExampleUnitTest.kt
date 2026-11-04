package com.example

import com.example.model.FoodStopRepository
import com.example.util.QrCodeGenerator
import com.example.viewmodel.MenuViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun verifyMenuCategoriesAndPrices() {
        val categories = FoodStopRepository.categories
        assertEquals(4, categories.size)
        assertEquals(
            listOf("MOMO'S", "SANDWICH'S", "BURGER'S", "BIRYANI'S"),
            categories.map { it.name }
        )
        assertEquals(17, FoodStopRepository.allItems.size)

        val momo = FoodStopRepository.allItems.first { it.name == "Chicken Steam Momo" }
        assertEquals("8pcs", momo.qty)
        assertEquals(80, momo.price)

        val biryani = FoodStopRepository.allItems.first { it.name == "Chicken Biryani" }
        assertEquals(130, biryani.price)
    }

    @Test
    fun verifyCartCalculationsAndQrGenerator() {
        val viewModel = MenuViewModel()
        val momoItem = FoodStopRepository.allItems.first { it.name == "Chicken Steam Momo" }
        val biryaniItem = FoodStopRepository.allItems.first { it.name == "Chicken Biryani" }

        viewModel.incrementItem(momoItem)
        viewModel.incrementItem(momoItem)
        viewModel.incrementItem(biryaniItem)

        assertEquals(3, viewModel.uiState.value.totalCartItems)
        assertEquals(290, viewModel.uiState.value.totalCartPrice)

        val waQr = QrCodeGenerator.encode(FoodStopRepository.WHATSAPP_BASE_URL)
        val igQr = QrCodeGenerator.encode(FoodStopRepository.INSTAGRAM_URL)
        assertEquals(25, waQr.size)
        assertEquals(29, igQr.size)
        assertTrue(waQr[0][0])
        assertTrue(igQr[0][0])
    }

    @Test
    fun verifyDailySpecialRotation() {
        val uniqueItemsAcrossCycle = (1..17).map { day ->
            FoodStopRepository.getDailySpecial(day).second.id
        }.toSet()
        assertEquals(17, uniqueItemsAcrossCycle.size)

        val viewModel = MenuViewModel()
        val initialSpecialId = viewModel.uiState.value.dailySpecialPair.second.id
        viewModel.shuffleDailySpecial()
        val nextSpecialId = viewModel.uiState.value.dailySpecialPair.second.id
        assertTrue(initialSpecialId != nextSpecialId)
        viewModel.resetDailySpecialToToday()
        assertEquals(initialSpecialId, viewModel.uiState.value.dailySpecialPair.second.id)
    }
}

