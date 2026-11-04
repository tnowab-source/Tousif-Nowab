package com.example.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.R
import com.example.model.FoodStopRepository
import com.example.ui.theme.BrightGold
import com.example.ui.theme.DarkTextOnGold
import com.example.ui.theme.DeepJungleBlack
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LightChampagneGold
import com.example.ui.theme.LuxuryGold
import com.example.ui.theme.MutedGoldText
import com.example.ui.theme.PlayfairDisplayFontFamily
import com.example.ui.theme.SurfaceEmerald
import com.example.ui.theme.SurfaceEmeraldDark
import com.example.ui.theme.WarmCreamText
import com.example.ui.theme.WhatsAppGreenLight

@Composable
fun EmbeddedGoogleMapRestaurantCard(
    onOpenDirections: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val goldGradientBrush = Brush.linearGradient(
        colors = listOf(LuxuryGold, LightChampagneGold, BrightGold)
    )

    var hasLocationPermission by remember {
        mutableStateOf(checkHasLocationPermission(context))
    }
    var distanceMeters by remember { mutableStateOf<Float?>(null) }
    var isLocating by remember { mutableStateOf(false) }
    var permissionDeniedMessage by remember { mutableStateOf(false) }
    var refreshTrigger by remember { mutableStateOf(0) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        hasLocationPermission = granted
        permissionDeniedMessage = !granted
        if (granted) {
            refreshTrigger++
        }
    }

    // Observe real device GPS / Network location when permission is granted
    DisposableEffect(hasLocationPermission, refreshTrigger) {
        if (!hasLocationPermission) {
            onDispose { }
        } else {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            val listener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    distanceMeters = calculateDistanceToKitchenMeters(
                        location.latitude,
                        location.longitude
                    )
                    isLocating = false
                }

                @Deprecated("Deprecated in Java")
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }

            if (locationManager != null) {
                isLocating = true
                val lastKnown = getBestLastKnownLocation(context, locationManager)
                if (lastKnown != null) {
                    distanceMeters = calculateDistanceToKitchenMeters(
                        lastKnown.latitude,
                        lastKnown.longitude
                    )
                    isLocating = false
                }
                requestLiveLocationUpdates(context, locationManager, listener)
            }

            onDispose {
                try {
                    locationManager?.removeUpdates(listener)
                } catch (_: SecurityException) {
                }
            }
        }
    }

    LaunchedEffect(isLocating) {
        if (isLocating) {
            kotlinx.coroutines.delay(4500L)
            isLocating = false
        }
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = SurfaceEmerald,
        border = BorderStroke(1.dp, LuxuryGold.copy(alpha = 0.35f)),
        shadowElevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .testTag("find_us_card")
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(SurfaceEmerald, SurfaceEmeraldDark)
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row: Find Us Title + Directions Button
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
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = stringResource(R.string.find_us_title),
                        tint = LuxuryGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = stringResource(R.string.find_us_title),
                        fontFamily = PlayfairDisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = WarmCreamText
                    )
                }

                // Gold Directions Pill Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(goldGradientBrush)
                        .clickable(onClick = onOpenDirections)
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                        .testTag("directions_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Navigation,
                            contentDescription = null,
                            tint = DarkTextOnGold,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = stringResource(R.string.directions_button),
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = DarkTextOnGold
                        )
                    }
                }
            }

            // Live Distance to Kitchen Banner
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = DeepJungleBlack.copy(alpha = 0.65f),
                border = BorderStroke(
                    1.dp,
                    if (distanceMeters != null) WhatsAppGreenLight.copy(alpha = 0.5f)
                    else LuxuryGold.copy(alpha = 0.3f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("kitchen_distance_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(LuxuryGold.copy(alpha = 0.16f))
                                .border(1.dp, LuxuryGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = null,
                                tint = if (distanceMeters != null) WhatsAppGreenLight else LuxuryGold,
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        Column {
                            Text(
                                text = stringResource(R.string.distance_to_kitchen_label),
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp,
                                color = MutedGoldText
                            )

                            val currentDist = distanceMeters
                            val statusText = when {
                                currentDist != null -> {
                                    if (currentDist < 1000f) {
                                        stringResource(
                                            R.string.distance_meters_away,
                                            currentDist.toInt()
                                        )
                                    } else {
                                        stringResource(
                                            R.string.distance_km_away,
                                            currentDist / 1000f
                                        )
                                    }
                                }
                                isLocating -> stringResource(R.string.distance_locating)
                                permissionDeniedMessage -> stringResource(R.string.distance_permission_denied)
                                hasLocationPermission -> stringResource(R.string.distance_gps_unavailable)
                                else -> stringResource(R.string.distance_check_button)
                            }

                            Text(
                                text = statusText,
                                fontFamily = PlayfairDisplayFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (currentDist != null) LightChampagneGold else WarmCreamText,
                                modifier = Modifier.testTag("kitchen_distance_value")
                            )
                        }
                    }

                    // Action Pill: Check Distance (requests permission) or Refresh
                    Surface(
                        onClick = {
                            if (hasLocationPermission) {
                                refreshTrigger++
                            } else {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        },
                        shape = RoundedCornerShape(50),
                        color = LuxuryGold.copy(alpha = 0.18f),
                        border = BorderStroke(1.dp, LuxuryGold.copy(alpha = 0.5f)),
                        modifier = Modifier.testTag("calculate_distance_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = if (hasLocationPermission) {
                                    Icons.Default.Refresh
                                } else {
                                    Icons.Default.MyLocation
                                },
                                contentDescription = null,
                                tint = LuxuryGold,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = if (hasLocationPermission) {
                                    stringResource(R.string.distance_refresh)
                                } else {
                                    stringResource(R.string.distance_check_button)
                                },
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = LightChampagneGold
                            )
                        }
                    }
                }
            }

            // Embedded Interactive Google Maps View (matching the original HTML iframe embed)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, LuxuryGold.copy(alpha = 0.32f), RoundedCornerShape(14.dp))
                    .testTag("embedded_google_maps_view")
            ) {
                EmbeddedGoogleMapWebView(
                    embedUrl = FoodStopRepository.GOOGLE_MAPS_EMBED_URL,
                    modifier = Modifier.fillMaxSize()
                )

                // Floating Open in Google Maps badge
                Surface(
                    onClick = onOpenDirections,
                    color = DeepJungleBlack.copy(alpha = 0.88f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, LuxuryGold.copy(alpha = 0.45f)),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .testTag("google_maps_preview")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.open_google_maps),
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp,
                            color = LightChampagneGold
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            tint = LightChampagneGold,
                            modifier = Modifier.size(11.dp)
                        )
                    }
                }
            }

            // Address Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DeepJungleBlack.copy(alpha = 0.55f))
                    .border(1.dp, LuxuryGold.copy(alpha = 0.18f), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = LuxuryGold,
                    modifier = Modifier.size(18.dp)
                )
                Column {
                    Text(
                        text = stringResource(R.string.kitchen_address_title),
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = WarmCreamText
                    )
                    Text(
                        text = stringResource(R.string.kitchen_address_subtitle),
                        fontFamily = InterFontFamily,
                        fontSize = 11.sp,
                        color = MutedGoldText.copy(alpha = 0.85f)
                    )
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun EmbeddedGoogleMapWebView(
    embedUrl: String,
    modifier: Modifier = Modifier
) {
    val htmlContent = remember(embedUrl) {
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <style>
                html, body {
                    margin: 0;
                    padding: 0;
                    width: 100%;
                    height: 100%;
                    background-color: #0b1a14;
                    overflow: hidden;
                }
                iframe {
                    width: 100%;
                    height: 100%;
                    border: 0;
                    filter: contrast(1.05) saturate(0.95);
                }
            </style>
        </head>
        <body>
            <iframe
                src="$embedUrl"
                allowfullscreen=""
                loading="lazy"
                referrerpolicy="no-referrer-when-downgrade">
            </iframe>
        </body>
        </html>
        """.trimIndent()
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            WebView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                setBackgroundColor(android.graphics.Color.parseColor("#0B1A14"))
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.cacheMode = WebSettings.LOAD_DEFAULT
                webViewClient = WebViewClient()
                loadDataWithBaseURL(
                    "https://maps.google.com",
                    htmlContent,
                    "text/html",
                    "UTF-8",
                    null
                )
            }
        }
    )
}

private fun checkHasLocationPermission(context: Context): Boolean {
    val fine = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val coarse = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    return fine || coarse
}

@SuppressLint("MissingPermission")
private fun getBestLastKnownLocation(
    context: Context,
    locationManager: LocationManager
): Location? {
    if (!checkHasLocationPermission(context)) return null
    val providers = listOf(
        LocationManager.GPS_PROVIDER,
        LocationManager.NETWORK_PROVIDER,
        LocationManager.PASSIVE_PROVIDER
    )
    var bestLocation: Location? = null
    for (provider in providers) {
        val loc = runCatching { locationManager.getLastKnownLocation(provider) }.getOrNull()
        if (loc != null && (bestLocation == null || loc.accuracy < bestLocation.accuracy)) {
            bestLocation = loc
        }
    }
    return bestLocation
}

@SuppressLint("MissingPermission")
private fun requestLiveLocationUpdates(
    context: Context,
    locationManager: LocationManager,
    listener: LocationListener
) {
    if (!checkHasLocationPermission(context)) return
    val providers = listOf(
        LocationManager.GPS_PROVIDER,
        LocationManager.NETWORK_PROVIDER
    )
    for (provider in providers) {
        runCatching {
            if (locationManager.isProviderEnabled(provider)) {
                locationManager.requestLocationUpdates(provider, 5000L, 10f, listener)
            }
        }
    }
}

fun calculateDistanceToKitchenMeters(userLat: Double, userLng: Double): Float {
    val results = FloatArray(1)
    Location.distanceBetween(
        userLat,
        userLng,
        FoodStopRepository.KITCHEN_LATITUDE,
        FoodStopRepository.KITCHEN_LONGITUDE,
        results
    )
    return results[0]
}
