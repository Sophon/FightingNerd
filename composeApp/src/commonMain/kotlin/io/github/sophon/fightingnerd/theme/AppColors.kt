package io.github.sophon.fightingnerd.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * App specific colors outside of the M3 scheme.
 * UI specific color to be added here.
 */
@Immutable
internal data class AppColorPalette(
    val profitGreen: Color = Color.Unspecified,
    val lossRed: Color = Color.Unspecified,
)

internal val lightProfitGreenColor = Color(color = 0xFF32de84)
internal val lightLossRedColor = Color(color = 0xFFD2122E)

internal val darkProfitGreenColor = Color(color = 0xFF32de84)
internal val darkLossRedColor = Color(color = 0xFFD2122E)

internal val lightAppColorPalette = AppColorPalette(
    profitGreen = lightProfitGreenColor,
    lossRed = lightLossRedColor,
)

internal val darkAppColorPalette = AppColorPalette(
    profitGreen = darkProfitGreenColor,
    lossRed = darkLossRedColor,
)

internal val localAppColorPalette = compositionLocalOf { AppColorPalette() }