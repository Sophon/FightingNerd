package io.github.sophon.fightingnerd.feat.moveList.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.sophon.fightingnerd.feat.moveList.ui.composables.MoveItem
import io.github.sophon.fightingnerd.theme.AppTheme
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
            modifier = Modifier
                .padding(paddingValues)
                .padding(4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            state.uiMoveList.forEach { move ->
                item(
                    key = move.id
                ) {
                    MoveItem(
                        move = move,
                        onMoveClick = { onMoveClick(move.id) },
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
        }
    }
}


//region PREVIEW
@Composable
@Preview
private fun MoveListPreviewDark() {
    AppTheme(darkTheme = true) {
        Content(
            state = MoveListState.PREVIEW,
            onMoveClick = {},
        )
    }
}

@Composable
@Preview
private fun MoveListPreviewLight() {
    AppTheme(darkTheme = false) {
        Content(
            state = MoveListState.PREVIEW,
            onMoveClick = {},
        )
    }
}
//endregion