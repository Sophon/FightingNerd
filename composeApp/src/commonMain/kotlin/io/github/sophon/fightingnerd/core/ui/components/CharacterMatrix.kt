package io.github.sophon.fightingnerd.core.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.compose_multiplatform
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions
import io.github.sophon.fightingnerd.theme.nerdTypography
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun CharacterMatrix(
    isExpanded: Boolean,
    characterList: ImmutableList<GameFeature.UiCharacter>,
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
    character: GameFeature.UiCharacter,
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