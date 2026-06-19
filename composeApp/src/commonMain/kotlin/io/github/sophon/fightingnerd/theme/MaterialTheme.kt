package io.github.sophon.fightingnerd.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

/**
 * FAILSAFE ONLY
 *
 * This is the fall-back for Material 3 components.
 * Use `nerdColorPalette` instead.
 */

private val defaultColorScheme = darkColorScheme(
    primary = AccentDefault,
    onPrimary = TextPrimaryDefault,
    primaryContainer = AccentPressedDefault,
    onPrimaryContainer = TextPrimaryDefault,
    secondary = TextSecondaryDefault,
    onSecondary = BackgroundDefault,
    secondaryContainer = SurfaceHighDefault,
    onSecondaryContainer = TextPrimaryDefault,
    tertiary = TextTertiaryDefault,
    onTertiary = TextPrimaryDefault,
    tertiaryContainer = SurfaceDefault,
    onTertiaryContainer = TextPrimaryDefault,
    error = ErrorDefault,
    onError = TextPrimaryDefault,
    errorContainer = SurfaceDefault,
    onErrorContainer = ErrorDefault,
    background = BackgroundDefault,
    onBackground = TextPrimaryDefault,
    surface = BackgroundDefault,
    onSurface = TextPrimaryDefault,
    surfaceVariant = SurfaceDefault,
    onSurfaceVariant = TextSecondaryDefault,
    surfaceTint = Color.Transparent,
    outline = DividerDefault,
    outlineVariant = DividerSubtleDefault,
    scrim = ScrimDefault,
    inverseSurface = TextPrimaryDefault,
    inverseOnSurface = BackgroundDefault,
    inversePrimary = AccentHoverDefault,
    surfaceDim = BackgroundDefault,
    surfaceBright = SurfacePressedDefault,
    surfaceContainerLowest = BackgroundDefault,
    surfaceContainerLow = SurfaceDefault,
    surfaceContainer = SurfaceDefault,
    surfaceContainerHigh = SurfaceHighDefault,
    surfaceContainerHighest = SurfacePressedDefault,
)

@Composable
internal fun FightingNerdTheme(
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(localFightingNerdColorPalette provides defaultFightingNerdColorPalette) {
        MaterialTheme(
            colorScheme = defaultColorScheme,
            typography = fightingNerdTypography(),
            content = content,
        )
    }
}
