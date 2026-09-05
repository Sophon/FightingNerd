package io.github.sophon.fightingnerd.core.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions

@Composable
internal fun TopBarButton(
    onClick: () -> Unit,
    imageVector: ImageVector = Icons.Outlined.Close,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(nerdDimensions.iconLarge)
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = nerdColorPalette.textPrimary,
        )
    }
}

internal data class IconAction(
    val icon: ImageVector,
    val onClick: () -> Unit,
    val isEnabled: Boolean = true,
)

@Composable
internal fun IconActionButton(
    action: IconAction,
    modifier: Modifier = Modifier,
) {
    val tint = if (action.isEnabled) {
        nerdColorPalette.textPrimary
    } else {
        nerdColorPalette.textSecondary
    }
    IconButton(
        onClick = action.onClick,
        enabled = action.isEnabled,
        modifier = modifier,
    ) {
        Icon(
            imageVector = action.icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
internal fun CircularProgressButton(
    progress: Float,
    onClick: () -> Unit,
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    color: Color = nerdColorPalette.accent,
    trackColor: Color = nerdColorPalette.surfaceHigh,
    isEnabled: Boolean = true,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 500),
        label = "CircularProgressButton",
    )
    Box(
        modifier = modifier.size(nerdDimensions.iconLarge),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            progress = { animatedProgress },
            color = color,
            trackColor = trackColor,
            strokeCap = StrokeCap.Butt,
            gapSize = 0.dp,
            modifier = Modifier.matchParentSize(),
        )
        IconButton(
            onClick = onClick,
            enabled = isEnabled,
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                tint = if (isEnabled) nerdColorPalette.textPrimary else nerdColorPalette.textDisabled,
            )
        }
    }
}
