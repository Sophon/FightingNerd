package io.github.sophon.fightingnerd.core.ui

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.sophon.fightingnerd.theme.nerdColorPalette

@Composable
internal fun CircularLoader(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        color = nerdColorPalette.accent,
        trackColor = nerdColorPalette.surfaceHigh,
        modifier = modifier,
    )
}
