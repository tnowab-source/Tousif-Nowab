package com.example.ui.components

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.example.ui.theme.DeepJungleBlack
import com.example.ui.theme.HeaderEmeraldStart
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LightChampagneGold
import com.example.ui.theme.LuxuryGold
import com.example.ui.theme.MenuRowBg
import com.example.ui.theme.MutedGoldText
import com.example.ui.theme.PlayfairDisplayFontFamily
import com.example.ui.theme.RichEmeraldBg
import com.example.ui.theme.SoftCreamText
import com.example.ui.theme.SurfaceEmerald
import com.example.ui.theme.SurfaceEmeraldDark
import com.example.ui.theme.WarmCreamText
import com.example.ui.theme.WhatsAppGreenDark
import com.example.ui.theme.WhatsAppGreenLight

@Composable
fun CategoryCard(
    category: MenuCategory,
    selectedCountInCategory: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = CardEmeraldBottom,
        border = BorderStroke(1.dp, LuxuryGold.copy(alpha = 0.35f)),
        shadowElevation = 8.dp,
        modifier = modifier
            .aspectRatio(0.88f)
            .testTag("category_card_${category.id}")
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Original food photo cropped with exact CSS object-position & scale(1.25)
            Image(
                painter = painterResource(id = R.drawable.img_food_collage),
                contentDescription = category.name,
                contentScale = ContentScale.Crop,
                alignment = BiasAlignment(category.cropBiasX, category.cropBiasY),
                modifier = Modifier
                    .fillMaxSize()
                    .scale(1.25f)
            )

            // Dark emerald vignette overlay matching HTML bg-gradient-to-t from-[#08130e] via-[#0d1f17]/75 to-black/30
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0.0f to Color.Black.copy(alpha = 0.30f),
                            0.55f to Color(0xFF0D1F17).copy(alpha = 0.72f),
                            1.0f to Color(0xFF08130E).copy(alpha = 0.96f)
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top row: Emoji badge + optional cart badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.55f))
                            .border(1.dp, LuxuryGold.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                    ) {
                        Text(
                            text = category.iconEmoji,
                            fontSize = 20.sp
                        )
                    }

                    if (selectedCountInCategory > 0) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(LuxuryGold)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "$selectedCountInCategory in order",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = DarkTextOnGold
                            )
                        }
                    }
                }

                // Bottom row: Category title, item count pill, and gold arrow circle
                Column {
                    Text(
                        text = category.name,
                        fontFamily = PlayfairDisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        letterSpacing = 0.6.sp,
                        color = WarmCreamText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color.Black.copy(alpha = 0.55f))
                                .border(
                                    1.dp,
                                    LuxuryGold.copy(alpha = 0.30f),
                                    RoundedCornerShape(50)
                                )
                                .padding(horizontal = 9.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.items_count_badge, category.items.size),
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp,
                                color = LuxuryGold
                            )
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(LuxuryGold.copy(alpha = 0.22f))
                                .border(1.dp, LuxuryGold.copy(alpha = 0.55f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = LuxuryGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MenuItemRowCard(
    item: MenuItem,
    category: MenuCategory,
    quantityInCart: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onDirectWhatsAppOrder: () -> Unit,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit = {},
    itemInstruction: String = "",
    onItemInstructionChange: (String) -> Unit = {},
    onTogglePresetInstruction: (String) -> Unit = {},
    onShareItem: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val chooserTitle = stringResource(R.string.share_chooser_title, item.name)

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MenuRowBg,
        border = BorderStroke(
            width = 1.dp,
            color = if (quantityInCart > 0) LuxuryGold else LuxuryGold.copy(alpha = 0.22f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("menu_item_${item.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(MenuRowBg, SurfaceEmeraldDark)
                    )
                )
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Food thumbnail + Name + Quantity/Freshly made subtitle + Price
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 10.dp)
                ) {
                    // Food thumbnail cropped to the category's dish photo
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, LuxuryGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
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
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        if (item.badge != null) {
                            val isBestSeller = item.badge == com.example.model.MenuBadge.BEST_SELLER
                            Box(
                                modifier = Modifier
                                    .padding(bottom = 4.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(
                                        if (isBestSeller) {
                                            Brush.horizontalGradient(
                                                colors = listOf(
                                                    LuxuryGold,
                                                    LightChampagneGold,
                                                    BrightGold
                                                )
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
                                        color = if (isBestSeller) BrightGold
                                        else LuxuryGold.copy(alpha = 0.55f),
                                        shape = RoundedCornerShape(50)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                    .testTag("badge_${item.id}")
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

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = item.name,
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = SoftCreamText,
                                modifier = Modifier.weight(1f, fill = false)
                            )

                            // Heart Favorite Toggle Icon Button
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isFavorite) FavoriteHeartPink.copy(alpha = 0.18f)
                                        else Color.White.copy(alpha = 0.05f)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isFavorite) FavoriteHeartPink.copy(alpha = 0.65f)
                                        else LuxuryGold.copy(alpha = 0.28f),
                                        shape = CircleShape
                                    )
                                    .clickable(onClick = onToggleFavorite)
                                    .testTag("favorite_btn_${item.id}")
                            ) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Default.Favorite
                                    else Icons.Default.FavoriteBorder,
                                    contentDescription = stringResource(R.string.toggle_favorite_desc),
                                    tint = if (isFavorite) FavoriteHeartPink
                                    else LuxuryGold.copy(alpha = 0.75f),
                                    modifier = Modifier.size(15.dp)
                                )
                            }

                            // Social Share Icon Button (Android native equivalent of Web Share API)
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(LuxuryGold.copy(alpha = 0.12f))
                                    .border(
                                        width = 1.dp,
                                        color = LuxuryGold.copy(alpha = 0.35f),
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        if (onShareItem != null) {
                                            onShareItem()
                                        } else {
                                            shareMenuItemWithFriends(context, item, chooserTitle)
                                        }
                                    }
                                    .testTag("share_btn_${item.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = stringResource(R.string.share_dish_desc),
                                    tint = LightChampagneGold,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        if (item.qty != null) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.qty,
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp,
                                color = MutedGoldText.copy(alpha = 0.85f)
                            )
                        }

                        if (item.isFreshlyMade) {
                            Spacer(modifier = Modifier.height(5.dp))
                            FreshlyMadeCountdownIndicator(
                                itemId = item.id,
                                prepTimeMinutes = item.prepTimeMinutes,
                                isActiveInOrder = quantityInCart > 0
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Exact price in Playfair Display Gold
                        Text(
                            text = "₹${item.price}",
                            fontFamily = PlayfairDisplayFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = LuxuryGold
                        )
                    }
                }

                // Right: Add / Quantity Stepper + Instant WhatsApp button
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (quantityInCart == 0) {
                        Surface(
                            onClick = onIncrement,
                            shape = RoundedCornerShape(10.dp),
                            color = LuxuryGold.copy(alpha = 0.16f),
                            border = BorderStroke(1.dp, LuxuryGold.copy(alpha = 0.6f)),
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("add_btn_${item.id}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(horizontal = 12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = stringResource(R.string.add_item_button),
                                    tint = LuxuryGold,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = stringResource(R.string.add_item_button),
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = WarmCreamText
                                )
                            }
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(LuxuryGold)
                                .height(36.dp)
                                .padding(horizontal = 4.dp)
                        ) {
                            IconButton(
                                onClick = onDecrement,
                                modifier = Modifier
                                    .size(28.dp)
                                    .testTag("dec_btn_${item.id}")
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
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp,
                                color = DarkTextOnGold,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )

                            IconButton(
                                onClick = onIncrement,
                                modifier = Modifier
                                    .size(28.dp)
                                    .testTag("inc_btn_${item.id}")
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

                    // Direct 1-tap "Order Now" WhatsApp link for this item
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable(onClick = onDirectWhatsAppOrder)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                            .testTag("direct_wa_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            tint = WhatsAppGreenLight,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = stringResource(R.string.order_now_short),
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp,
                            color = WhatsAppGreenLight
                        )
                    }
                }
            }

            // Show per-item instruction input field & quick chips when item is selected in the order
            if (quantityInCart > 0) {
                ItemSpecificInstructionField(
                    itemId = item.id,
                    instruction = itemInstruction,
                    onInstructionChange = onItemInstructionChange,
                    onTogglePreset = onTogglePresetInstruction,
                    testTagPrefix = "item_instruction_input"
                )
            }
        }
    }
}

