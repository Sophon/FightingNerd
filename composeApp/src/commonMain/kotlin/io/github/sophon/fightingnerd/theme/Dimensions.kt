package io.github.sophon.fightingnerd.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Screen-level:
 * - `screenPaddingHorizontal` - left/right padding inside a screen
 * - `screenPaddingVertical` - top/bottom padding inside a screen
 * - `sectionGap` - between unrelated sections within a screen
 * - `sectionGapLarge` - between major sections (e.g. screen title to first content)
 *
 * Component-level:
 * - `componentPadding` - default internal padding for cards, sheets, dialogs
 * - `componentPaddingTight` - tighter internal padding for compact components
 * - `componentGap` - between sibling components (button row, card-to-card)
 * - `componentGapTight` - between closely related sibling components
 *
 * Inline:
 * - `inlineGap` - gap between icon and label, or two inline elements
 * - `inlineGapTight` - gap for very tight inline pairings (badge + count)
 *
 * Component-specific paddings:
 * - `buttonPaddingHorizontal` / `buttonPaddingVertical` - inside buttons
 * - `chipPaddingHorizontal` / `chipPaddingVertical` - inside chips and tags
 * - `listRowPaddingHorizontal` / `listRowPaddingVertical` - inside list rows
 * - `topAppBarPaddingHorizontal` - inside the top app bar
 * - `dialogPadding` - inside dialogs
 *
 * Corners:
 * - `cornerSharp` - default for buttons, cards, chips, dialogs
 * - `cornerSubtle` - reserved for rare exceptions
 *
 * Strokes:
 * - `strokeThin` - dividers, default borders
 * - `strokeStrong` - focused outlines, emphasized borders
 *
 * Icon sizes:
 * - `iconInline` - alongside text
 * - `iconDefault` - top bar, buttons, list rows
 * - `iconLarge` - empty states, hero moments
 *
 * Heights:
 * - `topAppBarHeight` - top app bar
 * - `bottomNavHeight` - bottom nav bar
 * - `buttonHeight` - default button height
 * - `buttonHeightCompact` - compact button variant
 * - `minTouchTarget` - accessibility minimum
 */
@Immutable
internal data class FightingNerdDimensions(
    val screenPaddingHorizontal: Dp = Dp.Unspecified,
    val screenPaddingVertical: Dp = Dp.Unspecified,
    val sectionGap: Dp = Dp.Unspecified,
    val sectionGapLarge: Dp = Dp.Unspecified,
    val componentPadding: Dp = Dp.Unspecified,
    val componentPaddingTight: Dp = Dp.Unspecified,
    val componentGap: Dp = Dp.Unspecified,
    val componentGapTight: Dp = Dp.Unspecified,
    val inlineGap: Dp = Dp.Unspecified,
    val inlineGapTight: Dp = Dp.Unspecified,
    val buttonPaddingHorizontal: Dp = Dp.Unspecified,
    val buttonPaddingVertical: Dp = Dp.Unspecified,
    val chipPaddingHorizontal: Dp = Dp.Unspecified,
    val chipPaddingVertical: Dp = Dp.Unspecified,
    val listRowPaddingHorizontal: Dp = Dp.Unspecified,
    val listRowPaddingVertical: Dp = Dp.Unspecified,
    val topAppBarPaddingHorizontal: Dp = Dp.Unspecified,
    val dialogPadding: Dp = Dp.Unspecified,
    val cornerSharp: Dp = Dp.Unspecified,
    val cornerSubtle: Dp = Dp.Unspecified,
    val strokeThin: Dp = Dp.Unspecified,
    val strokeStrong: Dp = Dp.Unspecified,
    val iconInline: Dp = Dp.Unspecified,
    val iconDefault: Dp = Dp.Unspecified,
    val iconLarge: Dp = Dp.Unspecified,
    val topAppBarHeight: Dp = Dp.Unspecified,
    val bottomNavHeight: Dp = Dp.Unspecified,
    val buttonHeight: Dp = Dp.Unspecified,
    val buttonHeightCompact: Dp = Dp.Unspecified,
    val minTouchTarget: Dp = Dp.Unspecified,
)

internal val defaultFightingNerdDimensions = FightingNerdDimensions(
    screenPaddingHorizontal = 16.dp,
    screenPaddingVertical = 24.dp,
    sectionGap = 32.dp,
    sectionGapLarge = 48.dp,
    componentPadding = 16.dp,
    componentPaddingTight = 12.dp,
    componentGap = 16.dp,
    componentGapTight = 8.dp,
    inlineGap = 8.dp,
    inlineGapTight = 4.dp,
    buttonPaddingHorizontal = 24.dp,
    buttonPaddingVertical = 12.dp,
    chipPaddingHorizontal = 12.dp,
    chipPaddingVertical = 4.dp,
    listRowPaddingHorizontal = 16.dp,
    listRowPaddingVertical = 12.dp,
    topAppBarPaddingHorizontal = 16.dp,
    dialogPadding = 24.dp,
    cornerSharp = 0.dp,
    cornerSubtle = 4.dp,
    strokeThin = 1.dp,
    strokeStrong = 2.dp,
    iconInline = 16.dp,
    iconDefault = 24.dp,
    iconLarge = 48.dp,
    topAppBarHeight = 56.dp,
    bottomNavHeight = 80.dp,
    buttonHeight = 48.dp,
    buttonHeightCompact = 36.dp,
    minTouchTarget = 48.dp,
)

internal val localFightingNerdDimensions = staticCompositionLocalOf { FightingNerdDimensions() }

internal val nerdDimensions: FightingNerdDimensions
    @Composable
    @ReadOnlyComposable
    get() = localFightingNerdDimensions.current