package io.github.sophon.fightingnerd.core.ui.components

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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import coil3.compose.AsyncImage
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.compose_multiplatform
import io.github.sophon.fightingnerd.theme.FightingNerdTheme
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions
import io.github.sophon.fightingnerd.theme.nerdTypography
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun GameWidget(
    iconUrl: String?,
    title: String,
    isExpanded: Boolean,
    isLoading: Boolean,
    onExpandClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingAction: IconAction? = null,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        WidgetHeader(
            iconUrl = iconUrl,
            title = title,
            isExpanded = isExpanded,
            isLoading = isLoading,
            onExpandClick = onExpandClick,
            leadingAction = leadingAction,
        )

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
        ) {
            content()
        }
    }
}

@Composable
private fun WidgetHeader(
    iconUrl: String?,
    title: String,
    isExpanded: Boolean,
    onExpandClick: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    leadingAction: IconAction? = null,
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
                onClick = onExpandClick,
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
            if (leadingAction != null) {
                IconActionButton(
                    action = leadingAction,
                    modifier = Modifier
                        .size(nerdDimensions.iconHeadline)
                        .padding(nerdDimensions.inlineGapTight)
                )
            } else {
                AsyncImage(
                    model = iconUrl,
                    contentDescription = title,
                    placeholder = painterResource(Res.drawable.compose_multiplatform),
                    error = painterResource(Res.drawable.compose_multiplatform),
                    modifier = Modifier
                        .size(nerdDimensions.iconHeadline)
                        .padding(nerdDimensions.inlineGapTight)
                )
            }

            Text(
                text = title,
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
private val previewCharacters = persistentListOf(
    CharacterCard(id = "1", displayName = "Zuzana"),
    CharacterCard(id = "2", displayName = "Eva"),
    CharacterCard(id = "3", displayName = "Karolina"),
    CharacterCard(id = "4", displayName = "Marcela"),
    CharacterCard(id = "5", displayName = "Zdenka"),
    CharacterCard(id = "6", displayName = "Hana"),
)

@Preview
@Composable
private fun GameWidgetExpandedPreview() {
    FightingNerdTheme {
        GameWidget(
            iconUrl = null,
            title = "TEKKEN 8",
            isExpanded = true,
            isLoading = false,
            onExpandClick = {},
        ) {
            CharacterMatrix(
                characterList = previewCharacters,
                onCharacterClick = {},
            )
        }
    }
}

@Preview
@Composable
private fun GameWidgetCollapsedPreview() {
    FightingNerdTheme {
        GameWidget(
            iconUrl = null,
            title = "STREET FIGHTER 6",
            isExpanded = false,
            isLoading = false,
            onExpandClick = {},
        ) {
            CharacterMatrix(
                characterList = previewCharacters,
                onCharacterClick = {},
            )
        }
    }
}

@Preview
@Composable
private fun GameWidgetLoadingPreview() {
    FightingNerdTheme {
        GameWidget(
            iconUrl = null,
            title = "GUILTY GEAR STRIVE",
            isExpanded = false,
            isLoading = true,
            onExpandClick = {},
        ) {}
    }
}

@Preview
@Composable
private fun GameWidgetLeadingIconPreview() {
    FightingNerdTheme {
        GameWidget(
            iconUrl = null,
            title = "TEKKEN 8",
            isExpanded = true,
            isLoading = false,
            onExpandClick = {},
            leadingAction = IconAction(
                icon = Icons.Outlined.PlayArrow,
                onClick = {},
            ),
        ) {
            CharacterMatrix(
                characterList = previewCharacters,
                onCharacterClick = {},
            )
        }
    }
}

@Preview
@Composable
private fun GameWidgetLeadingIconDisabledPreview() {
    FightingNerdTheme {
        GameWidget(
            iconUrl = null,
            title = "TEKKEN 8",
            isExpanded = true,
            isLoading = false,
            onExpandClick = {},
            leadingAction = IconAction(
                icon = Icons.Outlined.PlayArrow,
                onClick = {},
                isEnabled = false,
            ),
        ) {
            CharacterMatrix(
                characterList = previewCharacters,
                onCharacterClick = {},
            )
        }
    }
}
//endregion
