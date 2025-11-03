package io.github.sophon.cornerman.uiGallery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun FeatureInfo(
    featureInfo: FeatureInfo,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(
            space = 8.dp,
            alignment = Alignment.Start,
        ),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface)
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
}


//region PREVIEW
@Composable
@Preview(showBackground = true)
private fun FeatureInfoPreviewDark() {
    AppTheme(darkTheme = true) {
        FeatureInfo(
            featureInfo = FeatureInfo(
                name = "Wavu Wiki",
                url = "https://wavu.wiki/t/Main_Page",
                iconUrl = "https://i.imgur.com/0cnTzNk.png",
            )
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun FeatureInfoPreviewLight() {
    AppTheme(darkTheme = false) {
        FeatureInfo(
            featureInfo = FeatureInfo(
                name = "WavuWiki",
                url = "https://wavu.wiki/t/Main_Page",
                iconUrl = "https://i.imgur.com/0cnTzNk.png",
            )
        )
    }
}
//endregion