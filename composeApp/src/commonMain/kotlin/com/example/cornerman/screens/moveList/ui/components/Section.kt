package com.example.cornerman.screens.moveList.ui.components

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
import com.example.core.util.orDash
import com.example.cornerman.theme.AppTheme
import com.example.wikiwavu.domain.model.Move
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

@Composable
private fun MoveItem(
    move: Move,
    isNotesExpanded: Boolean,
    onNotesExpandClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Text(
                text = move.input,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            )
            Spacer(Modifier.width(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(.3f))
        }
        Spacer(Modifier.height(4.dp))

        MainInformation(move)
        Spacer(Modifier.height(4.dp))

        SecondaryInformation(move)
        Spacer(Modifier.height(4.dp))

        if (move.notes.isNotEmpty()) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(.3f))
            Spacer(Modifier.height(4.dp))
            Notes(
                isExpanded = isNotesExpanded,
                onExpandClick = onNotesExpandClick,
                notes = move.notes,
            )
        }
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun MainInformation(
    move: Move,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Field(
            title = "Startup",
            text = move.startup.orDash(),
        )
        Field(
            title = "OH",
            text = move.onHit.orDash(),
        )
        Field(
            title = "OB",
            text = move.onBlock.orDash(),
        )
        Field(
            title = "CH",
            text = move.onCH.orDash(),
        )
    }
}

@Composable
private fun SecondaryInformation(
    move: Move,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Field(
            title = "Level",
            text = move.level.orDash(),
        )
        move.damage?.let { damage ->
            Field(
                title = "Damage",
                text = damage,
            )
        }
        move.recoveryOnWhiff?.let { recovery ->
            Field(
                title = "Recovery",
                text = recovery,
            )
        }
    }
}

@Composable
private fun Field(
    title: String,
    text: String?,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
        )

        if (text != null) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            )
        }
    }
}

@Composable
private fun Notes(
    isExpanded: Boolean,
    notes: List<String>,
    onExpandClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onExpandClick)
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = "NOTES",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            )

            Icon(
                imageVector = if (isExpanded) {
                    Icons.TwoTone.ExpandLess
                } else {
                    Icons.TwoTone.ExpandMore
                },
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(Modifier.height(2.dp))

        if (isExpanded) {
            notes.forEach { note ->
                Text(
                    text = "• $note",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(Modifier.height(1.dp))
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