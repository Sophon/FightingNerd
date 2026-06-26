package io.github.sophon.fightingnerd.core.ui.components

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.sophon.fightingnerd.theme.nerdColorPalette

@Composable
internal fun CircularLoader(
    modifier: Modifier = Modifier,
    color: Color = nerdColorPalette.accent,
    trackColor: Color = nerdColorPalette.surfaceHigh,
) {
    CircularProgressIndicator(
        color = color,
        trackColor = trackColor,
        modifier = modifier,
    )
}
