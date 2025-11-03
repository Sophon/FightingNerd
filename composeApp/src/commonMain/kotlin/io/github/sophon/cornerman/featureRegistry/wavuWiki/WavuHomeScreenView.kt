package io.github.sophon.cornerman.featureRegistry.wavuWiki

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.sophon.cornerman.featureRegistry.FeatureInfo
import io.github.sophon.cornerman.theme.AppTheme
import io.github.sophon.cornerman.uiGallery.CharacterOverview
import io.github.sophon.cornerman.uiGallery.FeatureInfo
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WavuHomeScreenView(
    featureInfo: FeatureInfo,
    onCharacterClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val vm = koinViewModel<WavuHomeScreenVM>()
    val state by vm.state.collectAsStateWithLifecycle()

    Content(
        state = state,
        featureInfo = featureInfo,
        onCharacterClick = onCharacterClick,
        modifier = modifier,
    )
}

@Composable
private fun Content(
    state: WavuHomeScreenViewState,
    onCharacterClick: (String) -> Unit,
    featureInfo: FeatureInfo? = null,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(.2f),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        if (featureInfo != null) {
            FeatureInfo(featureInfo)
        }
        Spacer(Modifier.height(8.dp))

        CharacterOverview(
            characterList = state.characterList,
            onCharacterClick = onCharacterClick,
        )
    }
}


//region PREVIEW
@Composable
@Preview(showBackground = true)
private fun WavuCharacterOverviewPreviewDark() {
    AppTheme(darkTheme = true) {
        Content(
            state = WavuHomeScreenViewState.PREVIEW,
            onCharacterClick = {},
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun WavuCharacterOverviewPreviewLight() {
    AppTheme(darkTheme = false) {
        Content(
            state = WavuHomeScreenViewState.PREVIEW,
            onCharacterClick = {},
        )
    }
}
//endregion