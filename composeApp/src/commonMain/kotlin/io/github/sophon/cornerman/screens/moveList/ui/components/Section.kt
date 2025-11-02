package io.github.sophon.cornerman.screens.moveList.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ExpandLess
import androidx.compose.material.icons.twotone.ExpandMore
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.sophon.core.util.orDash
import io.github.sophon.cornerman.theme.AppTheme
import io.github.sophon.wikiwavu.domain.model.Move
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun Section(
    title: String,
    moves: List<Move>,
    expandedNotes: Set<String>,
    onNotesExpandClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(Modifier.height(8.dp))

        moves.forEach { move ->
            MoveItem(
                move = move,
                onNotesExpandClick = { onNotesExpandClick(move.id) },
                isNotesExpanded = move.id in expandedNotes,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Spacer(Modifier.height(4.dp))
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
                Move(
                    charName = "Dragunov",
                    id = "df3+4",
                    input = "df3+4",
                    startup = "i22~24",
                    onHit = "+12g",
                    onBlock = "-7",
                    onCH = "+12g",
                    level = "m",
                    damage = "19",
                    recoveryOnWhiff = "r29",
                    notes = listOf(
                        "Strong Aerial Tailspin",
                        "Homing",
                        "Balcony Break",
                    )
                )
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
                Move(
                    charName = "Dragunov",
                    id = "df3+4",
                    input = "df3+4",
                    startup = "i22~24",
                    onHit = "+12g",
                    onBlock = "-7",
                    onCH = "+12g",
                    level = "m",
                    damage = "19",
                    recoveryOnWhiff = "r29",
                    notes = listOf(
                        "Strong Aerial Tailspin",
                        "Homing",
                        "Balcony Break",
                    )
                )
            ),
            expandedNotes = emptySet(),
            onNotesExpandClick = {},
        )
    }
}
//endregion