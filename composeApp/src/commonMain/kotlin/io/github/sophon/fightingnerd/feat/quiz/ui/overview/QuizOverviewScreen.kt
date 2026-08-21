package io.github.sophon.fightingnerd.feat.quiz.ui.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.compose_multiplatform
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.fightingnerd.LocalBottomBarPadding
import io.github.sophon.fightingnerd.core.ui.components.CircularLoader
import io.github.sophon.fightingnerd.feat.quiz.model.QuizGameWidget
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions
import io.github.sophon.fightingnerd.theme.nerdTypography
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun QuizOverviewScreen(
    onNavigateToQuiz: (gameId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val vm = koinViewModel<QuizOverviewVM>()
    val state by vm.state.collectAsStateWithLifecycle()

    WidgetSection(
        widgetList = state.quizGameWidgetList,
        onPlay = { gameId -> onNavigateToQuiz(gameId) },
        modifier = modifier,
    )
}

@Composable
private fun WidgetSection(
    widgetList: ImmutableList<QuizGameWidget>,
    onPlay: (gameId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = nerdDimensions.screenPaddingHorizontal,
                vertical = nerdDimensions.screenPaddingVertical,
            ),
    ) {
        LazyColumn(
            contentPadding = LocalBottomBarPadding.current,
        ) {
            widgetList.forEach { widget ->
                item(key = "header_${widget.game.id}") {
                    WidgetHeader(
                        game = widget.game,
                        featureName = widget.featureName,
                        isReady = widget.isReady,
                        onPlay = { onPlay(widget.game.id) },
                    )
                }

                item {
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun WidgetHeader(
    game: Game,
    featureName: String,
    isReady: Boolean,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(nerdDimensions.cornerDefault))
            .background(color = nerdColorPalette.surface)
            .padding(horizontal = nerdDimensions.screenPaddingHorizontal)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onPlay,
                enabled = isReady,
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                space = nerdDimensions.inlineGap,
                alignment = Alignment.Start,
            ),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
        ) {
            AsyncImage(
                model = game.iconUrl,
                contentDescription = featureName,
                placeholder = painterResource(Res.drawable.compose_multiplatform),
                error = painterResource(Res.drawable.compose_multiplatform),
                modifier = Modifier
                    .size(nerdDimensions.iconHeadline)
                    .padding(nerdDimensions.inlineGapTight)
            )

            Text(
                text = game.shortDisplayName.uppercase(),
                style = nerdTypography.headlineSmall,
                color = nerdColorPalette.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Spacer(Modifier.width(nerdDimensions.inlineGap))

        if (isReady) {
            Icon(
                imageVector = Icons.Outlined.PlayArrow,
                contentDescription = null,
                tint = nerdColorPalette.textPrimary,
                modifier = Modifier.size(nerdDimensions.iconLarge)
            )
        } else {
            CircularLoader(
                color = nerdColorPalette.textSecondary,
                modifier = Modifier.size(nerdDimensions.iconLarge)
            )
        }
    }
}