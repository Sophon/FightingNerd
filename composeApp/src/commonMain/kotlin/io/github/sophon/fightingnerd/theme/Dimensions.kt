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
 * - `matrixGap` - used for matrices
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
 * - `iconHeadline` - with headlines
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
    val screenPaddingHorizontal: Dp = 16.dp,
    val screenPaddingVertical: Dp = 24.dp,
    val sectionGap: Dp = 32.dp,
    val sectionGapLarge: Dp = 48.dp,
    val matrixGap: Dp = 4.dp,
    val componentPadding: Dp = 16.dp,
    val componentPaddingTight: Dp = 12.dp,
    val componentGap: Dp = 16.dp,
    val componentGapTight: Dp = 8.dp,
    val inlineGap: Dp = 8.dp,
    val inlineGapTight: Dp = 4.dp,
    val buttonPaddingHorizontal: Dp = 24.dp,
    val buttonPaddingVertical: Dp = 12.dp,
    val chipPaddingHorizontal: Dp = 12.dp,
    val chipPaddingVertical: Dp = 4.dp,
    val listRowPaddingHorizontal: Dp = 16.dp,
    val listRowPaddingVertical: Dp = 8.dp,
    val topAppBarPaddingHorizontal: Dp = 16.dp,
    val dialogPadding: Dp = 24.dp,
    val cornerDefault: Dp = 8.dp,
    val cornerExtra: Dp = 16.dp,
    val strokeThin: Dp = 1.dp,
    val strokeStrong: Dp = 2.dp,
    val iconInline: Dp = 24.dp,
    val iconDefault: Dp = 32.dp,
    val iconLarge: Dp = 48.dp,
    val iconHeadline: Dp = 64.dp,
    val topAppBarHeight: Dp = 56.dp,
    val bottomNavHeight: Dp = 80.dp,
    val buttonHeight: Dp = 48.dp,
    val buttonHeightCompact: Dp = 36.dp,
    val minTouchTarget: Dp = 48.dp,
)

internal val localFightingNerdDimensions = staticCompositionLocalOf { FightingNerdDimensions() }

internal val nerdDimensions: FightingNerdDimensions
    @Composable
    @ReadOnlyComposable
    get() = localFightingNerdDimensions.current
