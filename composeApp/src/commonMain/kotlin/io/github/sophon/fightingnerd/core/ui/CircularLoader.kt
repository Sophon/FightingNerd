package io.github.sophon.fightingnerd.core.ui

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.sophon.fightingnerd.theme.nerdColorPalette

@Composable
internal fun CircularLoader(
    color: Color = nerdColorPalette.accent,
    trackColor: Color = nerdColorPalette.surfaceHigh,
    modifier: Modifier = Modifier,
) {
    CircularProgressIndicator(
        color = color,
        trackColor = trackColor,
        modifier = modifier,
    )
}
