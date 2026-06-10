package io.github.sophon.fightingnerd.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
internal fun RoundedItem(
    isFirst: Boolean,
    isLast: Boolean,
    modifier: Modifier = Modifier,
    outerRadius: Dp = 12.dp,
    innerRadius: Dp = 0.dp,
    background: Color = MaterialTheme.colorScheme.surfaceContainer,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(
        topStart = if (isFirst) outerRadius else innerRadius,
        topEnd = if (isFirst) outerRadius else innerRadius,
        bottomStart = if (isLast) outerRadius else innerRadius,
        bottomEnd = if (isLast) outerRadius else innerRadius,
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(background),
    ) {
        content()
    }
}