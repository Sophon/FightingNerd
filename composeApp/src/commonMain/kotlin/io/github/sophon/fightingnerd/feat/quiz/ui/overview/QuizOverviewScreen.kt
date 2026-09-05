package io.github.sophon.fightingnerd.feat.quiz.ui.overview

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.fightingnerd.LocalBottomBarPadding
import io.github.sophon.fightingnerd.core.ui.components.CharacterMatrix
import io.github.sophon.fightingnerd.core.ui.components.GameWidget
import io.github.sophon.fightingnerd.core.ui.components.IconAction
import io.github.sophon.fightingnerd.theme.nerdDimensions
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun QuizOverviewScreen(
    onNavigateToQuiz: (gameId: String, characterId: String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val vm = koinViewModel<QuizOverviewVM>()
    val state by vm.state.collectAsStateWithLifecycle()

    Content(
        state = state,
        onExpandWidget = vm::onExpandWidget,
        onNavigateToQuiz = onNavigateToQuiz,
        modifier = modifier,
    )
}

@Composable
private fun Content(
    state: QuizOverviewState,
    onExpandWidget: (Game) -> Unit,
    onNavigateToQuiz: (gameId: String, characterId: String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        contentPadding = LocalBottomBarPadding.current,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = nerdDimensions.screenPaddingHorizontal),
    ) {
        state.quizGameWidgetList.forEach { widget ->
            item(key = "widget_${widget.game.id}") {
                GameWidget(
                    iconUrl = widget.game.iconUrl,
                    title = widget.game.shortDisplayName.uppercase(),
                    isExpanded = widget.isExpanded,
                    isLoading = widget.isReady.not(),
                    onExpandClick = { onExpandWidget(widget.game) },
                    leadingAction = IconAction(
                        icon = Icons.Outlined.PlayArrow,
                        onClick = { onNavigateToQuiz(widget.game.id, null) },
                        isEnabled = widget.isPlayable,
                    ),
                ) {
                    CharacterMatrix(
                        characterList = widget.characterList,
                        onCharacterClick = { characterId ->
                            onNavigateToQuiz(widget.game.id, characterId)
                        },
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}
