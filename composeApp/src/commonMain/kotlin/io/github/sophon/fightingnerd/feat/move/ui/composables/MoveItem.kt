package io.github.sophon.fightingnerd.feat.move.ui.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.unit.dp
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.move_list_field_damage
import fightingnerd.composeapp.generated.resources.move_list_field_guard
import fightingnerd.composeapp.generated.resources.move_list_field_on_block
import fightingnerd.composeapp.generated.resources.move_list_field_on_counter
import fightingnerd.composeapp.generated.resources.move_list_field_on_hit
import fightingnerd.composeapp.generated.resources.move_list_field_startup
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.fightingnerd.feat.move.model.Property
import io.github.sophon.fightingnerd.feat.move.model.icon
import io.github.sophon.fightingnerd.feat.move.ui.MoveListState
import io.github.sophon.fightingnerd.feat.move.ui.MoveListState.Companion.toUiMove
import io.github.sophon.fightingnerd.feat.move.ui.UiMove
import io.github.sophon.fightingnerd.feat.quiz.ui.quiz.components.VideoPlayer
import io.github.sophon.fightingnerd.theme.FightingNerdTheme
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions
import io.github.sophon.fightingnerd.theme.nerdTypography
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun MoveItem(
    uiMove: UiMove,
    onMoveClick: () -> Unit,
    isExpanded: Boolean,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                onClick = onMoveClick,
                indication = ripple(color = MaterialTheme.colorScheme.primaryContainer)
            )
            .clip(RoundedCornerShape(nerdDimensions.cornerDefault))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(nerdDimensions.componentPadding)
    ) {
        Header(
            input = uiMove.move.input,
            propertySet = uiMove.propertySet,
        )

        InfoFieldRow(
            fields = listOf(
                stringResource(Res.string.move_list_field_startup) to uiMove.move.startup,
                stringResource(Res.string.move_list_field_guard) to uiMove.move.guard,
                stringResource(Res.string.move_list_field_damage) to uiMove.move.damage,
            )
        )
        Spacer(Modifier.height(nerdDimensions.componentPaddingTight))

        InfoFieldRow(
            fields = listOf(
                stringResource(Res.string.move_list_field_on_hit) to uiMove.move.onHit,
                stringResource(Res.string.move_list_field_on_block) to uiMove.move.onBlock,
                stringResource(Res.string.move_list_field_on_counter) to uiMove.move.onCH,
            )
        )

        if (uiMove.isExpandable()) {
            Spacer(Modifier.height(nerdDimensions.componentPaddingTight))
            ExpansionIndicator(isExpanded = isExpanded)

            if (isExpanded) {
                Details(uiMove.move)
            }
        }
    }
}

@Composable
private fun Header(
    input: String,
    propertySet: Set<Property>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = input,
                style = nerdTypography.titleLarge,
                color = nerdColorPalette.textPrimary,
                maxLines = 2,
                modifier = Modifier.weight(1f),
            )

            Properties(
                propertySet = propertySet
            )
        }

        HorizontalDivider(
            modifier = Modifier
                .padding(vertical = nerdDimensions.listRowPaddingVertical)
                .background(nerdColorPalette.dividerSubtle)
        )
    }
}

@Composable
private fun FieldColumn(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier) {
        Box(modifier = Modifier.defaultMinSize(minHeight = 24.dp)) {
            content()
        }
        Text(
            text = label.uppercase(),
            style = nerdTypography.labelMedium,
            color = nerdColorPalette.textSecondary,
        )
    }
}

@Composable
private fun InfoFieldRow(
    fields: List<Pair<String, String?>>,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(nerdDimensions.inlineGapTight),
    ) {
        fields.forEach { (label, value) ->
            FieldColumn(
                label = label,
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = value ?: "-",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun Properties(
    propertySet: Set<Property>,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(nerdDimensions.componentGapTight),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        propertySet.forEach { property ->
            Image(
                painter = painterResource(property.icon()),
                contentDescription = property.name,
                modifier = Modifier.size(nerdDimensions.iconInline),
            )
        }
    }
}

@Composable
private fun ExpansionIndicator(
    isExpanded: Boolean,
    modifier: Modifier = Modifier
) {
    val chevronFlip by animateFloatAsState(
        targetValue = if (isExpanded) -1f else 1f,
        label = "chevronFlip",
    )

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth(),
    ) {
        Icon(
            imageVector = Icons.Outlined.ExpandMore,
            contentDescription = null,
            tint = nerdColorPalette.textPrimary,
            modifier = Modifier
                .size(nerdDimensions.iconInline)
                .graphicsLayer { scaleY = chevronFlip }
        )
    }
}

@Composable
private fun Details(
    move: Move,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
    ) {
        Spacer(Modifier.height(nerdDimensions.componentPaddingTight))

        move.urls.videoId?.let { videoUrl ->
            VideoPlayer(videoUrl)
            Spacer(Modifier.height(nerdDimensions.componentPaddingTight))
        }

        move.notes.takeIf { it.isNotEmpty() }?.let { noteList ->
            NotesSection(noteList)
            Spacer(Modifier.height(nerdDimensions.componentPaddingTight))
        }
    }
}

@Composable
private fun NotesSection(
    noteList: List<String>,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .fillMaxWidth()
    ) {
        noteList.forEach { note ->
            Text(
                text = "· $note",
                style = nerdTypography.bodyMedium,
                color = nerdColorPalette.textPrimary,
                textAlign = TextAlign.Start
            )
        }
    }
}


//region PREVIEW
@Preview
@Composable
private fun CollapsedItemPreview() {
    FightingNerdTheme {
        MoveItem(
            uiMove = MoveListState.PREVIEW.fullMoveList.values.last().toUiMove(),
            onMoveClick = {},
            isExpanded = false,
        )
    }
}

@Preview
@Composable
private fun ExpandedItemPreview() {
    FightingNerdTheme {
        MoveItem(
            uiMove = MoveListState.PREVIEW.fullMoveList.values.last().toUiMove(),
            onMoveClick = {},
            isExpanded = true,
        )
    }
}
//endregion