package io.github.sophon.fightingnerd.feat.home.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.compose_multiplatform
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.fightingnerd.LocalBottomBarPadding
import io.github.sophon.fightingnerd.feat.home.ui.HomeViewState
import io.github.sophon.fightingnerd.feat.home.ui.HomeViewState.GameWidget
import io.github.sophon.fightingnerd.feat.home.ui.HomeViewState.GameWidget.Character
import io.github.sophon.fightingnerd.theme.FightingNerdTheme
import io.github.sophon.fightingnerd.theme.ThemeMode
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun WidgetSection(
    widgetList: List<GameWidget>,
    onExpandWidget: (Game) -> Unit,
    onCharacterClick: (gameId: String, characterId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
    ) {
        LazyColumn(
            contentPadding = LocalBottomBarPadding.current,
        ) {
            widgetList.forEach { widget ->
                item(key = "header_${widget.game.id}") {
                    WidgetHeader(
                        game = widget.game,
                        featureName = widget.featureName,
                        isExpanded = widget.isExpanded,
                        onExpandClick = onExpandWidget,
                        isLoading = widget.isLoading,
                    )
                }

                item(key = "characters_${widget.game.id}") {
                    CharacterMatrix(
                        isExpanded = widget.isExpanded,
                        characterList = widget.characterList,
                        onCharacterClick = { characterId ->
                            onCharacterClick(widget.game.id, characterId)
                        }
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
internal fun WidgetHeader(
    game: Game,
    featureName: String,
    isExpanded: Boolean,
    onExpandClick: (Game) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val shape = if (isExpanded) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    } else {
        RoundedCornerShape(16.dp)
    }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(color = MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(horizontal = 8.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { onExpandClick(game) },
                enabled = isLoading.not(),
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                space = 8.dp,
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
                    .size(64.dp)
                    .padding(8.dp)
            )

            Text(
                text = game.displayName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Spacer(Modifier.width(8.dp))

        val chevronFlip by animateFloatAsState(
            targetValue = if (isExpanded) -1f else 1f,
            label = "chevronFlip",
        )
        if (isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(32.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.ExpandMore,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(32.dp)
                    .graphicsLayer { scaleY = chevronFlip }
            )
        }
    }
}

@Composable
private fun BoxWithConstraintsScope.CharacterMatrix(
    isExpanded: Boolean,
    characterList: List<Character>,
    onCharacterClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val columns = (maxWidth / CHARACTER_CARD_WIDTH.dp).toInt().coerceAtLeast(1)

    AnimatedVisibility(
        visible = isExpanded,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
    ) {
        Column(
             modifier = modifier,
        ) {
            val rows = characterList.chunked(columns)
            rows.forEachIndexed { index, rowCharacters ->
                CharacterRow(
                    characterList = rowCharacters,
                    onCharacterClick = onCharacterClick,
                    isLast = index == rows.lastIndex,
                )
            }
        }
    }
}

@Composable
internal fun CharacterRow(
    characterList: List<Character>,
    onCharacterClick: (String) -> Unit,
    isLast: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(
            space = 8.dp,
            alignment = Alignment.CenterHorizontally,
        ),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isLast) Modifier.clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                else Modifier
            )
            .background(color = MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(bottom = 8.dp)
    ) {
        characterList.forEach { character ->
            CharacterPanel(
                character = character,
                onClick = { onCharacterClick(character.id) },
            )
        }
    }
}

@Composable
private fun CharacterPanel(
    character: Character,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .width(CHARACTER_CARD_WIDTH.dp)
            .height(128.dp)
            .clickable(
                interactionSource = interactionSource,
                onClick = onClick,
                indication = ripple(color = MaterialTheme.colorScheme.primaryContainer),
                enabled = character.isLoading.not(),
            )
            .clip(RoundedCornerShape(8.dp))
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
            .padding(vertical = 8.dp)
    ) {
        Box {
            AsyncImage(
                model = character.iconUrl,
                contentDescription = character.displayName,
                placeholder = painterResource(Res.drawable.compose_multiplatform),
                error = painterResource(Res.drawable.compose_multiplatform),
                modifier = Modifier.size(64.dp)
            )

            if (character.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        Text(
            text = character.displayName,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private const val CHARACTER_CARD_WIDTH = 100


//region PREVIEW
@Preview
@Composable
private fun WidgetSectionPreview() {
    FightingNerdTheme {
        WidgetSection(
            widgetList = HomeViewState.PREVIEW.gameWidgetList,
            onExpandWidget = {},
            onCharacterClick = {_, _ -> },
        )
    }
}
//endregion