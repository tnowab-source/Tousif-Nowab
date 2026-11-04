package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.model.FoodStopRepository
import com.example.ui.theme.BrightGold
import com.example.ui.theme.CardEmeraldBottom
import com.example.ui.theme.CardEmeraldTop
import com.example.ui.theme.DarkTextOnGold
import com.example.ui.theme.DeepJungleBlack
import com.example.ui.theme.HeaderEmeraldEnd
import com.example.ui.theme.HeaderEmeraldStart
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LightChampagneGold
import com.example.ui.theme.LuxuryGold
import com.example.ui.theme.MutedGoldText
import com.example.ui.theme.PlayfairDisplayFontFamily
import com.example.ui.theme.RichEmeraldBg
import com.example.ui.theme.SoftCreamText
import com.example.ui.theme.SurfaceEmerald
import com.example.ui.theme.SurfaceEmeraldDark
import com.example.ui.theme.WarmCreamText
import com.example.ui.theme.WhatsAppGreenLight
import com.example.util.QrCodeGenerator
import com.example.viewmodel.QrModalData
import java.util.Calendar

@OptIn(ExperimentalTextApi::class)
@Composable
fun FoodStopHeader(
    onLogoClick: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onInstagramClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val goldGradientBrush = Brush.linearGradient(
        colors = listOf(LuxuryGold, LightChampagneGold, BrightGold)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(HeaderEmeraldStart, HeaderEmeraldEnd)
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: FS Gold Crest + FOOD STOP / HOME KITCHEN
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onLogoClick)
                    .padding(end = 8.dp)
                    .testTag("header_brand_logo")
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(8.dp, CircleShape)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(CardEmeraldTop, DeepJungleBlack)
                            )
                        )
                        .border(2.dp, LuxuryGold, CircleShape)
                ) {
                    Text(
                        text = stringResource(R.string.brand_monogram),
                        fontFamily = PlayfairDisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp,
                        style = MaterialTheme.typography.titleLarge.copy(
                            brush = goldGradientBrush
                        )
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    brush = goldGradientBrush,
                                    fontFamily = PlayfairDisplayFontFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp,
                                    letterSpacing = 1.2.sp
                                )
                            ) {
                                append(stringResource(R.string.brand_title))
                            }
                        }
                    )
                    Text(
                        text = stringResource(R.string.brand_subtitle),
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp,
                        letterSpacing = 2.8.sp,
                        color = LuxuryGold.copy(alpha = 0.85f)
                    )
                }
            }

            // Right: Phone / WhatsApp pill & Instagram handle
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Surface(
                    onClick = onWhatsAppClick,
                    shape = RoundedCornerShape(50),
                    color = LuxuryGold.copy(alpha = 0.14f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        LuxuryGold.copy(alpha = 0.35f)
                    ),
                    modifier = Modifier.testTag("header_whatsapp_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = stringResource(R.string.call_kitchen),
                            tint = LuxuryGold,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = stringResource(R.string.contact_phone),
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = WarmCreamText
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onInstagramClick)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .testTag("header_instagram_button")
                ) {
                    Text(
                        text = stringResource(R.string.contact_instagram),
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp,
                        letterSpacing = 0.6.sp,
                        color = MutedGoldText
                    )
                }
            }
        }

        // Gold divider line matching .gold-divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            LuxuryGold.copy(alpha = 0.55f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

