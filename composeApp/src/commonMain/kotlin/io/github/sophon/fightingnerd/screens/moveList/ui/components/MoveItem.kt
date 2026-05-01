package io.github.sophon.fightingnerd.screens.moveList.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.sophon.core.util.orDash
import io.github.sophon.fightingnerd.screens.moveList.domain.UiMove
import io.github.sophon.fightingnerd.theme.AppTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun MoveItem(
    move: UiMove,
    isNotesExpanded: Boolean,
    onNotesExpandClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp)
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
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(.3f),
                modifier = Modifier.weight(1f),
            )
            PropertyIcons(properties = move.properties)
        }
        Spacer(Modifier.height(4.dp))

        Fields(fieldList = move.mandatoryFields, isMandatory = true)
        Spacer(Modifier.height(4.dp))

        Fields(fieldList = move.optionalFields, isMandatory = false)
        Spacer(Modifier.height(8.dp))

        if (move.details.isNotEmpty()) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(.3f))
            Details(
                title = "Details",
                items = move.details,
            )
            Spacer(Modifier.height(4.dp))
        }

        if (move.notes.isNotEmpty()) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(.3f))
            Spacer(Modifier.height(4.dp))
            Details(
                title = "📝 Notes",
                isExpanded = isNotesExpanded,
                onExpandClick = onNotesExpandClick,
                items = move.notes,
            )
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun PropertyIcons(
    properties: Set<UiMove.Property>,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(
            space = 2.dp,
            alignment = Alignment.CenterHorizontally,
        ),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        properties.forEach { property ->
            Icon(
                painter = painterResource(property.resource),
                contentDescription = property.contentDescription,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = modifier
                    .size(24.dp),
            )
        }
    }
}

@Composable
private fun Fields(
    fieldList: List<UiMove.Field>,
    isMandatory: Boolean,
    modifier: Modifier = Modifier
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(48.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        fieldList.forEach { field ->
            if (isMandatory || field.value != null) {
                Column(
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        text = field.title,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                    )

                    Text(
                        text = field.value.orDash(),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    )
                }
            }
        }
    }
}


//region PREVIEW
@Composable
@Preview(showBackground = true)
private fun MoveItemPreviewDark() {
    AppTheme(darkTheme = true) {
        MoveItem(
            move = UiMove(
                id = "Raven-ws3+4",
                input = "ws3+4",
                mandatoryFields = listOf(
                    UiMove.Field("Startup", "i16~18"),
                    UiMove.Field("OH", "+15a (+5)"),
                    UiMove.Field("OB", "-12"),
                    UiMove.Field("CH", null),
                    UiMove.Field("Level", "m"),
                ),
                optionalFields = listOf(
                    UiMove.Field("Damage", "20"),
                    UiMove.Field("Recovery", "r34"),
                ),
                properties = setOf(
                    UiMove.Property.HEAT,
                    UiMove.Property.HOMING,
                ),
            ),
            isNotesExpanded = false,
            onNotesExpandClick = {},
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun MoveItemPreviewLight() {
    AppTheme(darkTheme = false) {
        MoveItem(
            move = UiMove(
                id = "King-d1+4",
                input = "d1+4",
                mandatoryFields = listOf(
                    UiMove.Field("Startup", "i12~13"),
                    UiMove.Field("OH", "0d"),
                    UiMove.Field("OB", null),
                    UiMove.Field("CH", null),
                    UiMove.Field("Level", "t(c)"),
                ),
                optionalFields = listOf(
                    UiMove.Field("Damage", "35"),
                    UiMove.Field("Recovery", "r24"),
                ),
                properties = setOf(
                    UiMove.Property.TORNADO,
                    UiMove.Property.THROW,
                )
            ),
            isNotesExpanded = false,
            onNotesExpandClick = {},
        )
    }
}
//endregion