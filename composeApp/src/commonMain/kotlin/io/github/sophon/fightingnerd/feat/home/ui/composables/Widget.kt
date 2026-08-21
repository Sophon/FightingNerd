package io.github.sophon.fightingnerd.feat.home.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.compose_multiplatform
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.fightingnerd.LocalBottomBarPadding
import io.github.sophon.fightingnerd.core.ui.components.CircularLoader
import io.github.sophon.fightingnerd.feat.home.ui.HomeViewState
import io.github.sophon.fightingnerd.feat.home.ui.HomeViewState.GameWidget
import io.github.sophon.fightingnerd.feat.home.ui.HomeViewState.GameWidget.UiCharacter
import io.github.sophon.fightingnerd.theme.FightingNerdTheme
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions
import io.github.sophon.fightingnerd.theme.nerdTypography
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun WidgetSection(
    widgetList: ImmutableList<GameWidget>,
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
private fun WidgetHeader(
    game: Game,
    featureName: String,
    isExpanded: Boolean,
    onExpandClick: (Game) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val shape = if (isExpanded) {
        RoundedCornerShape(topStart = nerdDimensions.cornerDefault, topEnd = nerdDimensions.cornerDefault)
    } else {
        RoundedCornerShape(nerdDimensions.cornerDefault)
    }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(color = nerdColorPalette.surface)
            .padding(horizontal = nerdDimensions.screenPaddingHorizontal)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { onExpandClick(game) },
                enabled = isLoading.not(),
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

        val chevronFlip by animateFloatAsState(
            targetValue = if (isExpanded) -1f else 1f,
            label = "chevronFlip",
        )
        if (isLoading) {
            CircularLoader(
                color = nerdColorPalette.textSecondary,
                modifier = Modifier.size(nerdDimensions.iconLarge)
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.ExpandMore,
                contentDescription = null,
                tint = nerdColorPalette.accent,
                modifier = Modifier
                    .size(nerdDimensions.iconLarge)
                    .graphicsLayer { scaleY = chevronFlip }
            )
        }
    }
}

@Composable
private fun CharacterMatrix(
    isExpanded: Boolean,
    characterList: List<UiCharacter>,
    onCharacterClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = isExpanded,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(
                space = nerdDimensions.matrixGap,
                alignment = Alignment.CenterHorizontally,
            ),
            verticalArrangement = Arrangement.spacedBy(nerdDimensions.matrixGap),
            modifier = modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(
                        bottomStart = nerdDimensions.cornerDefault,
                        bottomEnd = nerdDimensions.cornerDefault,
                    ),
                )
                .background(color = nerdColorPalette.surface)
                .padding(nerdDimensions.componentPaddingTight),
        ) {
            characterList.forEach { character ->
                CharacterPanel(
                    character = character,
                    onClick = { onCharacterClick(character.id) },
                )
            }
        }
    }
}

@Composable
private fun CharacterPanel(
    character: UiCharacter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(nerdDimensions.cornerDefault)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .width(CHARACTER_CARD_WIDTH.dp)
            .height(CHARACTER_CARD_HEIGHT.dp)
            .clickable(
                interactionSource = interactionSource,
                onClick = onClick,
                indication = ripple(color = nerdColorPalette.accent),
                enabled = character.isLoading.not(),
            )
            .clip(shape)
            .border(
                width = nerdDimensions.strokeThin,
                color = nerdColorPalette.dividerSubtle,
                shape = shape,
            )
            .padding(vertical = nerdDimensions.componentPaddingTight),
    ) {
        Box {
            AsyncImage(
                model = character.iconUrl,
                contentDescription = character.displayName,
                placeholder = painterResource(Res.drawable.compose_multiplatform),
                error = painterResource(Res.drawable.compose_multiplatform),
                modifier = Modifier.size(nerdDimensions.iconHeadline)
            )

            if (character.isLoading) {
                CircularLoader(
                    color = nerdColorPalette.textSecondary,
                    trackColor = Color.Transparent,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }

        Text(
            text = character.displayName,
            style = nerdTypography.titleMedium,
            color = nerdColorPalette.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private const val CHARACTER_CARD_WIDTH = 96
private const val CHARACTER_CARD_HEIGHT = 144


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