package io.github.sophon.fightingnerd.feat.move.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import io.github.sophon.fightingnerd.core.ui.components.CircularLoader
import io.github.sophon.fightingnerd.feat.move.model.Property
import io.github.sophon.fightingnerd.feat.move.ui.MoveListState.Field
import io.github.sophon.fightingnerd.feat.move.ui.icon
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions
import io.github.sophon.fightingnerd.theme.nerdTypography
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun FieldColumn(
    field: Field,
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
internal fun Properties(
    propertySet: ImmutableSet<Property>,
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
internal fun MoveImage(
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
internal fun NotesSection(
    noteList: ImmutableList<String>,
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
