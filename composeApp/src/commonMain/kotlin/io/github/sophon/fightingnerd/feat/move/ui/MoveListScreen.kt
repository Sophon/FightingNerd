package io.github.sophon.fightingnerd.feat.move.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import io.github.sophon.fightingnerd.feat.move.model.MediaAvailability
import io.github.sophon.fightingnerd.feat.move.ui.MoveListState.Field
import io.github.sophon.fightingnerd.feat.move.ui.MoveListState.UiMove
import io.github.sophon.fightingnerd.feat.move.ui.composables.BookmarksButton
import io.github.sophon.fightingnerd.feat.move.ui.composables.CharacterInfoBox
import io.github.sophon.fightingnerd.feat.move.ui.composables.FilterBottomSheet
import io.github.sophon.fightingnerd.feat.move.ui.composables.MoveItem
import io.github.sophon.fightingnerd.feat.move.ui.composables.MoveTopBar
import io.github.sophon.fightingnerd.feat.move.ui.composables.SharedMove
import io.github.sophon.fightingnerd.feat.share.ShareCaptureHost
import io.github.sophon.fightingnerd.feat.share.ShareSheet
import io.github.sophon.fightingnerd.infrastructure.toPngBytes
import io.github.sophon.fightingnerd.theme.FightingNerdTheme
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
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
    val pendingShareMoveId by vm.pendingShareMoveId.collectAsStateWithLifecycle()
    val shareSheet: ShareSheet = koinInject()
    val onExitWithReview: () -> Unit = {
        vm.onScreenExit()
        onExit()
    }

    Box(modifier = modifier) {
        Content(
            state = state,
            onExit = onExitWithReview,
            moveList = filteredMoves,
            onMoveClick = vm::onMoveClick,
            onShareClick = vm::onShare,
            searchQuery = state.searchQuery,
            onSearch = vm::onSearchInput,
            onFilterClick = vm::onDisplayFilter,
            onClearFilters = vm::onClearFilters,
            onFilterChipClick = vm::toggleFilter,
            onChangeSlider = vm::onChangeSlider,
            onBookmarkSwitch = vm::onBookmarkSwitch,
            onBookmarkClose = vm::onBookmarkClose,
            onDownload = vm::onDownloadMedia,
            onWipe = vm::onWipeMedia,
            onExpandCharacter = vm::onExpandCharacter,
            onCollapseCharacter = vm::onCollapseCharacter,
        )

        pendingShareMoveId?.let { id ->
            filteredMoves.firstOrNull { it.id == id }?.let { uiMove ->
                ShareCaptureHost(
                    content = { SharedMove(uiMove, uiMove.id == state.expandedMoveId) },
                    onCaptured = { bmp ->
                        shareSheet.shareImage(bmp.toPngBytes(), "move_${uiMove.id}.png")
                        vm.onSharedDone()
                    },
                )
            }
        }
    }
}

@Composable
private fun Content(
    state: MoveListState,
    onExit: () -> Unit,
    moveList: ImmutableList<UiMove>,
    onMoveClick: (moveId: String) -> Unit,
    onShareClick: (moveId: String) -> Unit,
    searchQuery: String?,
    onSearch: (query: String?) -> Unit,
    onFilterClick: (Boolean) -> Unit,
    onFilterChipClick: (Filter) -> Unit,
    onClearFilters: () -> Unit,
    onChangeSlider: (MoveListState.FilterSheet.FrameSlider, MoveListState.FilterSheet.MinMax?) -> Unit,
    onBookmarkSwitch: () -> Unit,
    onBookmarkClose: () -> Unit,
    onDownload: () -> Unit,
    onWipe: () -> Unit,
    onExpandCharacter: () -> Unit,
    onCollapseCharacter: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val currentOnBookmarkClose by rememberUpdatedState(onBookmarkClose)
    val currentOnCollapseCharacter by rememberUpdatedState(onCollapseCharacter)
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .collect { inProgress ->
                if (inProgress) {
                    currentOnBookmarkClose()
                    currentOnCollapseCharacter()
                }
            }
    }

    Scaffold(
        topBar = {
            MoveTopBar(
                onExit = onExit,
                characterName = state.character?.displayName.orEmpty(),
                isCharacterExpanded = (state.character?.isExpanded == true),
                canExpandCharacter = (state.character?.canExpand == true),
                onExpandCharacter = onExpandCharacter,
                searchQuery = searchQuery,
                onSearch = onSearch,
                onDisplayFilterSheet = { onFilterClick(true) },
                isFilterActive = state.filterSheet.isFilterActive,
                mediaState = state.mediaAvailability,
                onDownload = onDownload,
                onWipe = onWipe,
            )
        },
        contentWindowInsets = WindowInsets(0),
        modifier = modifier,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(nerdColorPalette.background)
                .padding(paddingValues),
        ) {
            MoveList(
                moveList = moveList,
                onMoveClick = onMoveClick,
                onShareClick = onShareClick,
                expandedMoveId = state.expandedMoveId,
                listState = listState,
            )

            if (state.character?.canExpand == true) {
                AnimatedVisibility(
                    visible = state.character.isExpanded,
                    enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                    exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut(),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .padding(
                            start = nerdDimensions.screenPaddingHorizontal,
                            end = nerdDimensions.screenPaddingHorizontal,
                            bottom = nerdDimensions.screenPaddingVertical,
                        ),
                ) {
                    CharacterInfoBox(character = state.character)
                }
            }

            if (state.filterSheet.isVisible) {
                FilterBottomSheet(
                    filterSheet = state.filterSheet,
                    onClear = onClearFilters,
                    onFilterChipClick = onFilterChipClick,
                    onChangeSlider = onChangeSlider,
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
                    .padding(bottom = nerdDimensions.screenPaddingVertical)
            )
        }
    }
}