fun shareMenuItemWithFriends(
    context: Context,
    item: MenuItem,
    chooserTitle: String
) {
    val qtySuffix = item.qty?.let { " ($it)" } ?: ""
    val shareTitle = "${item.name} at ${FoodStopRepository.RESTAURANT_NAME}"
    val shareText = buildString {
        appendLine("🍽️ Check out *${item.name}*$qtySuffix — ₹${item.price} at ${FoodStopRepository.RESTAURANT_NAME}!")
        appendLine("✨ Freshly made with authentic home-style spices.")
        appendLine("📍 ${FoodStopRepository.ADDRESS_FULL}")
        appendLine("📲 Order on WhatsApp: ${FoodStopRepository.WHATSAPP_BASE_URL}")
        append("📸 Instagram: ${FoodStopRepository.INSTAGRAM_URL}")
    }

    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, shareTitle)
        putExtra(Intent.EXTRA_TITLE, shareTitle)
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    val chooserIntent = Intent.createChooser(sendIntent, chooserTitle).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    runCatching {
        context.startActivity(chooserIntent)
    }
}

@Composable
fun FreshlyMadeCountdownIndicator(
    itemId: String,
    prepTimeMinutes: Int,
    isActiveInOrder: Boolean,
    modifier: Modifier = Modifier
) {
    val totalSeconds = (prepTimeMinutes * 60).coerceAtLeast(60)
    var remainingSeconds by androidx.compose.runtime.remember(itemId, prepTimeMinutes, isActiveInOrder) {
        androidx.compose.runtime.mutableIntStateOf(totalSeconds)
    }

    androidx.compose.runtime.LaunchedEffect(itemId, prepTimeMinutes, isActiveInOrder) {
        while (true) {
            kotlinx.coroutines.delay(1000L)
            remainingSeconds = if (remainingSeconds > 1) remainingSeconds - 1 else totalSeconds
        }
    }

    val progress = remainingSeconds.toFloat() / totalSeconds.toFloat()
    val mins = remainingSeconds / 60
    val secs = remainingSeconds % 60
    val formattedCountdown = String.format(java.util.Locale.US, "%02d:%02d", mins, secs)
    val accentColor = if (isActiveInOrder) WhatsAppGreenLight else LuxuryGold

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isActiveInOrder) {
                    WhatsAppGreenLight.copy(alpha = 0.14f)
                } else {
                    DeepJungleBlack.copy(alpha = 0.55f)
                }
            )
            .border(
                width = 1.dp,
                color = accentColor.copy(alpha = 0.40f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag("prep_countdown_$itemId"),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            // Circular animated countdown ring
            androidx.compose.foundation.Canvas(
                modifier = Modifier.size(12.dp)
            ) {
                drawCircle(
                    color = accentColor.copy(alpha = 0.25f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                )
                drawArc(
                    color = accentColor,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = 2.dp.toPx(),
                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                )
            }

            Text(
                text = if (isActiveInOrder) {
                    stringResource(R.string.prep_countdown_active_order, formattedCountdown)
                } else {
                    stringResource(R.string.prep_countdown_live, formattedCountdown)
                },
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                color = if (isActiveInOrder) LightChampagneGold else MutedGoldText
            )
        }

        // Visual countdown progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(2.5.dp)
                .clip(RoundedCornerShape(50))
                .background(accentColor.copy(alpha = 0.18f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0.05f, 1f))
                    .height(2.5.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(accentColor, LightChampagneGold)
                        )
                    )
            )
        }
    }
}

