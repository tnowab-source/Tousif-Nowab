package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BrightGold
import com.example.ui.theme.CardEmeraldBottom
import com.example.ui.theme.CardEmeraldTop
import com.example.ui.theme.DarkTextOnGold
import com.example.ui.theme.DeepJungleBlack
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LightChampagneGold
import com.example.ui.theme.LuxuryGold
import com.example.ui.theme.MutedGoldText
import com.example.ui.theme.PlayfairDisplayFontFamily
import com.example.ui.theme.SoftCreamText
import com.example.ui.theme.SurfaceEmeraldDark
import com.example.ui.theme.WarmCreamText
import com.example.ui.theme.WhatsAppGreenLight
import com.example.viewmodel.OrderStatus

@Composable
fun OrderStatusIndicatorWidget(
    currentStatus: OrderStatus,
    totalCartItems: Int,
    totalCartPrice: Int,
    onSelectStatus: (OrderStatus) -> Unit,
    onCycleStatus: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progressTarget = when (currentStatus) {
        OrderStatus.PREPARING -> 0.34f
        OrderStatus.OUT_FOR_DELIVERY -> 0.68f
        OrderStatus.DELIVERED -> 1.0f
    }
    val animatedProgress by animateFloatAsState(
        targetValue = progressTarget,
        animationSpec = tween(durationMillis = 350),
        label = "order_status_progress"
    )

    val statusAccentColor by animateColorAsState(
        targetValue = when (currentStatus) {
            OrderStatus.PREPARING -> LuxuryGold
            OrderStatus.OUT_FOR_DELIVERY -> BrightGold
            OrderStatus.DELIVERED -> WhatsAppGreenLight
        },
        animationSpec = tween(durationMillis = 300),
        label = "order_status_accent"
    )

    val steps = listOf(
        Triple(
            OrderStatus.PREPARING,
            stringResource(R.string.order_status_preparing),
            Icons.Default.Restaurant
        ),
        Triple(
            OrderStatus.OUT_FOR_DELIVERY,
            stringResource(R.string.order_status_out_for_delivery),
            Icons.Default.DeliveryDining
        ),
        Triple(
            OrderStatus.DELIVERED,
            stringResource(R.string.order_status_delivered),
            Icons.Default.CheckCircle
        )
    )

    val statusDescription = when (currentStatus) {
        OrderStatus.PREPARING -> stringResource(R.string.order_status_preparing_desc)
        OrderStatus.OUT_FOR_DELIVERY -> stringResource(R.string.order_status_out_for_delivery_desc)
        OrderStatus.DELIVERED -> stringResource(R.string.order_status_delivered_desc)
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = CardEmeraldBottom,
        border = BorderStroke(1.dp, statusAccentColor.copy(alpha = 0.45f)),
        shadowElevation = 6.dp,
        modifier = modifier
            .fillMaxWidth()
            .testTag("order_status_widget")
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(CardEmeraldTop, CardEmeraldBottom)
                    )
                )
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Top Header Row: Title + Quick Cycle Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.order_status_widget_title),
                        fontFamily = PlayfairDisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = WarmCreamText
                    )
                    if (totalCartItems > 0) {
                        Text(
                            text = "$totalCartItems item${if (totalCartItems > 1) "s" else ""} in current order • ₹$totalCartPrice",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp,
                            color = LightChampagneGold
                        )
                    }
                }

                Surface(
                    onClick = onCycleStatus,
                    shape = RoundedCornerShape(50),
                    color = statusAccentColor.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, statusAccentColor.copy(alpha = 0.55f)),
                    modifier = Modifier.testTag("order_status_cycle_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = stringResource(R.string.order_status_next_step),
                            tint = statusAccentColor,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = stringResource(R.string.order_status_next_step),
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = WarmCreamText
                        )
                    }
                }
            }

            // Interactive 3-State Toggle Selector ("Preparing", "Out for Delivery", "Delivered")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DeepJungleBlack.copy(alpha = 0.65f))
                    .border(1.dp, LuxuryGold.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                steps.forEach { (status, label, icon) ->
                    val isSelected = currentStatus == status
                    val isCompleted = status.stepIndex <= currentStatus.stepIndex

                    OrderStatusStepPill(
                        label = label,
                        icon = icon,
                        isSelected = isSelected,
                        isCompleted = isCompleted,
                        accentColor = statusAccentColor,
                        onClick = { onSelectStatus(status) },
                        testTag = "order_status_toggle_${status.id}",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Animated Progress Bar + Status Description
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(50))
                        .background(SurfaceEmeraldDark)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .height(5.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(LuxuryGold, statusAccentColor)
                                )
                            )
                    )
                }

                Text(
                    text = statusDescription,
                    fontFamily = InterFontFamily,
                    fontSize = 11.sp,
                    color = MutedGoldText,
                    modifier = Modifier.testTag("order_status_description")
                )
            }
        }
    }
}

@Composable
private fun OrderStatusStepPill(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    isCompleted: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val bgColor = when {
        isSelected -> accentColor
        isCompleted -> accentColor.copy(alpha = 0.14f)
        else -> Color.Transparent
    }
    val contentColor = when {
        isSelected -> DarkTextOnGold
        isCompleted -> LightChampagneGold
        else -> MutedGoldText.copy(alpha = 0.7f)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(9.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp)
            .testTag(testTag)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontFamily = InterFontFamily,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                fontSize = 10.sp,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}
