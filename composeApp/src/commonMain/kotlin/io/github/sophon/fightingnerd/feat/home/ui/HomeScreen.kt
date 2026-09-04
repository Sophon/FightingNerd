package io.github.sophon.fightingnerd.feat.home.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.fightingnerd.core.ui.components.GameWidget
import io.github.sophon.fightingnerd.theme.FightingNerdTheme
import io.github.sophon.fightingnerd.theme.nerdDimensions
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun HomeScreen(
    onNavigateToMoveList: (gameId: String, characterId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val vm = koinViewModel<HomeVM>()
    val state by vm.state.collectAsStateWithLifecycle()

    Content(
        state = state,
        onExpandWidget = vm::onExpandWidget,
        onCharacterClick = onNavigateToMoveList,
        onRefresh = vm::refresh,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    state: HomeViewState,
    onExpandWidget: (Game) -> Unit,
    onCharacterClick: (gameId: String, characterId: String) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    PullToRefreshBox(
        isRefreshing = false,
        onRefresh = onRefresh,
        modifier = modifier,
    ) {
        GameWidget(
            widgetList = state.gameWidgetList,
            onExpandWidget = onExpandWidget,
            onCharacterClick = onCharacterClick,
            modifier = Modifier
                .padding(horizontal = nerdDimensions.screenPaddingHorizontal)
        )
    }
}


//region PREVIEW
@Composable
@Preview
private fun HomeScreenPreview() {
    FightingNerdTheme {
        Content(
            state = HomeViewState.PREVIEW,
            onExpandWidget = {},
            onCharacterClick = {_, _ -> },
            onRefresh = {},
        )
    }
}
//endregion
