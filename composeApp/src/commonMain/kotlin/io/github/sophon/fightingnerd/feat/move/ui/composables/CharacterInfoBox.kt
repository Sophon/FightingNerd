package io.github.sophon.fightingnerd.feat.move.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.sophon.fightingnerd.feat.move.ui.MoveListState
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions
import io.github.sophon.fightingnerd.theme.nerdTypography
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun CharacterInfoBox(
    character: MoveListState.UiCharacter,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(nerdDimensions.cornerDefault)
    Column(
        verticalArrangement = Arrangement.spacedBy(nerdDimensions.componentGap),
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(nerdColorPalette.background)
            .border(
                width = nerdDimensions.strokeThin,
                color = nerdColorPalette.dividerSubtle,
                shape = shape,
            )
            .heightIn(max = PANEL_MAX_HEIGHT)
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = nerdDimensions.componentPadding,
                vertical = nerdDimensions.componentPadding,
            ),
    ) {
        InfoGrid(fields = character.propertyFields)
    }
}

@Composable
private fun InfoCell(
    field: MoveListState.Field,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(field.label),
            style = nerdTypography.labelMedium,
            color = nerdColorPalette.textSecondary,
        )
        Text(
            text = field.value?.takeIf { it.isNotBlank() } ?: "-",
            style = nerdTypography.bodyMedium,
            color = nerdColorPalette.textPrimary,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun InfoGrid(
    fields: List<MoveListState.Field>,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        maxItemsInEachRow = 2,
        horizontalArrangement = Arrangement.spacedBy(nerdDimensions.componentGap),
        verticalArrangement = Arrangement.spacedBy(nerdDimensions.componentGap),
    ) {
        fields.forEach { field ->
            InfoCell(
                field = field,
                modifier = Modifier.weight(1f),
            )
        }
    }
}


private val PANEL_MAX_HEIGHT = 400.dp
