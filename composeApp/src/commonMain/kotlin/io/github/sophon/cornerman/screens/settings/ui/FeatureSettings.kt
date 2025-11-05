package io.github.sophon.cornerman.screens.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import io.github.sophon.cornerman.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun FeatureSettings(
    featureSettingList: List<SettingsViewState.FeatureSetting>,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
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
                shape = RoundedCornerShape(16.dp)
            ),
    ) {
        itemsIndexed(featureSettingList) { index, featureSetting ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (index > 0) {
                    HorizontalDivider(
                        Modifier
                            .height(1.dp)
                            .padding(horizontal = 8.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(.1f))
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(vertical = 4.dp, horizontal = 8.dp)
                ) {
                    Text(
                        text = featureSetting.featureInfo.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Switch(
                        checked = featureSetting.isEnabled,
                        onCheckedChange = { onClick(index) }
                    )
                }
            }
        }
    }
}


//region PREVIEW
@Composable
@Preview(showBackground = true)
private fun FeatureSettingsPreviewDark() {
    AppTheme(true) {
        FeatureSettings(
            featureSettingList = SettingsViewState.PREVIEW.featureList,
            onClick = {},
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun FeatureSettingsPreviewLight() {
    AppTheme(false) {
        FeatureSettings(
            featureSettingList = SettingsViewState.PREVIEW.featureList,
            onClick = {},
        )
    }
}
//endregion