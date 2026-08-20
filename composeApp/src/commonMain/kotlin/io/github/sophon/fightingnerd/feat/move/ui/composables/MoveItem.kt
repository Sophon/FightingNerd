package io.github.sophon.fightingnerd.feat.move.ui.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import coil3.compose.SubcomposeAsyncImage
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.move_list_field_damage
import fightingnerd.composeapp.generated.resources.move_list_field_guard
import fightingnerd.composeapp.generated.resources.move_list_field_on_block
import fightingnerd.composeapp.generated.resources.move_list_field_on_hit
import fightingnerd.composeapp.generated.resources.move_list_field_startup
import io.github.sophon.fightingnerd.core.ui.components.CircularLoader
import io.github.sophon.fightingnerd.feat.move.model.Property
import io.github.sophon.fightingnerd.feat.move.ui.UiMove
import io.github.sophon.fightingnerd.feat.move.ui.icon
import io.github.sophon.fightingnerd.feat.quiz.ui.quiz.components.VideoPlayer
import io.github.sophon.fightingnerd.theme.FightingNerdTheme
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions
import io.github.sophon.fightingnerd.theme.nerdTypography
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

private const val COUNT_MAX_PER_ROW = 3

@Composable
internal fun MoveItem(
    uiMove: UiMove,
    onMoveClick: () -> Unit,
    isExpanded: Boolean,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isExpandable = remember { uiMove.isExpandable() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                enabled = isExpandable,
                interactionSource = interactionSource,
                onClick = onMoveClick,
                indication = ripple(color = MaterialTheme.colorScheme.primaryContainer)
            )
            .clip(RoundedCornerShape(nerdDimensions.cornerDefault))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(nerdDimensions.componentPadding)
    ) {
        Header(
            input = uiMove.input,
            name = uiMove.name,
            propertySet = uiMove.propertySet,
        )

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(nerdDimensions.inlineGap),
            maxItemsInEachRow = COUNT_MAX_PER_ROW,
            verticalArrangement = Arrangement.spacedBy(nerdDimensions.componentPaddingTight),
        ) {
            uiMove.coreFields.forEach { field ->
                FieldColumn(
                    field = field,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        if (isExpandable) {
            Spacer(Modifier.height(nerdDimensions.componentPaddingTight))
            ExpansionIndicator(isExpanded = isExpanded)

            if (isExpanded) {
                Details(uiMove)
            }
        }
    }
}

@Composable
private fun Header(
    input: String,
    name: String?,
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

        if (name.isNullOrBlank().not()) {
            Spacer(Modifier.height(nerdDimensions.componentGapTight))
            Text(
                text = name,
                style = nerdTypography.labelSmall,
                color = nerdColorPalette.textSecondary,
            )
            Spacer(Modifier.height(nerdDimensions.componentGapTight))
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
    field: UiMove.Field,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = field.value ?: "-",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.defaultMinSize(minHeight = 24.dp),
        )
        Text(
            text = stringResource(field.label).uppercase(),
            style = nerdTypography.labelMedium,
            color = nerdColorPalette.textSecondary,
        )
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
                modifier = Modifier.size(nerdDimensions.iconDefault),
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
    uiMove: UiMove,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
    ) {
        uiMove.urls.videoUrl?.let { videoUrl ->
            VideoPlayer(videoUrl)
            Spacer(Modifier.height(nerdDimensions.componentPaddingTight))
        }

        when {
            uiMove.urls.hitboxImageList.isNotEmpty() -> {
                MoveImage(uiMove.urls.hitboxImageList.first())
                Spacer(Modifier.height(nerdDimensions.componentPaddingTight))
            }
            uiMove.urls.moveImageList.isNotEmpty() -> {
                MoveImage(uiMove.urls.moveImageList.first())
                Spacer(Modifier.height(nerdDimensions.componentPaddingTight))
            }
        }

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(nerdDimensions.inlineGapTight),
            maxItemsInEachRow = COUNT_MAX_PER_ROW,
            verticalArrangement = Arrangement.spacedBy(nerdDimensions.componentPaddingTight),
        ) {
            uiMove.optionalFields.forEach { field ->
                FieldColumn(
                    field = field,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        if (uiMove.optionalFields.isNotEmpty()) {
            Spacer(Modifier.height(nerdDimensions.componentPaddingTight))
        }

        uiMove.notes.takeIf { it.isNotEmpty() }?.let { noteList ->
            NotesSection(noteList)
            Spacer(Modifier.height(nerdDimensions.componentPaddingTight))
        }
    }
}

@Composable
private fun MoveImage(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth()
    ) {
        SubcomposeAsyncImage(
            model = imageUrl,
            contentDescription = null,
            loading = { CircularLoader() },
            modifier = modifier,
        )
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
private val previewUiMove = UiMove(
    id = "nina-hub1",
    input = "H.ub1",
    name = "Neck Hunter: Villain",
    propertySet = persistentSetOf(Property.Heat, Property.Homing),
    coreFields = persistentListOf(
        UiMove.Field(Res.string.move_list_field_startup, "i24"),
        UiMove.Field(Res.string.move_list_field_guard, "h"),
        UiMove.Field(Res.string.move_list_field_damage, "25"),
        UiMove.Field(Res.string.move_list_field_on_block, "+8"),
        UiMove.Field(Res.string.move_list_field_on_hit, "+60a"),
    ),
    optionalFields = persistentListOf(),
    notes = persistentListOf(
        "Strong Aerial Tailspin",
        "Homing",
        "Consumes 150F of remaining Heat time",
    ),
)

@Preview
@Composable
private fun CollapsedItemPreview() {
    FightingNerdTheme {
        MoveItem(
            uiMove = previewUiMove,
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
            uiMove = previewUiMove,
            onMoveClick = {},
            isExpanded = true,
        )
    }
}
//endregion