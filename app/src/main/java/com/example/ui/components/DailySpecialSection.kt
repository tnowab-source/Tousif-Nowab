package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.FoodStopRepository
import com.example.model.MenuCategory
import com.example.model.MenuItem
import com.example.ui.theme.BrightGold
import com.example.ui.theme.CardEmeraldBottom
import com.example.ui.theme.CardEmeraldTop
import com.example.ui.theme.DarkTextOnGold
import com.example.ui.theme.HeaderEmeraldStart
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LightChampagneGold
import com.example.ui.theme.LuxuryGold
import com.example.ui.theme.MutedGoldText
import com.example.ui.theme.PlayfairDisplayFontFamily
import com.example.ui.theme.SoftCreamText
import com.example.ui.theme.WarmCreamText
import com.example.ui.theme.WhatsAppGreenLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DailySpecialHighlightCard(
    dailySpecialPair: Pair<MenuCategory, MenuItem>,
    isTodayDefault: Boolean,
    quantityInCart: Int,
    itemInstruction: String,
    onItemInstructionChange: (String) -> Unit,
    onTogglePresetInstruction: (String) -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: (MenuItem) -> Unit,
    onIncrementItem: (MenuItem) -> Unit,
    onDecrementItem: (MenuItem) -> Unit,
    onDirectWhatsAppOrder: (MenuItem) -> Unit,
    onOpenCategory: (String) -> Unit,
    onShuffleSpecial: () -> Unit,
    onResetToTodaySpecial: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val (category, item) = dailySpecialPair
    val chooserTitle = stringResource(R.string.share_chooser_title, item.name)
    val formattedToday = remember {
        SimpleDateFormat("EEEE, MMM d", Locale.ENGLISH).format(Date())
    }
    val highlightDescription = remember(item.id) {
        FoodStopRepository.getDailySpecialHighlightNote(item.id)
    }
    val goldGradient = Brush.linearGradient(
        colors = listOf(LuxuryGold, LightChampagneGold, BrightGold)
    )

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = CardEmeraldBottom,
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.linearGradient(
                colors = listOf(BrightGold, LuxuryGold.copy(alpha = 0.55f), BrightGold)
            )
        ),
        shadowElevation = 10.dp,
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_special_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            HeaderEmeraldStart,
                            CardEmeraldTop,
                            CardEmeraldBottom
                        )
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Top Row: Badge + Date + Shuffle / Reset Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(goldGradient)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = DarkTextOnGold,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = if (isTodayDefault) {
                                    stringResource(R.string.daily_special_badge)
                                } else {
                                    stringResource(R.string.daily_special_chef_pick)
                                },
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp,
                                letterSpacing = 0.8.sp,
                                color = DarkTextOnGold
                            )
                        }
                    }

                    Text(
                        text = "• $formattedToday",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp,
                        color = LightChampagneGold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (!isTodayDefault) {
                        Surface(
                            onClick = onResetToTodaySpecial,
                            shape = RoundedCornerShape(50),
                            color = LuxuryGold.copy(alpha = 0.14f),
                            border = BorderStroke(1.dp, LuxuryGold.copy(alpha = 0.4f)),
                            modifier = Modifier.testTag("daily_special_reset_button")
                        ) {
                            Text(
                                text = stringResource(R.string.daily_special_reset_today),
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp,
                                color = LightChampagneGold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Surface(
                        onClick = onShuffleSpecial,
                        shape = RoundedCornerShape(50),
                        color = LuxuryGold.copy(alpha = 0.18f),
                        border = BorderStroke(1.dp, LuxuryGold.copy(alpha = 0.45f)),
                        modifier = Modifier.testTag("daily_special_shuffle_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = stringResource(R.string.daily_special_try_another),
                                tint = LuxuryGold,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = stringResource(R.string.daily_special_try_another),
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = LuxuryGold
                            )
                        }
                    }
                }
            }

            // Main Spotlight Content: Dish Photo + Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Cropped Dish Visual with Category Tag
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.5.dp, LuxuryGold.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                        .clickable { onOpenCategory(category.id) }
                        .testTag("daily_special_image")
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_food_collage),
                        contentDescription = item.name,
                        contentScale = ContentScale.Crop,
                        alignment = BiasAlignment(category.cropBiasX, category.cropBiasY),
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(1.35f)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color(0xFF06110C).copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(horizontal = 7.dp, vertical = 5.dp)
                    ) {
                        Text(text = category.iconEmoji, fontSize = 12.sp)
                        Text(
                            text = category.name,
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            color = LightChampagneGold
                        )
                    }
                }

                // Dish Title, Badges, Description, and Countdown
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = item.name,
                            fontFamily = PlayfairDisplayFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = WarmCreamText,
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .testTag("daily_special_dish_name")
                        )

                        item.qty?.let { qtyText ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(LuxuryGold.copy(alpha = 0.22f))
                                    .border(
                                        0.8.dp,
                                        LuxuryGold.copy(alpha = 0.45f),
                                        RoundedCornerShape(5.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = qtyText,
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 10.sp,
                                    color = LightChampagneGold
                                )
                            }
                        }
                    }

                    item.badge?.let { badgeType ->
                        val isBestSeller = badgeType == com.example.model.MenuBadge.BEST_SELLER
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(
                                    if (isBestSeller) {
                                        Brush.horizontalGradient(
                                            colors = listOf(LuxuryGold, LightChampagneGold, BrightGold)
                                        )
                                    } else {
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                LuxuryGold.copy(alpha = 0.22f),
                                                LuxuryGold.copy(alpha = 0.14f)
                                            )
                                        )
                                    }
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isBestSeller) BrightGold else LuxuryGold.copy(alpha = 0.55f),
                                    shape = RoundedCornerShape(50)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isBestSeller) {
                                    stringResource(R.string.badge_best_seller)
                                } else {
                                    stringResource(R.string.badge_popular)
                                },
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 9.sp,
                                letterSpacing = 0.7.sp,
                                color = if (isBestSeller) DarkTextOnGold else LightChampagneGold
                            )
                        }
                    }

                    Text(
                        text = highlightDescription,
                        fontFamily = InterFontFamily,
                        fontStyle = FontStyle.Italic,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = SoftCreamText.copy(alpha = 0.9f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (item.isFreshlyMade) {
                        FreshlyMadeCountdownIndicator(
                            itemId = "daily_${item.id}",
                            prepTimeMinutes = item.prepTimeMinutes,
                            isActiveInOrder = quantityInCart > 0
                        )
                    }
                }
            }

            HorizontalDivider(color = LuxuryGold.copy(alpha = 0.22f))

            // Bottom Action Bar: Price + Favorite + Share + WhatsApp + Add/Quantity Stepper
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SPECIAL PRICE",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        letterSpacing = 1.sp,
                        color = MutedGoldText
                    )
                    Text(
                        text = "₹${item.price}",
                        fontFamily = PlayfairDisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = LuxuryGold,
                        modifier = Modifier.testTag("daily_special_price")
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Favorite toggle
                    IconButton(
                        onClick = { onToggleFavorite(item) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (isFavorite) Color(0xFFFF5A79).copy(alpha = 0.18f)
                                else LuxuryGold.copy(alpha = 0.10f)
                            )
                            .border(
                                1.dp,
                                if (isFavorite) Color(0xFFFF5A79).copy(alpha = 0.6f)
                                else LuxuryGold.copy(alpha = 0.3f),
                                CircleShape
                            )
                            .testTag("daily_special_fav_button")
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = stringResource(R.string.toggle_favorite_desc),
                            tint = if (isFavorite) Color(0xFFFF5A79) else LuxuryGold,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Social Share button
                    IconButton(
                        onClick = { shareMenuItemWithFriends(context, item, chooserTitle) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(LuxuryGold.copy(alpha = 0.12f))
                            .border(1.dp, LuxuryGold.copy(alpha = 0.35f), CircleShape)
                            .testTag("daily_special_share_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = stringResource(R.string.share_dish_desc),
                            tint = LightChampagneGold,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Quick WhatsApp order button for the Daily Special
                    IconButton(
                        onClick = { onDirectWhatsAppOrder(item) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(WhatsAppGreenLight.copy(alpha = 0.18f))
                            .border(1.dp, WhatsAppGreenLight.copy(alpha = 0.55f), CircleShape)
                            .testTag("daily_special_whatsapp_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = stringResource(R.string.daily_special_order_whatsapp),
                            tint = WhatsAppGreenLight,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Add Special / Stepper
                    if (quantityInCart == 0) {
                        Button(
                            onClick = { onIncrementItem(item) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LuxuryGold,
                                contentColor = DarkTextOnGold
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            modifier = Modifier
                                .height(38.dp)
                                .testTag("daily_special_add_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.daily_special_add_button),
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(LuxuryGold)
                                .height(38.dp)
                                .padding(horizontal = 4.dp)
                        ) {
                            IconButton(
                                onClick = { onDecrementItem(item) },
                                modifier = Modifier
                                    .size(28.dp)
                                    .testTag("daily_special_dec_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = stringResource(R.string.decrease_qty),
                                    tint = DarkTextOnGold,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = quantityInCart.toString(),
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = DarkTextOnGold,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )
                            IconButton(
                                onClick = { onIncrementItem(item) },
                                modifier = Modifier
                                    .size(28.dp)
                                    .testTag("daily_special_inc_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = stringResource(R.string.increase_qty),
                                    tint = DarkTextOnGold,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Per-item instruction field when the Daily Special is in the cart
            AnimatedVisibility(visible = quantityInCart > 0) {
                Column(modifier = Modifier.padding(top = 4.dp)) {
                    HorizontalDivider(
                        color = LuxuryGold.copy(alpha = 0.18f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    ItemSpecificInstructionField(
                        itemId = item.id,
                        instruction = itemInstruction,
                        onInstructionChange = onItemInstructionChange,
                        onTogglePreset = onTogglePresetInstruction,
                        testTagPrefix = "daily_special_instruction"
                    )
                }
            }
        }
    }
}
