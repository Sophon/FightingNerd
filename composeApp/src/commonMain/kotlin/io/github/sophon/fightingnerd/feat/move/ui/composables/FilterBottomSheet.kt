package io.github.sophon.fightingnerd.feat.move.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.fightingnerd.feat.move.ui.MoveListState
import io.github.sophon.fightingnerd.theme.FightingNerdTheme
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions
import io.github.sophon.fightingnerd.theme.nerdTypography
import io.github.sophon.wikiwavu.integration.model.TekkenFilters
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FilterBottomSheet(
    filterSheet: MoveListState.FilterSheet,
    onFilterChipClick: (Filter) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(nerdDimensions.componentPadding)
        ) {
            ChipSection(
                filterSheet = filterSheet,
                onFilterChipClick = onFilterChipClick,
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
        modifier = modifier.fillMaxWidth(),
    ) {
        filterSheet.filterSet.forEach { filter ->
            val isActive = filter in filterSheet.activeFilterSet
            FilterChip(
                selected = isActive,
                onClick = { onFilterChipClick(filter) },
                label = {
                    Text(
                        text = filter::class.simpleName?.uppercase() ?: "UNKNOWN",
                        style = nerdTypography.labelMedium,
                        color = if (isActive) nerdColorPalette.accent else nerdColorPalette.textSecondary,
                    )
                },
            )
        }
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
                filterSet = setOf(
                    TekkenFilters.Heat,
                    TekkenFilters.Homing,
                    TekkenFilters.PowerCrush
                ),
            ),
            onFilterChipClick = {},
            onDismiss = {},
        )
    }
}
//endregion