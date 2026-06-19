package io.github.sophon.fightingnerd.feat.move.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.sophon.fightingnerd.feat.move.ui.composables.MoveItem
import io.github.sophon.fightingnerd.theme.FightingNerdTheme
import io.github.sophon.fightingnerd.theme.nerdDimensions
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

    Content(
        state = state,
        onMoveClick = { /*TODO*/ },
        modifier = modifier,
    )
}

@Composable
private fun Content(
    state: MoveListState,
    onMoveClick: (id: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
    ) { paddingValues ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(nerdDimensions.listRowPaddingVertical),
            modifier = Modifier
                .padding(paddingValues)
                .padding(
                    horizontal = nerdDimensions.screenPaddingHorizontal,
                    vertical = nerdDimensions.screenPaddingVertical,
                )
                .background(MaterialTheme.colorScheme.surface)
        ) {
            items(
                items = state.uiMoveList,
                key = { it.id }
            ) { move ->
                MoveItem(
                    move = move,
                    onMoveClick = { onMoveClick(move.id) },
                )
            }
        }
    }
}


//region PREVIEW
@Composable
@Preview
private fun MoveListPreviewDark() {
    FightingNerdTheme {
        Content(
            state = MoveListState.PREVIEW,
            onMoveClick = {},
        )
    }
}
//endregion