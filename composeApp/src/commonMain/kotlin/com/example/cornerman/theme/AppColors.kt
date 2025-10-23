package com.example.cornerman.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * App specific colors outside of the M3 scheme.
 * UI specific color to be added here.
 */
@Immutable
data class AppColorPalette(
    val profitGreen: Color = Color.Unspecified,
    val lossRed: Color = Color.Unspecified,
)

val lightProfitGreenColor = Color(color = 0xFF32de84)
val lightLossRedColor = Color(color = 0xFFD2122E)

val darkProfitGreenColor = Color(color = 0xFF32de84)
val darkLossRedColor = Color(color = 0xFFD2122E)

val lightAppColorPalette = AppColorPalette(
    profitGreen = lightProfitGreenColor,
    lossRed = lightLossRedColor,
)

val darkAppColorPalette = AppColorPalette(
    profitGreen = darkProfitGreenColor,
    lossRed = darkLossRedColor,
)

val localAppColorPalette = compositionLocalOf { AppColorPalette() }