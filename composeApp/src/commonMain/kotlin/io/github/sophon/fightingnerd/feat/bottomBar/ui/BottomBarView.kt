package io.github.sophon.fightingnerd.feat.bottomBar.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun BottomBarView(
    modifier: Modifier = Modifier
) {
    val vm = koinViewModel<BottomBarVM>()
    val state by vm.state.collectAsStateWithLifecycle()

    BottomBarContent(
        state = state,
        onItemClick = vm::onItemClick,
    )
}


@Composable
private fun BottomBarContent(
    state: BottomBarState,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    //TODO:
}