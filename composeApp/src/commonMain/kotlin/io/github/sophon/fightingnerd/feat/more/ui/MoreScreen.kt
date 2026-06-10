package io.github.sophon.fightingnerd.feat.more.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.sophon.fightingnerd.feat.more.ui.composables.FeatureSettings
import io.github.sophon.fightingnerd.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
internal fun MoreScreen(
    modifier: Modifier = Modifier,
) {
    val vm = koinInject<MoreVM>()
    val state by vm.state.collectAsStateWithLifecycle()

    Content(
        state = state,
        onFeatureClick = vm::toggleFeature,
        modifier = modifier,
    )
}

@Composable
private fun Content(
    state: MoreState,
    onFeatureClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    FeatureSettings(
        featureList = state.featureList,
        onClick = {/*TODO*/},
    )
}


//region PREVIEW
@Composable
@Preview(showBackground = true)
private fun SettingsPreviewDark() {
    AppTheme(darkTheme = true) {
        Content(
            state = MoreState.PREVIEW,
            onFeatureClick = {},
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun SettingsPreviewLight() {
    AppTheme(darkTheme = false) {
        Content(
            state = MoreState.PREVIEW,
            onFeatureClick = {},
        )
    }
}
//endregion
