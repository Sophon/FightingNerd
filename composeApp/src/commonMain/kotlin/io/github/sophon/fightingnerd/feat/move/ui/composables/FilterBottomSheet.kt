package io.github.sophon.fightingnerd.feat.move.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.move_list_filter_button_clear
import fightingnerd.composeapp.generated.resources.move_list_filter_label_on_block
import fightingnerd.composeapp.generated.resources.move_list_filter_label_on_hit
import fightingnerd.composeapp.generated.resources.move_list_filter_label_startup
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.fightingnerd.feat.move.ui.MoveListState
import io.github.sophon.fightingnerd.feat.move.ui.MoveListState.Companion.FRAME_MAX
import io.github.sophon.fightingnerd.feat.move.ui.MoveListState.Companion.FRAME_MIN
import io.github.sophon.fightingnerd.feat.move.ui.MoveListState.Companion.FRAME_MIN_STARTUP
import io.github.sophon.fightingnerd.theme.FightingNerdTheme
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions
import io.github.sophon.fightingnerd.theme.nerdTypography
import io.github.sophon.wikiwavu.integration.model.TekkenFilters
import kotlinx.collections.immutable.persistentSetOf
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FilterBottomSheet(
    filterSheet: MoveListState.FilterSheet,
    onClear: () -> Unit,
    onFilterChipClick: (Filter) -> Unit,
    onChangeStartup: (MoveListState.FilterSheet.MinMax?) -> Unit,
    onChangeOnBlock: (MoveListState.FilterSheet.MinMax?) -> Unit,
    onChangeOnHit: (MoveListState.FilterSheet.MinMax?) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isSliderDragging by remember { mutableStateOf(false) }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        sheetGesturesEnabled = isSliderDragging.not(),
        modifier = modifier,
    ) {
        val focusManager = LocalFocusManager.current
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    focusManager.clearFocus()
                }
                .padding(horizontal = nerdDimensions.componentPadding),
        ) {
            TextButton(
                onClick = onClear,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = nerdColorPalette.textPrimary,
                ),
            ) {
                Text(
                    text = stringResource(Res.string.move_list_filter_button_clear).uppercase(),
                    style = nerdTypography.labelLarge,
                )
            }
            Spacer(Modifier.height(nerdDimensions.sectionGap))

            ChipSection(
                filterSheet = filterSheet,
                onFilterChipClick = onFilterChipClick,
            )
            Spacer(Modifier.height(nerdDimensions.sectionGap))

            MinMaxSection(
                filterSheet = filterSheet,
                onChangeStartup = onChangeStartup,
                onChangeOnBlock = onChangeOnBlock,
                onChangeOnHit = onChangeOnHit,
                onDraggingChange = { isSliderDragging = it },
            )
        }
    }
}

@Composable
private fun ChipSection(
    filterSheet: MoveListState.FilterSheet,
    onFilterChipClick: (Filter) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(
            space = nerdDimensions.matrixGap,
            alignment = Alignment.CenterHorizontally,
        ),
        verticalArrangement = Arrangement.spacedBy(nerdDimensions.matrixGap),
        modifier = modifier
            .fillMaxWidth()
    ) {
        filterSheet.filterSet.forEach { filter ->
            val isActive = filter in filterSheet.activeFilterSet
            FilterChip(
                selected = isActive,
                onClick = { onFilterChipClick(filter) },
                label = {
                    Text(
                        text = filter.name.uppercase(),
                        style = nerdTypography.labelMedium,
                        color = if (isActive) nerdColorPalette.accent else nerdColorPalette.textPrimary,
                    )
                },
            )
        }
    }
}

@Composable
private fun MinMaxSection(
    filterSheet: MoveListState.FilterSheet,
    onChangeStartup: (MoveListState.FilterSheet.MinMax?) -> Unit,
    onChangeOnBlock: (MoveListState.FilterSheet.MinMax?) -> Unit,
    onChangeOnHit: (MoveListState.FilterSheet.MinMax?) -> Unit,
    onDraggingChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        SliderFilter(
            label = stringResource(Res.string.move_list_filter_label_startup),
            min = FRAME_MIN_STARTUP,
            max = FRAME_MAX,
            value = filterSheet.startup.minMax,
            thumbs = filterSheet.startup.thumbs,
            onChange = onChangeStartup,
            onDraggingChange = onDraggingChange,
        )
        Spacer(Modifier.height(nerdDimensions.inlineGapTight))

        SliderFilter(
            label = stringResource(Res.string.move_list_filter_label_on_block),
            min = FRAME_MIN,
            max = FRAME_MAX,
            value = filterSheet.onBlock.minMax,
            thumbs = filterSheet.onBlock.thumbs,
            onChange = onChangeOnBlock,
            onDraggingChange = onDraggingChange,
        )
        Spacer(Modifier.height(nerdDimensions.inlineGapTight))

        SliderFilter(
            label = stringResource(Res.string.move_list_filter_label_on_hit),
            min = FRAME_MIN,
            max = FRAME_MAX,
            value = filterSheet.onHit.minMax,
            thumbs = filterSheet.onHit.thumbs,
            onChange = onChangeOnHit,
            onDraggingChange = onDraggingChange,
        )
        Spacer(Modifier.height(nerdDimensions.inlineGapTight))
    }
}


//region PREVIEW
@Preview
@Composable
private fun FilterBottomSheetPreview() {
    FightingNerdTheme {
        FilterBottomSheet(
            filterSheet = MoveListState.FilterSheet(
                isVisible = true,
                filterSet = persistentSetOf(
                    TekkenFilters.Heat,
                    TekkenFilters.Homing,
                    TekkenFilters.PowerCrush
                ),
            ),
            onFilterChipClick = {},
            onDismiss = {},
            onChangeStartup = {},
            onChangeOnBlock = {},
            onChangeOnHit = {},
            onClear = {},
        )
    }
}
//endregion