@Composable
fun FindUsAndQrFooterSection(
    onOpenDirections: () -> Unit,
    onOpenWhatsAppDirect: () -> Unit,
    onOpenInstagramDirect: () -> Unit,
    onShowQrModal: (QrModalData) -> Unit,
    modifier: Modifier = Modifier
) {
    val goldGradientBrush = Brush.linearGradient(
        colors = listOf(LuxuryGold, LightChampagneGold, BrightGold)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Gold horizontal divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            LuxuryGold.copy(alpha = 0.5f),
                            Color.Transparent
                        )
                    )
                )
        )

        // 1. Find Us & Embedded Google Maps Card with Live Kitchen Distance
        EmbeddedGoogleMapRestaurantCard(
            onOpenDirections = onOpenDirections
        )

        // 2. Quick Connect QR Codes Card
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SurfaceEmerald,
            border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryGold.copy(alpha = 0.35f)),
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("quick_connect_qr_card")
        ) {
            Column(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(SurfaceEmerald, SurfaceEmeraldDark)
                        )
                    )
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode2,
                        contentDescription = null,
                        tint = LuxuryGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = stringResource(R.string.quick_connect_title),
                        fontFamily = PlayfairDisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        letterSpacing = 0.6.sp,
                        color = WarmCreamText
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.qr_scan_prompt),
                    fontFamily = InterFontFamily,
                    fontSize = 11.sp,
                    color = MutedGoldText.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // WhatsApp QR Card
                    QrCodeCardItem(
                        title = stringResource(R.string.qr_whatsapp_label),
                        subtitle = stringResource(R.string.qr_whatsapp_sub),
                        qrContent = FoodStopRepository.WHATSAPP_BASE_URL,
                        onCardClick = {
                            onShowQrModal(
                                QrModalData(
                                    title = "WHATSAPP ORDERING QR",
                                    subtitle = FoodStopRepository.WHATSAPP_NUMBER_DISPLAY,
                                    url = FoodStopRepository.WHATSAPP_BASE_URL,
                                    isWhatsApp = true
                                )
                            )
                        },
                        onDirectLinkClick = onOpenWhatsAppDirect,
                        testTag = "qr_card_whatsapp",
                        modifier = Modifier.weight(1f)
                    )

                    // Instagram QR Card
                    QrCodeCardItem(
                        title = stringResource(R.string.qr_instagram_label),
                        subtitle = stringResource(R.string.qr_instagram_sub),
                        qrContent = FoodStopRepository.INSTAGRAM_URL,
                        onCardClick = {
                            onShowQrModal(
                                QrModalData(
                                    title = "INSTAGRAM PROFILE QR",
                                    subtitle = FoodStopRepository.INSTAGRAM_HANDLE_DISPLAY,
                                    url = FoodStopRepository.INSTAGRAM_URL,
                                    isWhatsApp = false
                                )
                            )
                        },
                        onDirectLinkClick = onOpenInstagramDirect,
                        testTag = "qr_card_instagram",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Copyright footer
        val currentYear = remember { Calendar.getInstance().get(Calendar.YEAR) }
        Text(
            text = stringResource(R.string.footer_copyright, currentYear),
            fontFamily = InterFontFamily,
            fontSize = 11.sp,
            letterSpacing = 1.4.sp,
            color = MutedGoldText.copy(alpha = 0.55f),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
    }
}

@Composable
fun QrCodeCardItem(
    title: String,
    subtitle: String,
    qrContent: String,
    onCardClick: () -> Unit,
    onDirectLinkClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(DeepJungleBlack.copy(alpha = 0.65f))
            .border(1.dp, LuxuryGold.copy(alpha = 0.28f), RoundedCornerShape(14.dp))
            .clickable(onClick = onCardClick)
            .padding(12.dp)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(116.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .border(2.dp, LuxuryGold, RoundedCornerShape(10.dp))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            ScannableQrCanvas(
                content = qrContent,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = title,
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 1.sp,
            color = LuxuryGold
        )

        Spacer(modifier = Modifier.height(2.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .clickable(onClick = onDirectLinkClick)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = subtitle,
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                color = SoftCreamText
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = null,
                tint = LuxuryGold,
                modifier = Modifier.size(11.dp)
            )
        }
    }
}

@Composable
fun ScannableQrCanvas(
    content: String,
    modifier: Modifier = Modifier
) {
    val matrix = remember(content) { QrCodeGenerator.encode(content) }
    Canvas(modifier = modifier.aspectRatio(1f)) {
        val modulesCount = matrix.size
        // Include 2-module quiet zone around the QR matrix
        val totalModules = modulesCount + 4
        val cellSize = size.width / totalModules
        drawRect(color = Color.White)
        for (r in 0 until modulesCount) {
            for (c in 0 until modulesCount) {
                if (matrix[r][c]) {
                    drawRect(
                        color = Color(0xFF080F0C),
                        topLeft = Offset((c + 2) * cellSize, (r + 2) * cellSize),
                        size = Size(cellSize * 1.03f, cellSize * 1.03f)
                    )
                }
            }
        }
    }
}

@Composable
fun QrEnlargeModalDialog(
    modalData: QrModalData,
    onDismiss: () -> Unit,
    onOpenUrl: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = RichEmeraldBg,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, LuxuryGold),
            shadowElevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("qr_enlarge_dialog")
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(CardEmeraldTop, CardEmeraldBottom)
                        )
                    )
                    .padding(22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = modalData.title,
                        fontFamily = PlayfairDisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = WarmCreamText
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("qr_dialog_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.qr_modal_close),
                            tint = LuxuryGold
                        )
                    }
                }

                HorizontalDivider(
                    color = LuxuryGold.copy(alpha = 0.25f),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(3.dp, LuxuryGold, RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ScannableQrCanvas(
                        content = modalData.url,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = FoodStopRepository.RESTAURANT_NAME,
                    fontFamily = PlayfairDisplayFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = LuxuryGold
                )
                Text(
                    text = modalData.subtitle,
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = SoftCreamText
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        onOpenUrl(modalData.url)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (modalData.isWhatsApp) WhatsAppGreenLight else LuxuryGold,
                        contentColor = DarkTextOnGold
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("qr_dialog_action_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (modalData.isWhatsApp) {
                            stringResource(R.string.qr_modal_open_whatsapp)
                        } else {
                            stringResource(R.string.qr_modal_open_instagram)
                        },
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
