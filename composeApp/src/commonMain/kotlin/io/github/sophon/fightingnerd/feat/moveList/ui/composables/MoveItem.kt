package io.github.sophon.fightingnerd.feat.moveList.ui.composables

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.move_list_field_damage
import fightingnerd.composeapp.generated.resources.move_list_field_guard
import fightingnerd.composeapp.generated.resources.move_list_field_on_block
import fightingnerd.composeapp.generated.resources.move_list_field_on_counter
import fightingnerd.composeapp.generated.resources.move_list_field_on_hit
import fightingnerd.composeapp.generated.resources.move_list_field_startup
import io.github.sophon.fightingnerd.feat.moveList.model.Property
import io.github.sophon.fightingnerd.feat.moveList.model.icon
import io.github.sophon.fightingnerd.feat.moveList.ui.MoveListState
import io.github.sophon.fightingnerd.theme.FightingNerdTheme
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions
import io.github.sophon.fightingnerd.theme.nerdTypography
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun MoveItem(
    move: MoveListState.UiMove,
    onMoveClick: () -> Unit,
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
            .clip(RoundedCornerShape(nerdDimensions.cornerSubtle))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(nerdDimensions.componentPadding)
    ) {
        Header(
            input = move.input,
            propertySet = move.propertySet,
        )

        InfoFieldRow(
            fields = listOf(
                stringResource(Res.string.move_list_field_startup) to move.startup,
                stringResource(Res.string.move_list_field_guard) to move.guard,
                stringResource(Res.string.move_list_field_damage) to move.damage,
            )
        )

        Spacer(Modifier.height(8.dp))

        InfoFieldRow(
            fields = listOf(
                stringResource(Res.string.move_list_field_on_hit) to move.onHit,
                stringResource(Res.string.move_list_field_on_block) to move.onBlock,
                stringResource(Res.string.move_list_field_on_counter) to move.onCounter,
            )
        )
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


//region PREVIEW
@Preview
@Composable
private fun ItemPreview() {
    FightingNerdTheme {
        MoveItem(
            move = MoveListState.PREVIEW.uiMoveList.last(),
            onMoveClick = {},
        )
    }
}
//endregion