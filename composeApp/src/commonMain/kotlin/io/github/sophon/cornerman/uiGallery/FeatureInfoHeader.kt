package io.github.sophon.cornerman.uiGallery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandCircleDown
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import cornerman.composeapp.generated.resources.Res
import cornerman.composeapp.generated.resources.compose_multiplatform
import io.github.sophon.cornerman.featureRegistry.FeatureInfo
import io.github.sophon.cornerman.theme.AppTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun FeatureInfoHeader(
    featureInfo: FeatureInfo,
    isExpanded: Boolean,
    onExpandClick: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(horizontal = 8.dp)
            .clickable(onClick = onExpandClick, enabled = isLoading.not())
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.Start,
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = featureInfo.iconUrl,
                contentDescription = featureInfo.name,
                placeholder = painterResource(Res.drawable.compose_multiplatform),
                error = painterResource(Res.drawable.compose_multiplatform),
                modifier = Modifier
                    .size(64.dp)
                    .padding(8.dp)
            )

            Text(
                text = featureInfo.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(32.dp))
        } else {
            val icon = if (isExpanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .size(32.dp)
            )
        }
    }
}


//region PREVIEW
@Composable
@Preview(showBackground = true)
private fun FeatureInfoPreviewDark() {
    AppTheme(darkTheme = true) {
        FeatureInfoHeader(
            featureInfo = FeatureInfo(
                name = "Wavu Wiki",
                url = "https://wavu.wiki/t/Main_Page",
                iconUrl = "https://i.imgur.com/0cnTzNk.png",
            ),
            isExpanded = true,
            onExpandClick = {},
            isLoading = false,
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun FeatureInfoLoadingPreview() {
    AppTheme(darkTheme = true) {
        FeatureInfoHeader(
            featureInfo = FeatureInfo(
                name = "Wavu Wiki",
                url = "https://wavu.wiki/t/Main_Page",
                iconUrl = "https://i.imgur.com/0cnTzNk.png",
            ),
            isExpanded = true,
            onExpandClick = {},
            isLoading = true,
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun FeatureInfoPreviewLight() {
    AppTheme(darkTheme = false) {
        FeatureInfoHeader(
            featureInfo = FeatureInfo(
                name = "WavuWiki",
                url = "https://wavu.wiki/t/Main_Page",
                iconUrl = "https://i.imgur.com/0cnTzNk.png",
            ),
            isExpanded = false,
            onExpandClick = {},
            isLoading = false,
        )
    }
}
//endregion