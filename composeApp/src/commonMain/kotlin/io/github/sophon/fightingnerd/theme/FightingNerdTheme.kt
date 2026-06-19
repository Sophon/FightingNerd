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
 * - `accent` - Brand red. Logo, primary CTA, selected nav.
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


@Immutable
internal data class FightingNerdColorPalette(
    val accent: Color = AccentDefault,
    val accentHover: Color = AccentHoverDefault,
    val accentPressed: Color = AccentPressedDefault,
    val background: Color = BackgroundDefault,
    val surface: Color = SurfaceDefault,
    val surfaceHigh: Color = SurfaceHighDefault,
    val surfacePressed: Color = SurfacePressedDefault,
    val textPrimary: Color = TextPrimaryDefault,
    val textSecondary: Color = TextSecondaryDefault,
    val textTertiary: Color = TextTertiaryDefault,
    val textDisabled: Color = TextDisabledDefault,
    val divider: Color = DividerDefault,
    val dividerSubtle: Color = DividerSubtleDefault,
    val success: Color = SuccessDefault,
    val warning: Color = WarningDefault,
    val error: Color = ErrorDefault,
    val scrim: Color = ScrimDefault,
)

internal val localFightingNerdColorPalette = staticCompositionLocalOf { FightingNerdColorPalette() }

internal val nerdColorPalette: FightingNerdColorPalette
    @Composable
    @ReadOnlyComposable
    get() = localFightingNerdColorPalette.current
