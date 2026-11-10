package com.example.model

enum class MenuBadge {
    BEST_SELLER,
    POPULAR
}

enum class PriceRangeFilter(val id: String, val minPrice: Int, val maxPrice: Int) {
    BUDGET("budget", 0, 65),
    STANDARD("standard", 66, 95),
    PREMIUM("premium", 96, Int.MAX_VALUE);

    fun matches(price: Int): Boolean = price in minPrice..maxPrice
}

data class MenuItem(
    val id: String,
    val categoryId: String,
    val categoryName: String,
    val name: String,
    val qty: String? = null,
    val price: Int,
    val badge: MenuBadge? = null,
    val isFreshlyMade: Boolean = true,
    val prepTimeMinutes: Int = 12
)

data class MenuCategory(
    val id: String,
    val name: String,
    val iconEmoji: String,
    val cropBiasX: Float,
    val cropBiasY: Float,
    val items: List<MenuItem>
)

object FoodStopRepository {
    const val RESTAURANT_NAME = "FOOD STOP – HOME KITCHEN"
    const val WHATSAPP_NUMBER_DISPLAY = "8100375680"
    const val WHATSAPP_NUMBER_INTL = "918100375680"
    const val WHATSAPP_BASE_URL = "https://wa.me/918100375680"
    const val INSTAGRAM_HANDLE_DISPLAY = "@FOOD.09674"
    const val INSTAGRAM_HANDLE_CLEAN = "FOOD.09674"
    const val INSTAGRAM_URL = "https://www.instagram.com/food.09674/"
    const val ADDRESS_LINE_1 = "Subhashgram, Panchghara"
    const val ADDRESS_LINE_2 = "Kolkata - 700147, West Bengal"
    const val ADDRESS_FULL = "Subhashgram, Panchghara, Kolkata - 700147"
    const val KITCHEN_LATITUDE = 22.4192
    const val KITCHEN_LONGITUDE = 88.4312
    const val GOOGLE_MAPS_EMBED_URL =
        "https://maps.google.com/maps?q=Subhashgram,%20Panchghara,%20Kolkata%20-%20700147&t=&z=15&ie=UTF8&iwloc=&output=embed"
    const val GOOGLE_MAPS_DIRECTIONS_URL =
        "https://www.google.com/maps/search/?api=1&query=Subhashgram+Panchghara+Kolkata+700147"

