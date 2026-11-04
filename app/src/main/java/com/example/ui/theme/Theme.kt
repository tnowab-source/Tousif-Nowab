package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val FoodStopColorScheme = darkColorScheme(
    primary = LuxuryGold,
    onPrimary = DarkTextOnGold,
    primaryContainer = HeaderEmeraldStart,
    onPrimaryContainer = LightChampagneGold,
    secondary = LightChampagneGold,
    onSecondary = DarkTextOnGold,
    secondaryContainer = SurfaceEmerald,
    onSecondaryContainer = SoftCreamText,
    tertiary = BrightGold,
    background = DeepJungleBlack,
    onBackground = SoftCreamText,
    surface = RichEmeraldBg,
    onSurface = SoftCreamText,
    surfaceVariant = SurfaceEmerald,
    onSurfaceVariant = MutedGoldText,
    outline = LuxuryGold
)

@Composable
fun FoodStopTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = FoodStopColorScheme,
        typography = Typography,
        content = content
    )
}
