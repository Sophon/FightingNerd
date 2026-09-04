package io.github.sophon.fightingnerd.core.ui.components

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.compose_multiplatform
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.fightingnerd.LocalBottomBarPadding
import io.github.sophon.fightingnerd.theme.FightingNerdTheme
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions
import io.github.sophon.fightingnerd.theme.nerdTypography
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun GameWidget(
    gameFeatureList: ImmutableList<GameFeature>,
    onExpandWidget: (Game) -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    onLeadingClick: () -> Unit = {},
    content: @Composable (gameId: String) -> Unit,
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
    ) {
        LazyColumn(
            contentPadding = LocalBottomBarPadding.current,
        ) {
            gameFeatureList.forEach { gameFeature ->
                item(key = "header_${gameFeature.game.id}") {
                    WidgetHeader(
                        game = gameFeature.game,
                        featureName = gameFeature.featureName,
                        isExpanded = gameFeature.isExpanded,
                        onExpandClick = onExpandWidget,
                        isLoading = gameFeature.isLoading,
                        leadingIcon = leadingIcon,
                        onLeadingClick = onLeadingClick,
                    )
                }

                item(key = "content_${gameFeature.game.id}") {
                    content(gameFeature.game.id)
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
    leadingIcon: ImageVector? = null,
    onLeadingClick: () -> Unit = {},
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
            if (leadingIcon != null) {
                IconButton(
                    onClick = onLeadingClick,
                    modifier = Modifier
                        .size(nerdDimensions.iconHeadline)
                        .padding(nerdDimensions.inlineGapTight)
                ) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = nerdColorPalette.textPrimary,
                        modifier = Modifier.size(nerdDimensions.iconLarge)
                    )
                }
            } else {
                AsyncImage(
                    model = game.iconUrl,
                    contentDescription = featureName,
                    placeholder = painterResource(Res.drawable.compose_multiplatform),
                    error = painterResource(Res.drawable.compose_multiplatform),
                    modifier = Modifier
                        .size(nerdDimensions.iconHeadline)
                        .padding(nerdDimensions.inlineGapTight)
                )
            }

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


//region PREVIEW
internal object GameWidgets {
    private fun mockCharacters(): ImmutableList<GameFeature.UiCharacter> {
        val names = listOf("Zuzana", "Eva", "Karolina", "Marcela", "Zdenka", "Hana")
        val mocked = names.mapIndexed { index, name ->
            GameFeature.UiCharacter(
                id = "char_$index",
                displayName = name,
                queryName = "",
                hasMoves = true,
            )
        }.toImmutableList()
        return mocked
    }

    private fun mockWidget(
        game: Game,
        featureName: String,
        isExpanded: Boolean,
    ): GameFeature {
        val widget = GameFeature(
            game = game,
            featureName = featureName,
            characterList = mockCharacters(),
            isExpanded = isExpanded,
        )
        return widget
    }

    val PREVIEW: ImmutableList<GameFeature> = persistentListOf(
        mockWidget(Game.Tekken8, "Wavu Wiki", isExpanded = true),
        mockWidget(Game.StreetFighter6, "SuperCombo", isExpanded = false),
        mockWidget(Game.KoFXV, "Dream Cancel", isExpanded = false),
    )
}

@Preview
@Composable
private fun WidgetSectionPreview() {
    FightingNerdTheme {
        val widgetList = GameWidgets.PREVIEW
        GameWidget(
            gameFeatureList = widgetList,
            onExpandWidget = {},
        ) { gameId ->
            val game = widgetList.first { it.game.id == gameId }
            CharacterMatrix(
                isExpanded = game.isExpanded,
                characterList = game.characterList,
                onCharacterClick = {},
            )
        }
    }
}

@Preview
@Composable
private fun LeadingIconPreview() {
    FightingNerdTheme {
        val widgetList = GameWidgets.PREVIEW
        GameWidget(
            gameFeatureList = widgetList,
            onExpandWidget = {},
            leadingIcon = Icons.Outlined.PlayArrow,
        ) { gameId ->
            val game = widgetList.first { it.game.id == gameId }
            CharacterMatrix(
                isExpanded = game.isExpanded,
                characterList = game.characterList,
                onCharacterClick = {},
            )
        }
    }
}
//endregion