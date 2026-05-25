package io.github.sophon.fightingnerd.feat.moveList.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import io.github.sophon.fightingnerd.feat.moveList.ui.MoveListState
import io.github.sophon.fightingnerd.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun MoveItem(
    move: MoveListState.UiMove,
    onMoveClick: (id: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(16.dp)
    ) {
        Header(
            input = move.input
        )

        InfoFieldRow(
            fields = listOf(
                "startup" to move.startup,
                "level" to move.level,
                "properties" to move.propertySet.joinToString(),
            )
        )
        Spacer(Modifier.height(8.dp))

        InfoFieldRow(
            fields = listOf(
                "on hit" to move.onHit,
                "on block" to move.onBlock,
                "on counter" to move.onCounter,
            )
        )

        //TODO: show more
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
                    style = MaterialTheme.typography.titleMedium,
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