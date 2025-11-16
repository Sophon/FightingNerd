package io.github.sophon.fightingnerd.screens.moveList.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.sophon.fightingnerd.screens.moveList.domain.UiMove
import io.github.sophon.fightingnerd.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun Section(
    title: String,
    moves: List<UiMove>,
    expandedNotes: Set<String>,
    onNotesExpandClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(color = MaterialTheme.colorScheme.surfaceVariant)
                .padding(8.dp)
        )

        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(topEnd = 8.dp, bottomStart = 8.dp, bottomEnd = 8.dp))
        ) {
            moves.forEach { move ->
                MoveItem(
                    move = move,
                    onNotesExpandClick = { onNotesExpandClick(move.id) },
                    isNotesExpanded = move.id in expandedNotes
                )
            }
        }
    }
}


//region PREVIEW
@Composable
@Preview(showBackground = true)
private fun SectionPreviewDark() {
    AppTheme(darkTheme = true) {
        Section(
            title = "Heat",
            moves = listOf(
                UiMove(
                    id = "df3+4",
                    input = "df3+4",
                    mandatoryFields = listOf(
                        UiMove.Field("Startup", "i22~24"),
                        UiMove.Field("OH", "+12g"),
                        UiMove.Field("OB", "-7"),
                        UiMove.Field("CH", "+12g"),
                    ),
                    optionalFields = listOf(
                        UiMove.Field("Damage", "19"),
                        UiMove.Field("Recovery", "r29"),
                    ),
                    notes = listOf(
                        "Strong Aerial Tailspin",
                        "Homing",
                        "Balcony Break",
                    )
                ),
                UiMove(
                    id = "df3+4",
                    input = "df3+4",
                    mandatoryFields = listOf(
                        UiMove.Field("Startup", "i22~24"),
                        UiMove.Field("OH", "+12g"),
                        UiMove.Field("OB", "-7"),
                        UiMove.Field("CH", "+12g"),
                    ),
                    optionalFields = listOf(
                        UiMove.Field("Damage", "19"),
                        UiMove.Field("Recovery", "r29"),
                    ),
                    notes = listOf(
                        "Strong Aerial Tailspin",
                        "Homing",
                        "Balcony Break",
                    )
                ),
            ),
            expandedNotes = emptySet(),
            onNotesExpandClick = {},
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun SectionPreviewLight() {
    AppTheme(darkTheme = false) {
        Section(
            title = "Heat",
            moves = listOf(
                UiMove(
                    id = "df3+4",
                    input = "df3+4",
                    mandatoryFields = listOf(
                        UiMove.Field("Startup", "i22~24"),
                        UiMove.Field("OH", "+12g"),
                        UiMove.Field("OB", "-7"),
                        UiMove.Field("CH", "+12g"),
                    ),
                    optionalFields = listOf(
                        UiMove.Field("Damage", "19"),
                        UiMove.Field("Recovery", "r29"),
                    ),
                    notes = listOf(
                        "Strong Aerial Tailspin",
                        "Homing",
                        "Balcony Break",
                    )
                ),
                UiMove(
                    id = "df3+4",
                    input = "df3+4",
                    mandatoryFields = listOf(
                        UiMove.Field("Startup", "i22~24"),
                        UiMove.Field("OH", "+12g"),
                        UiMove.Field("OB", "-7"),
                        UiMove.Field("CH", "+12g"),
                    ),
                    optionalFields = listOf(
                        UiMove.Field("Damage", "19"),
                        UiMove.Field("Recovery", "r29"),
                    ),
                    notes = listOf(
                        "Strong Aerial Tailspin",
                        "Homing",
                        "Balcony Break",
                    )
                ),
            ),
            expandedNotes = emptySet(),
            onNotesExpandClick = {},
        )
    }
}
//endregion