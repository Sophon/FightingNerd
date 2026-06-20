package io.github.sophon.fightingnerd.feat.move.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.fightingnerd.feat.move.ui.composables.FilterBottomSheet
import io.github.sophon.fightingnerd.feat.move.ui.composables.MoveItem
import io.github.sophon.fightingnerd.theme.FightingNerdTheme
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions
import io.github.sophon.fightingnerd.theme.nerdTypography
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun MoveListScreen(
    gameId: String,
    characterId: String,
    modifier: Modifier = Modifier,
) {
    val vm = koinViewModel<MoveListVM>(
        parameters = { parametersOf(gameId, characterId) }
    )
    val state by vm.state.collectAsStateWithLifecycle()

    val filteredMoves by remember(
        state.fullMoveList,
        state.filterSheet.activeFilterSet,
        state.filterSheet.activeSliderFilters,
    ) {
        derivedStateOf {
            state.fullMoveList.values.applyFilters(
                state.filterSheet.activeFilterSet + state.filterSheet.activeSliderFilters
            )
        }
    }

    Content(
        state = state,
        moveList = filteredMoves,
        onMoveClick = { /*TODO*/ },
        onFilterClick = vm::onDisplayFilter,
        onFilterChipClick = vm::toggleFilter,
        onChangeStartup = vm::onChangeStartup,
        onChangeOnBlock = vm::onChangeOnBlock,
        onChangeOnHit = vm::onChangeOnHit,
        modifier = modifier,
    )
}

@Composable
private fun Content(
    state: MoveListState,
    moveList: List<MoveListState.UiMove>,
    onMoveClick: (id: String) -> Unit,
    onFilterClick: (Boolean) -> Unit,
    onFilterChipClick: (Filter) -> Unit,
    onChangeStartup: (MoveListState.FilterSheet.MinMax?) -> Unit,
    onChangeOnBlock: (MoveListState.FilterSheet.MinMax?) -> Unit,
    onChangeOnHit: (MoveListState.FilterSheet.MinMax?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopBar(
                characterName = state.character?.displayName.orEmpty(),
                onDisplayFilterSheet = { onFilterClick(true) },
            )
        },
        modifier = modifier,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface),
        ) {
            MoveList(
                moveList = moveList,
                onMoveClick = onMoveClick,
            )

            if (state.filterSheet.isVisible) {
                FilterBottomSheet(
                    filterSheet = state.filterSheet,
                    onFilterChipClick = onFilterChipClick,
                    onChangeStartup = onChangeStartup,
                    onChangeOnBlock = onChangeOnBlock,
                    onChangeOnHit = onChangeOnHit,
                    onDismiss = { onFilterClick(false) },
                )
            }
        }
    }
}

@Composable
private fun MoveList(
    moveList: List<MoveListState.UiMove>,
    onMoveClick: (id: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    LaunchedEffect(moveList) {
        listState.scrollToItem(0)
    }
    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(nerdDimensions.listRowPaddingVertical),
        modifier = modifier
            .padding(horizontal = nerdDimensions.screenPaddingHorizontal),
    ) {
        items(
            items = moveList,
            key = { it.id },
        ) { move ->
            MoveItem(
                move = move,
                onMoveClick = { onMoveClick(move.id) },
            )
        }
    }
}

@Composable
private fun TopBar(
    characterName: String,
    onDisplayFilterSheet: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = nerdDimensions.screenPaddingHorizontal,
                vertical = nerdDimensions.screenPaddingVertical,
            )
    ) {
        Text(
            text = characterName.uppercase(),
            style = nerdTypography.displaySmall,
            color = nerdColorPalette.textPrimary,
            modifier = Modifier.weight(1f)
        )

        IconButton(
            onClick = onDisplayFilterSheet,
        ) {
            Icon(
                imageVector = Icons.Outlined.FilterList,
                contentDescription = "Filter moves",
                tint = nerdColorPalette.textPrimary,
            )
        }
    }
}


//region PREVIEW
@Composable
@Preview
private fun MoveListPreviewDark() {
    FightingNerdTheme {
        val state = MoveListState.PREVIEW
        Content(
            state = state,
            moveList = state.fullMoveList.values.applyFilters(emptySet()),
            onMoveClick = {},
            onFilterClick = {},
            onFilterChipClick = {},
            onChangeStartup = {},
            onChangeOnBlock = {},
            onChangeOnHit = {},
        )
    }
}
//endregion