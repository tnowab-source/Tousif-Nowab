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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.OrderHistoryEntity
import com.example.ui.theme.BrightGold
import com.example.ui.theme.CardEmeraldBottom
import com.example.ui.theme.CardEmeraldTop
import com.example.ui.theme.DarkTextOnGold
import com.example.ui.theme.DeepJungleBlack
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LightChampagneGold
import com.example.ui.theme.LuxuryGold
import com.example.ui.theme.MenuRowBg
import com.example.ui.theme.MutedGoldText
import com.example.ui.theme.PlayfairDisplayFontFamily
import com.example.ui.theme.SoftCreamText
import com.example.ui.theme.SurfaceEmerald
import com.example.ui.theme.WarmCreamText
import com.example.ui.theme.WhatsAppGreenLight
import com.example.viewmodel.OrderStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrderHistorySection(
    orders: List<OrderHistoryEntity>,
    onReorderToCart: (OrderHistoryEntity) -> Unit,
    onWhatsAppRepeatOrder: (OrderHistoryEntity) -> Unit,
    onCycleOrderStatus: (OrderHistoryEntity) -> Unit,
    onDeleteOrder: (Int) -> Unit,
    onClearAllHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("order_history_section"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Section Header
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
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = LuxuryGold,
                    modifier = Modifier.size(22.dp)
                )
                Column {
                    Text(
                        text = stringResource(R.string.order_history_title),
                        fontFamily = PlayfairDisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = WarmCreamText
                    )
                    Text(
                        text = stringResource(R.string.order_history_subtitle),
                        fontFamily = InterFontFamily,
                        fontSize = 11.sp,
                        color = MutedGoldText
                    )
                }
            }

            if (orders.isNotEmpty()) {
                TextButton(
                    onClick = onClearAllHistory,
                    modifier = Modifier.testTag("clear_all_history_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = null,
                        tint = LightChampagneGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.order_history_clear_all),
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = LightChampagneGold
                    )
                }
            }
        }

        if (orders.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = SurfaceEmerald.copy(alpha = 0.85f),
                border = BorderStroke(1.dp, LuxuryGold.copy(alpha = 0.28f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("order_history_empty_state")
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = LuxuryGold.copy(alpha = 0.7f),
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = stringResource(R.string.order_history_empty_title),
                        fontFamily = PlayfairDisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = WarmCreamText,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.order_history_empty_subtitle),
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        color = MutedGoldText,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            orders.forEach { order ->
                PastOrderHistoryCard(
                    order = order,
                    onReorderToCart = { onReorderToCart(order) },
                    onWhatsAppRepeatOrder = { onWhatsAppRepeatOrder(order) },
                    onCycleOrderStatus = { onCycleOrderStatus(order) },
                    onDeleteOrder = { onDeleteOrder(order.id) }
                )
            }
        }
    }
}

@Composable
private fun PastOrderHistoryCard(
    order: OrderHistoryEntity,
    onReorderToCart: () -> Unit,
    onWhatsAppRepeatOrder: () -> Unit,
    onCycleOrderStatus: () -> Unit,
    onDeleteOrder: () -> Unit
) {
    val formattedDate = remember(order.timestamp) {
        SimpleDateFormat("dd MMM yyyy • hh:mm a", Locale.US).format(Date(order.timestamp))
    }
    val status = OrderStatus.fromId(order.statusId)
    val statusLabel = when (status) {
        OrderStatus.PREPARING -> stringResource(R.string.order_status_preparing)
        OrderStatus.OUT_FOR_DELIVERY -> stringResource(R.string.order_status_out_for_delivery)
        OrderStatus.DELIVERED -> stringResource(R.string.order_status_delivered)
    }
    val statusColor = when (status) {
        OrderStatus.PREPARING -> LuxuryGold
        OrderStatus.OUT_FOR_DELIVERY -> BrightGold
        OrderStatus.DELIVERED -> WhatsAppGreenLight
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = CardEmeraldBottom,
        border = BorderStroke(1.dp, LuxuryGold.copy(alpha = 0.35f)),
        shadowElevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("past_order_card_${order.id}")
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(CardEmeraldTop, CardEmeraldBottom)
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top Row: Order Number + Timestamp + Status Pill + Delete Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Order #FS-${100 + order.id}",
                        fontFamily = PlayfairDisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = WarmCreamText
                    )
                    Text(
                        text = formattedDate,
                        fontFamily = InterFontFamily,
                        fontSize = 11.sp,
                        color = MutedGoldText
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Tappable Status Pill to cycle status in Room DB
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(statusColor.copy(alpha = 0.18f))
                            .border(1.dp, statusColor.copy(alpha = 0.6f), RoundedCornerShape(50))
                            .clickable(onClick = onCycleOrderStatus)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                            .testTag("past_order_status_${order.id}")
                    ) {
                        Text(
                            text = statusLabel,
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = statusColor
                        )
                    }

                    IconButton(
                        onClick = onDeleteOrder,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("delete_history_${order.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = stringResource(R.string.order_history_delete),
                            tint = MutedGoldText,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            HorizontalDivider(color = LuxuryGold.copy(alpha = 0.22f))

            // Items Summary Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MenuRowBg)
                    .border(1.dp, LuxuryGold.copy(alpha = 0.18f), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = order.itemsSummary,
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = SoftCreamText
                )

                if (order.orderNote.isNotBlank()) {
                    Text(
                        text = "Note: ${order.orderNote}",
                        fontFamily = InterFontFamily,
                        fontStyle = FontStyle.Italic,
                        fontSize = 11.sp,
                        color = MutedGoldText
                    )
                }
            }

            // Bottom Row: Total Price + Reorder & WhatsApp Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${order.totalItemsCount} item${if (order.totalItemsCount > 1) "s" else ""}",
                        fontFamily = InterFontFamily,
                        fontSize = 11.sp,
                        color = MutedGoldText
                    )
                    Text(
                        text = "₹${order.totalAmount}",
                        fontFamily = PlayfairDisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = LuxuryGold
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = onReorderToCart,
                        shape = RoundedCornerShape(10.dp),
                        color = DeepJungleBlack.copy(alpha = 0.7f),
                        border = BorderStroke(1.dp, LuxuryGold.copy(alpha = 0.55f)),
                        modifier = Modifier
                            .height(38.dp)
                            .testTag("reorder_button_${order.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            modifier = Modifier.padding(horizontal = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Replay,
                                contentDescription = null,
                                tint = LuxuryGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = stringResource(R.string.order_history_reorder),
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = LightChampagneGold
                            )
                        }
                    }

                    Button(
                        onClick = onWhatsAppRepeatOrder,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WhatsAppGreenLight,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        modifier = Modifier
                            .height(38.dp)
                            .testTag("wa_repeat_order_${order.id}")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = stringResource(R.string.order_history_whatsapp_again),
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
