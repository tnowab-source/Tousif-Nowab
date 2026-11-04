package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.MenuCategory
import com.example.model.MenuItem
import com.example.ui.theme.CardEmeraldBottom
import com.example.ui.theme.CardEmeraldTop
import com.example.ui.theme.DarkTextOnGold
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LightChampagneGold
import com.example.ui.theme.LuxuryGold
import com.example.ui.theme.MutedGoldText
import com.example.ui.theme.PlayfairDisplayFontFamily
import com.example.ui.theme.SurfaceEmerald
import com.example.ui.theme.WarmCreamText

val FavoriteHeartPink = Color(0xFFFF5A79)

@Composable
fun MyFavoritesSection(
    favoritePairs: List<Pair<MenuCategory, MenuItem>>,
    favoriteItemIds: Set<String>,
    cartQuantities: Map<String, Int>,
    itemInstructions: Map<String, String> = emptyMap(),
    onItemInstructionChange: (String, String) -> Unit = { _, _ -> },
    onTogglePresetInstruction: (String, String) -> Unit = { _, _ -> },
    onToggleFavorite: (MenuItem) -> Unit,
    onIncrementItem: (MenuItem) -> Unit,
    onDecrementItem: (MenuItem) -> Unit,
    onDirectItemWhatsApp: (MenuItem) -> Unit,
    onAddAllFavoritesToCart: () -> Unit,
    onClearAllFavorites: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("my_favorites_section"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = FavoriteHeartPink,
                    modifier = Modifier.size(22.dp)
                )
                Column {
                    Text(
                        text = stringResource(R.string.my_favorites_count_title, favoritePairs.size),
                        fontFamily = PlayfairDisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = WarmCreamText
                    )
                    Text(
                        text = stringResource(R.string.my_favorites_subtitle),
                        fontFamily = InterFontFamily,
                        fontSize = 11.sp,
                        color = MutedGoldText
                    )
                }
            }

            if (favoritePairs.isNotEmpty()) {
                TextButton(
                    onClick = onClearAllFavorites,
                    modifier = Modifier.testTag("clear_all_favorites_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = null,
                        tint = LightChampagneGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.clear_favorites),
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = LightChampagneGold
                    )
                }
            }
        }

        if (favoritePairs.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = SurfaceEmerald.copy(alpha = 0.85f),
                border = BorderStroke(1.dp, LuxuryGold.copy(alpha = 0.28f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("my_favorites_empty_state")
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = LuxuryGold.copy(alpha = 0.75f),
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = stringResource(R.string.my_favorites_empty_title),
                        fontFamily = PlayfairDisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = WarmCreamText,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.my_favorites_empty_subtitle),
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        color = MutedGoldText,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = CardEmeraldBottom,
                border = BorderStroke(1.dp, LuxuryGold.copy(alpha = 0.35f)),
                shadowElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(CardEmeraldTop, CardEmeraldBottom)
                            )
                        )
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    favoritePairs.forEach { (category, item) ->
                        MenuItemRowCard(
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

                    HorizontalDivider(color = LuxuryGold.copy(alpha = 0.22f))

                    Button(
                        onClick = onAddAllFavoritesToCart,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LuxuryGold,
                            contentColor = DarkTextOnGold
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("add_all_favorites_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.add_all_favorites_to_cart),
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