    // Exact menu categories, items, quantities, and prices from the uploaded HTML file
    val categories: List<MenuCategory> = listOf(
        MenuCategory(
            id = "momo",
            name = "MOMO'S",
            iconEmoji = "🥟",
            // CSS crop: "75% 16%" -> biasX = (75/50)-1 = 0.5f, biasY = (16/50)-1 = -0.68f
            cropBiasX = 0.50f,
            cropBiasY = -0.68f,
            items = listOf(
                MenuItem(
                    id = "momo_chicken_steam",
                    categoryId = "momo",
                    categoryName = "MOMO'S",
                    name = "Chicken Steam Momo",
                    qty = "8pcs",
                    price = 80,
                    badge = MenuBadge.BEST_SELLER,
                    prepTimeMinutes = 12
                ),
                MenuItem(
                    id = "momo_veg_steam",
                    categoryId = "momo",
                    categoryName = "MOMO'S",
                    name = "Veg Steam Momo",
                    qty = "8pcs",
                    price = 50,
                    prepTimeMinutes = 10
                ),
                MenuItem(
                    id = "momo_chicken_fried",
                    categoryId = "momo",
                    categoryName = "MOMO'S",
                    name = "Chicken Fried Momo",
                    qty = "8pcs",
                    price = 100,
                    prepTimeMinutes = 14
                ),
                MenuItem(
                    id = "momo_veg_fried",
                    categoryId = "momo",
                    categoryName = "MOMO'S",
                    name = "Veg Fried Momo",
                    qty = "8pcs",
                    price = 80,
                    prepTimeMinutes = 12
                ),
                MenuItem(
                    id = "momo_pan_fried_chicken",
                    categoryId = "momo",
                    categoryName = "MOMO'S",
                    name = "Pan Fried Chicken Momo",
                    qty = "8pcs",
                    price = 120,
                    badge = MenuBadge.POPULAR,
                    prepTimeMinutes = 15
                ),
                MenuItem(
                    id = "momo_pan_fried_veg",
                    categoryId = "momo",
                    categoryName = "MOMO'S",
                    name = "Pan Fried Veg Momo",
                    qty = "8pcs",
                    price = 100,
                    prepTimeMinutes = 14
                )
            )
        ),
        MenuCategory(
            id = "sandwich",
            name = "SANDWICH'S",
            iconEmoji = "🥪",
            // CSS crop: "78% 38%" -> biasX = (78/50)-1 = 0.56f, biasY = (38/50)-1 = -0.24f
            cropBiasX = 0.56f,
            cropBiasY = -0.24f,
            items = listOf(
                MenuItem(
                    id = "sandwich_egg",
                    categoryId = "sandwich",
                    categoryName = "SANDWICH'S",
                    name = "Egg Sandwich",
                    qty = null,
                    price = 50,
                    prepTimeMinutes = 8
                ),
                MenuItem(
                    id = "sandwich_chicken",
                    categoryId = "sandwich",
                    categoryName = "SANDWICH'S",
                    name = "Chicken Sandwich",
                    qty = null,
                    price = 70,
                    prepTimeMinutes = 10
                ),
                MenuItem(
                    id = "sandwich_corn_cheese",
                    categoryId = "sandwich",
                    categoryName = "SANDWICH'S",
                    name = "Corn Cheese Sandwich",
                    qty = null,
                    price = 70,
                    badge = MenuBadge.POPULAR,
                    prepTimeMinutes = 10
                ),
                MenuItem(
                    id = "sandwich_chicken_cheese",
                    categoryId = "sandwich",
                    categoryName = "SANDWICH'S",
                    name = "Chicken Cheese Sandwich",
                    qty = null,
                    price = 90,
                    badge = MenuBadge.BEST_SELLER,
                    prepTimeMinutes = 12
                ),
                MenuItem(
                    id = "sandwich_egg_cheese",
                    categoryId = "sandwich",
                    categoryName = "SANDWICH'S",
                    name = "Egg Cheese Sandwich",
                    qty = null,
                    price = 60,
                    prepTimeMinutes = 9
                )
            )
        ),
        MenuCategory(
            id = "burger",
            name = "BURGER'S",
            iconEmoji = "🍔",
            // CSS crop: "58% 64%" -> biasX = (58/50)-1 = 0.16f, biasY = (64/50)-1 = 0.28f
            cropBiasX = 0.16f,
            cropBiasY = 0.28f,
            items = listOf(
                MenuItem(
                    id = "burger_egg",
                    categoryId = "burger",
                    categoryName = "BURGER'S",
                    name = "Egg Burger",
                    qty = null,
                    price = 50,
                    prepTimeMinutes = 10
                ),
                MenuItem(
                    id = "burger_egg_cheese",
                    categoryId = "burger",
                    categoryName = "BURGER'S",
                    name = "Egg Cheese Burger",
                    qty = null,
                    price = 60,
                    badge = MenuBadge.POPULAR,
                    prepTimeMinutes = 11
                ),
                MenuItem(
                    id = "burger_chicken",
                    categoryId = "burger",
                    categoryName = "BURGER'S",
                    name = "Chicken Burger",
                    qty = null,
                    price = 70,
                    prepTimeMinutes = 12
                ),
                MenuItem(
                    id = "burger_chicken_cheese",
                    categoryId = "burger",
                    categoryName = "BURGER'S",
                    name = "Chicken Cheese Burger",
                    qty = null,
                    price = 90,
                    badge = MenuBadge.BEST_SELLER,
                    prepTimeMinutes = 14
                )
            )
        ),
        MenuCategory(
            id = "biryani",
            name = "BIRYANI'S",
            iconEmoji = "🍛",
            // CSS crop: "38% 86%" -> biasX = (38/50)-1 = -0.24f, biasY = (86/50)-1 = 0.72f
            cropBiasX = -0.24f,
            cropBiasY = 0.72f,
            items = listOf(
                MenuItem(
                    id = "biryani_egg",
                    categoryId = "biryani",
                    categoryName = "BIRYANI'S",
                    name = "Egg Biryani",
                    qty = null,
                    price = 90,
                    prepTimeMinutes = 15
                ),
                MenuItem(
                    id = "biryani_chicken",
                    categoryId = "biryani",
                    categoryName = "BIRYANI'S",
                    name = "Chicken Biryani",
                    qty = null,
                    price = 130,
                    badge = MenuBadge.BEST_SELLER,
                    prepTimeMinutes = 18
                )
            )
        )
    )

    val allItems: List<MenuItem> = categories.flatMap { it.items }

    fun getCategoryById(id: String): MenuCategory? = categories.find { it.id == id }
}
