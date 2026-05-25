package io.github.sophon.fightingnerd.feat.moveList.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    //TODO:
}
