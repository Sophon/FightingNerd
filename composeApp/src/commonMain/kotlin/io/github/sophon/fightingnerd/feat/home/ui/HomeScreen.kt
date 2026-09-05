package io.github.sophon.fightingnerd.feat.home.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.fightingnerd.LocalBottomBarPadding
import io.github.sophon.fightingnerd.core.ui.components.CharacterCard
import io.github.sophon.fightingnerd.core.ui.components.CharacterMatrix
import io.github.sophon.fightingnerd.core.ui.components.GameWidget
import io.github.sophon.fightingnerd.theme.FightingNerdTheme
import io.github.sophon.fightingnerd.theme.nerdDimensions
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
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
        modifier = modifier
            .padding(
                horizontal = nerdDimensions.screenPaddingHorizontal,
            ),
    ) {
        LazyColumn(
            contentPadding = LocalBottomBarPadding.current,
        ) {
            state.gameFeatureList.forEach { feature ->
                item(key = "widget_${feature.game.id}") {
                    GameWidget(
                        iconUrl = feature.game.iconUrl,
                        title = feature.game.shortDisplayName.uppercase(),
                        isExpanded = feature.isExpanded,
                        isLoading = feature.isLoading,
                        onExpandClick = { onExpandWidget(feature.game) },
                    ) {
                        CharacterMatrix(
                            characterList = feature.characterList.toCharacterCards(),
                            onCharacterClick = { characterId ->
                                onCharacterClick(feature.game.id, characterId)
                            },
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

private fun ImmutableList<GameFeature.UiCharacter>.toCharacterCards(): ImmutableList<CharacterCard> {
    val cards = map { character ->
        CharacterCard(
            id = character.id,
            displayName = character.displayName,
            iconUrl = character.iconUrl,
            isLoading = character.isLoading,
        )
    }.toImmutableList()
    return cards
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