@Composable
fun CategoryDetailSection(
    category: MenuCategory,
    cartQuantities: Map<String, Int>,
    itemInstructions: Map<String, String> = emptyMap(),
    onItemInstructionChange: (String, String) -> Unit = { _, _ -> },
    onTogglePresetInstruction: (String, String) -> Unit = { _, _ -> },
    favoriteItemIds: Set<String> = emptySet(),
    onToggleFavorite: (MenuItem) -> Unit = {},
    onBackToCategories: () -> Unit,
    onIncrementItem: (MenuItem) -> Unit,
    onDecrementItem: (MenuItem) -> Unit,
    onDirectItemWhatsApp: (MenuItem) -> Unit,
    onOrderCategoryWhatsApp: (MenuCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val goldGradientBrush = Brush.linearGradient(
        colors = listOf(LuxuryGold, LightChampagneGold, BrightGold)
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back to Categories Pill + Category Title Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = onBackToCategories,
                shape = RoundedCornerShape(50),
                color = LuxuryGold.copy(alpha = 0.14f),
                border = BorderStroke(1.dp, LuxuryGold.copy(alpha = 0.35f)),
                modifier = Modifier.testTag("back_to_categories_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_to_categories),
                        tint = LuxuryGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = stringResource(R.string.back_to_categories),
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = WarmCreamText
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = category.iconEmoji, fontSize = 22.sp)
                Text(
                    text = category.name,
                    fontFamily = PlayfairDisplayFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = WarmCreamText
                )
            }
        }

        // Category Banner Container matching the HTML card container
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = CardEmeraldBottom,
            border = BorderStroke(1.dp, LuxuryGold.copy(alpha = 0.35f)),
            shadowElevation = 10.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(CardEmeraldTop, CardEmeraldBottom)
                        )
                    )
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Category Hero Header with photo banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, LuxuryGold.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_food_collage),
                        contentDescription = category.name,
                        contentScale = ContentScale.Crop,
                        alignment = BiasAlignment(category.cropBiasX, category.cropBiasY),
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(1.25f)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF08130E).copy(alpha = 0.90f),
                                        Color(0xFF08130E).copy(alpha = 0.45f)
                                    )
                                )
                            )
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = category.name,
                                fontFamily = PlayfairDisplayFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = WarmCreamText
                            )
                            Text(
                                text = stringResource(R.string.items_count_badge, category.items.size) +
                                    " • " + stringResource(R.string.freshly_made_tagline),
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                color = LightChampagneGold
                            )
                        }
                        Text(
                            text = category.iconEmoji,
                            fontSize = 36.sp
                        )
                    }
                }

                HorizontalDivider(
                    color = LuxuryGold.copy(alpha = 0.25f),
                    modifier = Modifier.padding(vertical = 2.dp)
                )

                // All items in this category
                category.items.forEach { item ->
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

                Spacer(modifier = Modifier.height(6.dp))

                // Gold Gradient "Order <Category> on WhatsApp" CTA matching HTML
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .shadow(8.dp, RoundedCornerShape(14.dp))
                        .clip(RoundedCornerShape(14.dp))
                        .background(goldGradientBrush)
                        .clickable { onOrderCategoryWhatsApp(category) }
                        .testTag("order_category_whatsapp_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            tint = DarkTextOnGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = stringResource(R.string.order_category_whatsapp, category.name),
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            letterSpacing = 0.5.sp,
                            color = DarkTextOnGold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PriceRangeFilterBar(
    activePriceFilter: com.example.model.PriceRangeFilter?,
    onSelectPriceFilter: (com.example.model.PriceRangeFilter?) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        null to stringResource(R.string.price_filter_all),
        com.example.model.PriceRangeFilter.BUDGET to stringResource(R.string.price_filter_budget),
        com.example.model.PriceRangeFilter.STANDARD to stringResource(R.string.price_filter_standard),
        com.example.model.PriceRangeFilter.PREMIUM to stringResource(R.string.price_filter_premium)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("price_range_filter_bar"),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = stringResource(R.string.price_filter_label),
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            letterSpacing = 0.8.sp,
            color = MutedGoldText
        )

        androidx.compose.foundation.lazy.LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(options.size) { index ->
                val (filterValue, labelText) = options[index]
                val isSelected = activePriceFilter == filterValue
                val tagSuffix = filterValue?.id ?: "all"

                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (filterValue == null) {
                            onSelectPriceFilter(null)
                        } else {
                            onSelectPriceFilter(filterValue)
                        }
                    },
                    label = {
                        Text(
                            text = labelText,
                            fontFamily = InterFontFamily,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = SurfaceEmeraldDark,
                        labelColor = SoftCreamText,
                        selectedContainerColor = LuxuryGold,
                        selectedLabelColor = DarkTextOnGold
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = LuxuryGold.copy(alpha = 0.35f),
                        selectedBorderColor = BrightGold
                    ),
                    modifier = Modifier.testTag("price_chip_$tagSuffix")
                )
            }
        }
    }
}

