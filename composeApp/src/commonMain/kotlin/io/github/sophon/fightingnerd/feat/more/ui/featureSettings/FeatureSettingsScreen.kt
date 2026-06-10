package io.github.sophon.fightingnerd.feat.more.ui.featureSettings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.sophon.fightingnerd.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun FeatureSettingsScreen(
    modifier: Modifier = Modifier,
) {
    val vm = koinViewModel<FeatureSettingsVM>()
    val state by vm.state.collectAsStateWithLifecycle()

    Content(
        state = state,
        modifier = modifier,
    )
}

@Composable
private fun Content(
    state: FeatureSettingsState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(
            space = 2.dp,
            alignment = Alignment.Top,
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        item { Spacer(Modifier.height(16.dp)) }

        items(state.featureList) { feature ->
            val shape = RoundedCornerShape(16.dp)
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clip(shape)
                    .background(color = MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(.2f),
                        shape = shape,
                    ),
            ) {
                Spacer(Modifier.height(4.dp))
                Toggle(
                    title = feature.featureName,
                    subtitle = feature.version,
                    isEnabled = feature.isEnabled,
                    onToggle = { /* TODO */ },
                )

                feature.gameList.forEach { game ->
                    Toggle(
                        title = game.displayName,
                        isEnabled = game.isEnabled,
                        onToggle = { /* TODO */ },
                        modifier = Modifier.padding(start = 16.dp),
                    )
                }
            }
        }

        item { Spacer(Modifier.height(32.dp)) }
    }
}

@Composable
private fun Toggle(
    title: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle,
        )
    }
}


//region PREVIEW
@Composable
@Preview(showBackground = true)
private fun FeatureSettingsPreviewDark() {
    AppTheme(true) {
        Content(
            state = FeatureSettingsState.PREVIEW,
        )
    }
}

@Composable
@Preview
private fun FeatureSettingsPreviewLight() {
    AppTheme(false) {
        Content(
            state = FeatureSettingsState.PREVIEW,
        )
    }
}
//endregion