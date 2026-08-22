package io.github.sophon.fightingnerd.feat.move.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.move_list_field_damage
import fightingnerd.composeapp.generated.resources.move_list_field_guard
import fightingnerd.composeapp.generated.resources.move_list_field_on_block
import fightingnerd.composeapp.generated.resources.move_list_field_on_hit
import fightingnerd.composeapp.generated.resources.move_list_field_startup
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.fightingnerd.feat.move.ui.composables.BookmarksButton
import io.github.sophon.fightingnerd.feat.move.ui.composables.FilterBottomSheet
import io.github.sophon.fightingnerd.feat.move.ui.composables.MoveItem
import io.github.sophon.fightingnerd.feat.move.ui.composables.MoveTopBar
import io.github.sophon.fightingnerd.theme.FightingNerdTheme
import io.github.sophon.fightingnerd.theme.nerdDimensions
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun MoveListScreen(
    gameId: String,
    characterId: String,
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val vm = koinViewModel<MoveListVM>(
        parameters = { parametersOf(gameId, characterId) }
    )
    val state by vm.state.collectAsStateWithLifecycle()
    val filteredMoves by vm.filteredMoves.collectAsStateWithLifecycle()

    Content(
        state = state,
        onExit = onExit,
        moveList = filteredMoves,
        onMoveClick = vm::onMoveClick,
        searchQuery = state.searchQuery,
        onSearch = vm::onSearchInput,
        onFilterClick = vm::onDisplayFilter,
        onClearFilters = vm::onClearFilters,
        onFilterChipClick = vm::toggleFilter,
        onChangeStartup = vm::onChangeStartup,
        onChangeOnBlock = vm::onChangeOnBlock,
        onChangeOnHit = vm::onChangeOnHit,
        onBookmarkSwitch = vm::onBookmarkSwitch,
        onBookmarkClose = vm::onBookmarkClose,
        modifier = modifier,
    )
}

@Composable
private fun Content(
    state: MoveListState,
    onExit: () -> Unit,
    moveList: ImmutableList<UiMove>,
    onMoveClick: (moveId: String) -> Unit,
    searchQuery: String?,
    onSearch: (query: String?) -> Unit,
    onFilterClick: (Boolean) -> Unit,
    onFilterChipClick: (Filter) -> Unit,
    onClearFilters: () -> Unit,
    onChangeStartup: (MoveListState.FilterSheet.MinMax?) -> Unit,
    onChangeOnBlock: (MoveListState.FilterSheet.MinMax?) -> Unit,
    onChangeOnHit: (MoveListState.FilterSheet.MinMax?) -> Unit,
    onBookmarkSwitch: () -> Unit,
    onBookmarkClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val currentOnBookmarkClose by rememberUpdatedState(onBookmarkClose)
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .collect { inProgress ->
                if (inProgress) currentOnBookmarkClose()
            }
    }

    Scaffold(
        topBar = {
            MoveTopBar(
                onExit = onExit,
                characterName = state.character?.displayName.orEmpty(),
                searchQuery = searchQuery,
                onSearch = onSearch,
                onDisplayFilterSheet = { onFilterClick(true) },
                isFilterActive = state.filterSheet.isFilterActive,
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
                expandedMoveId = state.expandedMoveId,
                listState = listState,
            )

            if (state.filterSheet.isVisible) {
                FilterBottomSheet(
                    filterSheet = state.filterSheet,
                    onClear = onClearFilters,
                    onFilterChipClick = onFilterChipClick,
                    onChangeStartup = onChangeStartup,
                    onChangeOnBlock = onChangeOnBlock,
                    onChangeOnHit = onChangeOnHit,
                    onDismiss = { onFilterClick(false) },
                )
            }

            BookmarksButton(
                bookmarks = state.bookmarks,
                onBookmarkSwitch = onBookmarkSwitch,
                onBookmarkClick = { index ->
                    scope.launch { listState.animateScrollToItem(index) }
                    onBookmarkClose()
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = nerdDimensions.componentGap)
            )
        }
    }
}

@Composable
private fun MoveList(
    moveList: ImmutableList<UiMove>,
    expandedMoveId: String?,
    onMoveClick: (moveId: String) -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
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
        ) { uiMove ->
            MoveItem(
                uiMove = uiMove,
                onMoveClick = { onMoveClick(uiMove.id) },
                isExpanded = (uiMove.id == expandedMoveId)
            )
        }
    }
}


//region PREVIEW
private val previewMoves: ImmutableList<UiMove> = persistentListOf(
    UiMove(
        id = "nina-b12",
        input = "b1,2",
        name = "Dark Jab > Hell Stab",
        coreFields = persistentListOf(
            UiMove.Field(Res.string.move_list_field_startup, "i12"),
            UiMove.Field(Res.string.move_list_field_guard, "h,m"),
            UiMove.Field(Res.string.move_list_field_damage, "12,20"),
            UiMove.Field(Res.string.move_list_field_on_block, "-12"),
            UiMove.Field(Res.string.move_list_field_on_hit, "+8"),
        ),
        optionalFields = persistentListOf(),
    ),
    UiMove(
        id = "nina-b1+2",
        input = "b1+2",
        name = "Blindside",
        coreFields = persistentListOf(
            UiMove.Field(Res.string.move_list_field_startup, "i16"),
            UiMove.Field(Res.string.move_list_field_guard, "m"),
            UiMove.Field(Res.string.move_list_field_on_block, "+0"),
            UiMove.Field(Res.string.move_list_field_on_hit, "+4"),
        ),
        optionalFields = persistentListOf(),
    ),
)

@Composable
@Preview
private fun MoveListPreview() {
    FightingNerdTheme {
        Content(
            state = MoveListState.PREVIEW,
            onExit = {},
            moveList = previewMoves,
            onMoveClick = {},
            searchQuery = null,
            onFilterClick = {},
            onFilterChipClick = {},
            onChangeStartup = {},
            onChangeOnBlock = {},
            onChangeOnHit = {},
            onClearFilters = {},
            onSearch = {},
            onBookmarkSwitch = {},
            onBookmarkClose = {},
        )
    }
}

@Composable
@Preview
private fun MoveListSearchPreview() {
    FightingNerdTheme {
        Content(
            state = MoveListState.PREVIEW,
            onExit = {},
            moveList = previewMoves,
            onMoveClick = {},
            searchQuery = "",
            onFilterClick = {},
            onFilterChipClick = {},
            onChangeStartup = {},
            onChangeOnBlock = {},
            onChangeOnHit = {},
            onClearFilters = {},
            onSearch = {},
            onBookmarkSwitch = {},
            onBookmarkClose = {},
        )
    }
}
//endregion
