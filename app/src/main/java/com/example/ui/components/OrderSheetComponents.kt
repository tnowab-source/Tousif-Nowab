package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.FoodStopRepository
import com.example.model.MenuItem
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
fun ItemSpecificInstructionField(
    itemId: String,
    instruction: String,
    onInstructionChange: (String) -> Unit,
    onTogglePreset: (String) -> Unit,
    testTagPrefix: String,
    modifier: Modifier = Modifier
) {
    val presets = listOf(
        stringResource(R.string.preset_extra_spicy),
        stringResource(R.string.preset_no_onion),
        stringResource(R.string.preset_less_mayo)
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        OutlinedTextField(
            value = instruction,
            onValueChange = onInstructionChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.item_instruction_placeholder),
                    fontFamily = InterFontFamily,
                    fontSize = 11.sp,
                    color = MutedGoldText.copy(alpha = 0.65f)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.EditNote,
                    contentDescription = null,
                    tint = LuxuryGold,
                    modifier = Modifier.size(16.dp)
                )
            },
            trailingIcon = {
                if (instruction.isNotEmpty()) {
                    IconButton(
                        onClick = { onInstructionChange("") },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(R.string.clear_search),
                            tint = LuxuryGold,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            },
            textStyle = TextStyle(
                fontFamily = InterFontFamily,
                fontSize = 12.sp,
                color = WarmCreamText
            ),
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = LuxuryGold,
                unfocusedBorderColor = LuxuryGold.copy(alpha = 0.32f),
                focusedContainerColor = DeepJungleBlack.copy(alpha = 0.65f),
                unfocusedContainerColor = DeepJungleBlack.copy(alpha = 0.45f),
                focusedTextColor = WarmCreamText,
                unfocusedTextColor = SoftCreamText,
                cursorColor = LuxuryGold
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("${testTagPrefix}_$itemId")
        )

        // Quick-tap preset tags ("Extra Spicy", "No Onion", "Less Mayo")
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            presets.forEach { preset ->
                val isPresetActive = instruction
                    .split(",")
                    .map { it.trim() }
                    .any { it.equals(preset, ignoreCase = true) }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (isPresetActive) LuxuryGold
                            else LuxuryGold.copy(alpha = 0.12f)
                        )
                        .border(
                            width = 1.dp,
                            color = if (isPresetActive) LuxuryGold else LuxuryGold.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(50)
                        )
                        .clickable { onTogglePreset(preset) }
                        .padding(horizontal = 9.dp, vertical = 3.dp)
                        .testTag("${testTagPrefix}_preset_${itemId}_${preset.lowercase().replace(" ", "_")}")
                ) {
                    Text(
                        text = preset,
                        fontFamily = InterFontFamily,
                        fontWeight = if (isPresetActive) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 10.sp,
                        color = if (isPresetActive) DarkTextOnGold else LightChampagneGold
                    )
                }
            }
        }
    }
}

