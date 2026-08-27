package io.github.sophon.fightingnerd.core.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions

@Composable
internal fun ProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = nerdColorPalette.accent,
    trackColor: Color = nerdColorPalette.surfaceHigh,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 500),
        label = "ProgressBar",
    )
    LinearProgressIndicator(
        progress = { animatedProgress },
        color = color,
        trackColor = trackColor,
        strokeCap = StrokeCap.Butt,
        gapSize = 0.dp,
        drawStopIndicator = {},
        modifier = modifier
            .width(128.dp)
            .padding(bottom = nerdDimensions.componentPadding)
            .height(8.dp)
            .clip(CircleShape),
    )
}
