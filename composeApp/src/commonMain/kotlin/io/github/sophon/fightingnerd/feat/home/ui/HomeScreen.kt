package io.github.sophon.fightingnerd.feat.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.sophon.core.feature.Game
import io.github.sophon.fightingnerd.feat.home.ui.composables.WidgetSection
import io.github.sophon.fightingnerd.theme.AppTheme
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
        modifier = modifier,
    )
}

@Composable
private fun Content(
    state: HomeViewState,
    onExpandWidget: (Game) -> Unit,
    onCharacterClick: (gameId: String, characterId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier.fillMaxSize(),
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(paddingValues)
        ) {
            WidgetSection(
                widgetList = state.gameWidgetList,
                onExpandWidget = onExpandWidget,
                onCharacterClick = onCharacterClick,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}


//region PREVIEW
@Composable
@Preview
private fun DarkHomeScreenPreview() {
    AppTheme(darkTheme = true) {
        Content(
            state = HomeViewState.PREVIEW,
            onExpandWidget = {},
            onCharacterClick = {_, _ -> },
        )
    }
}

@Composable
@Preview
private fun LightHomeScreenPreview() {
    AppTheme(darkTheme = false) {
        Content(
            state = HomeViewState.PREVIEW,
            onExpandWidget = {},
            onCharacterClick = {_, _ -> },
        )
    }
}
//endregion