@Composable
fun FullMenuSection(
    searchQuery: String,
    activeCategoryFilterId: String?,
    activePriceFilter: com.example.model.PriceRangeFilter?,
    cartQuantities: Map<String, Int>,
    itemInstructions: Map<String, String> = emptyMap(),
    onItemInstructionChange: (String, String) -> Unit = { _, _ -> },
    onTogglePresetInstruction: (String, String) -> Unit = { _, _ -> },
    favoriteItemIds: Set<String> = emptySet(),
    onToggleFavorite: (MenuItem) -> Unit = {},
    onSearchQueryChange: (String) -> Unit,
    onSelectCategoryFilter: (String?) -> Unit,
    onSelectPriceFilter: (com.example.model.PriceRangeFilter?) -> Unit,
    onResetAllFilters: () -> Unit,
    onIncrementItem: (MenuItem) -> Unit,
    onDecrementItem: (MenuItem) -> Unit,
    onDirectItemWhatsApp: (MenuItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Search input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.search_menu_placeholder),
                    fontFamily = InterFontFamily,
                    fontSize = 13.sp,
                    color = MutedGoldText.copy(alpha = 0.6f)
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
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(R.string.clear_search),
                            tint = LuxuryGold
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = LuxuryGold,
                unfocusedBorderColor = LuxuryGold.copy(alpha = 0.35f),
                focusedContainerColor = SurfaceEmerald,
                unfocusedContainerColor = SurfaceEmeraldDark,
                focusedTextColor = WarmCreamText,
                unfocusedTextColor = SoftCreamText,
                cursorColor = LuxuryGold
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_menu_input")
        )

        // Price Range Filter Bar (All, Budget, Standard, Premium)
        PriceRangeFilterBar(
            activePriceFilter = activePriceFilter,
            onSelectPriceFilter = onSelectPriceFilter
        )

        // Category Filter Chips
        androidx.compose.foundation.lazy.LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(FoodStopRepository.categories.size) { idx ->
                val category = FoodStopRepository.categories[idx]
                val isSelected = activeCategoryFilterId == category.id
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectCategoryFilter(category.id) },
                    label = {
                        Text(
                            text = "${category.iconEmoji} ${category.name}",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = SurfaceEmeraldDark,
                        labelColor = MutedGoldText,
                        selectedContainerColor = LuxuryGold,
                        selectedLabelColor = DarkTextOnGold
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = LuxuryGold.copy(alpha = 0.35f),
                        selectedBorderColor = LuxuryGold
                    ),
                    modifier = Modifier.testTag("filter_chip_${category.id}")
                )
            }
        }

        // Filtered categories & menu items
        val normalizedQuery = searchQuery.trim().lowercase()
        var totalMatchingCount = 0

        FoodStopRepository.categories
            .filter { activeCategoryFilterId == null || it.id == activeCategoryFilterId }
            .forEach { category ->
                val matchingItems = category.items.filter { item ->
                    val matchesQuery = normalizedQuery.isEmpty() ||
                        item.name.lowercase().contains(normalizedQuery) ||
                        category.name.lowercase().contains(normalizedQuery)
                    val matchesPrice = activePriceFilter == null || activePriceFilter.matches(item.price)
                    matchesQuery && matchesPrice
                }

                if (matchingItems.isNotEmpty()) {
                    totalMatchingCount += matchingItems.size
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = CardEmeraldBottom,
                        border = BorderStroke(1.dp, LuxuryGold.copy(alpha = 0.32f)),
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
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(text = category.iconEmoji, fontSize = 20.sp)
                                    Text(
                                        text = category.name,
                                        fontFamily = PlayfairDisplayFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = WarmCreamText
                                    )
                                }
                                Text(
                                    text = stringResource(
                                        R.string.items_count_badge,
                                        matchingItems.size
                                    ),
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp,
                                    color = LuxuryGold
                                )
                            }

                            HorizontalDivider(color = LuxuryGold.copy(alpha = 0.22f))

                            matchingItems.forEach { item ->
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
                        }
                    }
                }
            }

        if (totalMatchingCount == 0) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = SurfaceEmerald.copy(alpha = 0.85f),
                border = BorderStroke(1.dp, LuxuryGold.copy(alpha = 0.28f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("full_menu_empty_state")
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
                        color = WarmCreamText
                    )
                    Text(
                        text = stringResource(R.string.no_search_results_subtitle),
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        color = MutedGoldText
                    )
                    TextButton(
                        onClick = onResetAllFilters,
                        modifier = Modifier.testTag("reset_filters_button")
                    ) {
                        Text(
                            text = stringResource(R.string.reset_filters),
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = LuxuryGold
                        )
                    }
                }
            }
        }
    }
}
