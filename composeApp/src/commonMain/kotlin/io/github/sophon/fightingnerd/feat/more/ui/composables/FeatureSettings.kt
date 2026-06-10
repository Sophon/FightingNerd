package io.github.sophon.fightingnerd.feat.more.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.sophon.fightingnerd.feat.more.ui.MoreState
import io.github.sophon.fightingnerd.feat.more.ui.MoreState.UiFeatureSetting
import io.github.sophon.fightingnerd.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun FeatureSettings(
    featureList: List<UiFeatureSetting>,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(
            space = 2.dp,
            alignment = Alignment.Top,
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color = MaterialTheme.colorScheme.surfaceVariant)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(.2f),
                shape = RoundedCornerShape(16.dp),
            ),
    ) {
        itemsIndexed(featureList) { index, feature ->
            FeatureSetting(
                feature = feature,
                isFirst = index == 0,
                isLast = index == featureList.lastIndex,
            )
        }
    }
}

@Composable
private fun FeatureSetting(
    feature: UiFeatureSetting,
    isFirst: Boolean,
    isLast: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Toggle(
            title = feature.featureName,
            subtitle = feature.version,
            isEnabled = feature.isEnabled,
            onToggle = { /* TODO */ },
            isFirst = isFirst,
            isLast = isLast && feature.gameList.isEmpty(),
        )

        val gameCount = feature.gameList.size
        feature.gameList.forEachIndexed { index, game ->
            Toggle(
                title = game.displayName,
                isEnabled = game.isEnabled,
                onToggle = { /* TODO */ },
                isLast = isLast && (index == gameCount - 1),
                modifier = Modifier.padding(start = 16.dp),
            )
        }

        HorizontalDivider(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .background(MaterialTheme.colorScheme.outline)
        )
    }
}

@Composable
private fun Toggle(
    title: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    subtitle: String? = null,
    isFirst: Boolean = false,
    isLast: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(
        topStart = if (isFirst) 12.dp else 4.dp,
        topEnd = if (isFirst) 12.dp else 4.dp,
        bottomStart = if (isLast) 12.dp else 4.dp,
        bottomEnd = if (isLast) 12.dp else 4.dp,
    )

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
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
        FeatureSettings(
            featureList = MoreState.PREVIEW.featureList,
            onClick = {},
        )
    }
}

@Composable
@Preview
private fun FeatureSettingsPreviewLight() {
    AppTheme(false) {
        FeatureSettings(
            featureList = MoreState.PREVIEW.featureList,
            onClick = {},
        )
    }
}
//endregion