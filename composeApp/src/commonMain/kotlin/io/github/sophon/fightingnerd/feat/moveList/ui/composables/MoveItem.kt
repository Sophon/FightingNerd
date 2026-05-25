package io.github.sophon.fightingnerd.feat.moveList.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import fightingnerd.composeapp.generated.resources.move_list_field_level
import fightingnerd.composeapp.generated.resources.move_list_field_on_block
import fightingnerd.composeapp.generated.resources.move_list_field_on_counter
import fightingnerd.composeapp.generated.resources.move_list_field_on_hit
import fightingnerd.composeapp.generated.resources.move_list_field_properties
import fightingnerd.composeapp.generated.resources.move_list_field_startup
import io.github.sophon.fightingnerd.feat.moveList.ui.MoveListState
import io.github.sophon.fightingnerd.theme.AppTheme
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
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
    ) {
        Header(
            input = move.input
        )

        InfoFieldRow(
            fields = listOf(
                stringResource(Res.string.move_list_field_startup) to move.startup,
                stringResource(Res.string.move_list_field_level) to move.level,
                stringResource(Res.string.move_list_field_properties) to move.propertySet.joinToString(),
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
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun Header(
    input: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = input,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
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
            .padding(4.dp),
    ) {
        fields.forEach { (label, value) ->
            Column(
                modifier = modifier.weight(1f),
            ) {
                Text(
                    text = value ?: "-",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}


//region PREVIEW
@Preview
@Composable
private fun ItemPreviewDark() {
    AppTheme(darkTheme = true) {
        MoveItem(
            move = MoveListState.PREVIEW.uiMoveList.last(),
            onMoveClick = {},
        )
    }
}

@Preview
@Composable
private fun ItemPreviewLight() {
    AppTheme(darkTheme = false) {
        MoveItem(
            move = MoveListState.PREVIEW.uiMoveList.first(),
            onMoveClick = {},
        )
    }
}
//endregion