@Composable
private fun MoveList(
    moveList: ImmutableList<UiMove>,
    expandedMoveId: String?,
    onMoveClick: (moveId: String) -> Unit,
    onShareClick: (moveId: String) -> Unit,
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
                onShareClick = onShareClick,
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
            Field(Res.string.move_list_field_startup, "i12"),
            Field(Res.string.move_list_field_guard, "h,m"),
            Field(Res.string.move_list_field_damage, "12,20"),
            Field(Res.string.move_list_field_on_block, "-12"),
            Field(Res.string.move_list_field_on_hit, "+8"),
        ),
        optionalFields = persistentListOf(),
    ),
    UiMove(
        id = "nina-b1+2",
        input = "b1+2",
        name = "Blindside",
        coreFields = persistentListOf(
            Field(Res.string.move_list_field_startup, "i16"),
            Field(Res.string.move_list_field_guard, "m"),
            Field(Res.string.move_list_field_on_block, "+0"),
            Field(Res.string.move_list_field_on_hit, "+4"),
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
            onShareClick = {},
            searchQuery = null,
            onFilterClick = {},
            onFilterChipClick = {},
            onChangeSlider = { _, _ -> },
            onClearFilters = {},
            onSearch = {},
            onBookmarkSwitch = {},
            onBookmarkClose = {},
            onDownload = {},
            onWipe = {},
            onExpandCharacter = {},
            onCollapseCharacter = {},
        )
    }
}

@Composable
@Preview
private fun MoveListSearchPreview() {
    FightingNerdTheme {
        Content(
            state = MoveListState.PREVIEW.copy(mediaAvailability = MediaAvailability.Downloaded),
            onExit = {},
            moveList = previewMoves,
            onMoveClick = {},
            onShareClick = {},
            searchQuery = "",
            onFilterClick = {},
            onFilterChipClick = {},
            onChangeSlider = { _, _ -> },
            onClearFilters = {},
            onSearch = {},
            onBookmarkSwitch = {},
            onBookmarkClose = {},
            onDownload = {},
            onWipe = {},
            onExpandCharacter = {},
            onCollapseCharacter = {},
        )
    }
}

@Composable
@Preview
private fun MoveListDownloadPreview() {
    FightingNerdTheme {
        Content(
            state = MoveListState.PREVIEW.copy(
                mediaAvailability = MediaAvailability.Downloading(
                    downloaded = 40,
                    total = 100,
                ),
            ),
            onExit = {},
            moveList = previewMoves,
            onMoveClick = {},
            onShareClick = {},
            searchQuery = null,
            onFilterClick = {},
            onFilterChipClick = {},
            onChangeSlider = { _, _ -> },
            onClearFilters = {},
            onSearch = {},
            onBookmarkSwitch = {},
            onBookmarkClose = {},
            onDownload = {},
            onWipe = {},
            onExpandCharacter = {},
            onCollapseCharacter = {},
        )
    }
}
//endregion
