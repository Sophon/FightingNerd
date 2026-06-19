package io.github.sophon.fightingnerd.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color


/**
 * `accent` is reserved - use only for: logo, primary CTA, selected/active nav state,
 * high-importance properties (Heat, Homing). Never for body text, dividers, backgrounds,
 * hover/pressed on non-CTAs, decorative accents, or error states.
 *
 * Space allocation per screen: most space = `background`, next = text on background,
 * cherry on top = `accent`. If a screen looks red-heavy, it's wrong.
 *
 * System colors (`success`/`warning`/`error`) are status feedback only, never decorative.
 * Errors use `error` (orange), never `accent` (red).
 *
 * Roles:
 * - `accent` - Brand red. Logo, primary CTA, selected nav, Heat/Homing tags.
 * - `accentHover` - Brighter red. Hover state on accent surfaces.
 * - `accentPressed` - Darker red. Pressed state on accent surfaces.
 * - `background` - App background. The dominant color on every screen.
 * - `surface` - Elevated surface: cards, sheets, list row containers.
 * - `surfaceHigh` - Higher elevation: dialogs, popups, menus.
 * - `surfacePressed` - Highest elevation / pressed states on neutral surfaces.
 * - `textPrimary` - Headlines, body text, primary content.
 * - `textSecondary` - Subtitles, supporting info, secondary labels.
 * - `textTertiary` - Timestamps, metadata, hints.
 * - `textDisabled` - Disabled text and icons.
 * - `divider` - Strong dividers, focused outlines.
 * - `dividerSubtle` - Subtle dividers, unfocused outlines.
 * - `success` - Confirmation feedback only (toasts, checkmarks).
 * - `warning` - Non-blocking alerts.
 * - `error` - Error states, destructive actions, blocking alerts.
 * - `scrim` - Modal scrims and overlays (use with alpha at call site).
 */
internal val nerdColorPalette: FightingNerdColorPalette
    @Composable
    @ReadOnlyComposable
    get() = localFightingNerdColorPalette.current

@Immutable
internal data class FightingNerdColorPalette(
    val accent: Color = Color.Unspecified,
    val accentHover: Color = Color.Unspecified,
    val accentPressed: Color = Color.Unspecified,
    val background: Color = Color.Unspecified,
    val surface: Color = Color.Unspecified,
    val surfaceHigh: Color = Color.Unspecified,
    val surfacePressed: Color = Color.Unspecified,
    val textPrimary: Color = Color.Unspecified,
    val textSecondary: Color = Color.Unspecified,
    val textTertiary: Color = Color.Unspecified,
    val textDisabled: Color = Color.Unspecified,
    val divider: Color = Color.Unspecified,
    val dividerSubtle: Color = Color.Unspecified,
    val success: Color = Color.Unspecified,
    val warning: Color = Color.Unspecified,
    val error: Color = Color.Unspecified,
    val scrim: Color = Color.Unspecified,
)

internal val defaultFightingNerdColorPalette = FightingNerdColorPalette(
    accent = AccentDefault,
    accentHover = AccentHoverDefault,
    accentPressed = AccentPressedDefault,
    background = BackgroundDefault,
    surface = SurfaceDefault,
    surfaceHigh = SurfaceHighDefault,
    surfacePressed = SurfacePressedDefault,
    textPrimary = TextPrimaryDefault,
    textSecondary = TextSecondaryDefault,
    textTertiary = TextTertiaryDefault,
    textDisabled = TextDisabledDefault,
    divider = DividerDefault,
    dividerSubtle = DividerSubtleDefault,
    success = SuccessDefault,
    warning = WarningDefault,
    error = ErrorDefault,
    scrim = ScrimDefault,
)

internal val localFightingNerdColorPalette = staticCompositionLocalOf { FightingNerdColorPalette() }

