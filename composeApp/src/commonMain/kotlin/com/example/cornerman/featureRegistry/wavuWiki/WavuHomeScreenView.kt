package com.example.cornerman.featureRegistry.wavuWiki

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.cornerman.featureRegistry.FeatureInfo
import com.example.cornerman.theme.AppTheme
import com.example.cornerman.uiGallery.CharacterOverview
import cornerman.composeapp.generated.resources.Res
import cornerman.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource
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
    ) {
        if (featureInfo != null) {
            AsyncImage(
                model = featureInfo.iconUrl,
                contentDescription = featureInfo.name,
                placeholder = painterResource(Res.drawable.compose_multiplatform),
                error = painterResource(Res.drawable.compose_multiplatform),
                modifier = Modifier
                    .size(64.dp)
            )
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