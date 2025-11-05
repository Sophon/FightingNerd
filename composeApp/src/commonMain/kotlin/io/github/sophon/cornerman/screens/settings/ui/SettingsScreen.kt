package io.github.sophon.cornerman.screens.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.sophon.cornerman.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
internal fun SettingsScreen(
    modifier: Modifier = Modifier,
) {
    val vm = koinInject<SettingsVM>()
    val state by vm.state.collectAsStateWithLifecycle()

    Content(
        state = state,
        onFeatureClick = vm::toggleFeature,
        modifier = modifier,
    )
}

@Composable
private fun Content(
    state: SettingsViewState,
    onFeatureClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        FeatureSettings(
            featureSettingList = state.featureList,
            onClick = onFeatureClick,
        )
    }
}


//region PREVIEW
@Composable
@Preview(showBackground = true)
private fun SettingsPreviewDark() {
    AppTheme(darkTheme = true) {
        Content(
            state = SettingsViewState.PREVIEW,
            onFeatureClick = {},
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun SettingsPreviewLight() {
    AppTheme(darkTheme = false) {
        Content(
            state = SettingsViewState.PREVIEW,
            onFeatureClick = {},
        )
    }
}
//endregion