@Composable
fun StickyOrderNowBar(
    totalItems: Int,
    totalPrice: Int,
    onReviewOrder: () -> Unit,
    onOrderNowWhatsApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = HeaderEmeraldStart,
        border = BorderStroke(1.5.dp, LuxuryGold),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        shadowElevation = 16.dp,
        modifier = modifier
            .fillMaxWidth()
            .testTag("sticky_order_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(HeaderEmeraldStart, RichEmeraldBg)
                    )
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Cart summary (clickable to open Order Sheet if items > 0)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(enabled = totalItems > 0, onClick = onReviewOrder)
                    .padding(vertical = 4.dp, horizontal = 4.dp)
                    .testTag("review_order_button")
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(LuxuryGold.copy(alpha = 0.18f))
                        .border(1.dp, LuxuryGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = LuxuryGold,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                if (totalItems > 0) {
                    Column {
                        Text(
                            text = "$totalItems item${if (totalItems > 1) "s" else ""} selected • Tap to customize",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp,
                            color = LightChampagneGold
                        )
                        Text(
                            text = "₹$totalPrice",
                            fontFamily = PlayfairDisplayFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp,
                            color = LuxuryGold
                        )
                    }
                } else {
                    Column {
                        Text(
                            text = "WhatsApp Direct Ordering",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = WarmCreamText
                        )
                        Text(
                            text = "Select items or tap Order Now • 8100375680",
                            fontFamily = InterFontFamily,
                            fontSize = 11.sp,
                            color = MutedGoldText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Right: Order Now on WhatsApp CTA button
            Button(
                onClick = onOrderNowWhatsApp,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = WhatsAppGreenLight,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                modifier = Modifier
                    .height(48.dp)
                    .testTag("order_now_whatsapp_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.order_now_short),
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderReviewBottomSheet(
    cartItemsDetailed: List<Pair<MenuItem, Int>>,
    itemInstructions: Map<String, String>,
    totalPrice: Int,
    orderNote: String,
    onItemInstructionChange: (String, String) -> Unit,
    onToggleItemPreset: (String, String) -> Unit,
    onOrderNoteChange: (String) -> Unit,
    onIncrementItem: (MenuItem) -> Unit,
    onDecrementItem: (MenuItem) -> Unit,
    onClearCart: () -> Unit,
    onDismiss: () -> Unit,
    onConfirmWhatsAppOrder: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = RichEmeraldBg,
        contentColor = SoftCreamText
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.your_order_title),
                        fontFamily = PlayfairDisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = WarmCreamText
                    )
                    Text(
                        text = "${FoodStopRepository.RESTAURANT_NAME} • ${FoodStopRepository.WHATSAPP_NUMBER_DISPLAY}",
                        fontFamily = InterFontFamily,
                        fontSize = 11.sp,
                        color = LuxuryGold
                    )
                }

                TextButton(
                    onClick = onClearCart,
                    modifier = Modifier.testTag("clear_order_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = null,
                        tint = LightChampagneGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.clear_order),
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = LightChampagneGold
                    )
                }
            }

            HorizontalDivider(color = LuxuryGold.copy(alpha = 0.25f))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 320.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(cartItemsDetailed, key = { it.first.id }) { (item, count) ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MenuRowBg)
                            .border(1.dp, LuxuryGold.copy(alpha = 0.22f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.name + (item.qty?.let { " ($it)" } ?: ""),
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = WarmCreamText
                                )
                                Text(
                                    text = "₹${item.price} × $count = ₹${item.price * count}",
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                    color = LuxuryGold
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(LuxuryGold)
                                    .height(32.dp)
                            ) {
                                IconButton(
                                    onClick = { onDecrementItem(item) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Remove,
                                        contentDescription = stringResource(R.string.decrease_qty),
                                        tint = DarkTextOnGold,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                                Text(
                                    text = count.toString(),
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = DarkTextOnGold,
                                    modifier = Modifier.padding(horizontal = 6.dp)
                                )
                                IconButton(
                                    onClick = { onIncrementItem(item) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = stringResource(R.string.increase_qty),
                                        tint = DarkTextOnGold,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }

                        // Per-item specific instruction input field & preset chips
                        ItemSpecificInstructionField(
                            itemId = item.id,
                            instruction = itemInstructions[item.id].orEmpty(),
                            onInstructionChange = { onItemInstructionChange(item.id, it) },
                            onTogglePreset = { preset -> onToggleItemPreset(item.id, preset) },
                            testTagPrefix = "sheet_item_instruction"
                        )
                    }
                }
            }

            OutlinedTextField(
                value = orderNote,
                onValueChange = onOrderNoteChange,
                placeholder = {
                    Text(
                        text = stringResource(R.string.order_note_placeholder),
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        color = MutedGoldText.copy(alpha = 0.6f)
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LuxuryGold,
                    unfocusedBorderColor = LuxuryGold.copy(alpha = 0.35f),
                    focusedContainerColor = SurfaceEmerald,
                    unfocusedContainerColor = SurfaceEmeraldDark,
                    focusedTextColor = WarmCreamText,
                    unfocusedTextColor = SoftCreamText
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("order_note_input")
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.total_amount_label),
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = SoftCreamText
                )
                Text(
                    text = "₹$totalPrice",
                    fontFamily = PlayfairDisplayFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = LuxuryGold
                )
            }

            Button(
                onClick = onConfirmWhatsAppOrder,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = WhatsAppGreenDark,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("sheet_confirm_whatsapp_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.order_now_whatsapp) + " (₹$totalPrice)",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}
