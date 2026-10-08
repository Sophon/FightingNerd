package io.github.sophon.fightingnerd.adapter.inbound.share

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Renders [content] off-screen and hands the resulting PNG bytes to [onCaptured]. */
@Composable
internal fun ShareCaptureHost(
    content: @Composable () -> Unit,
    onCaptured: suspend (ByteArray) -> Unit,
    modifier: Modifier = Modifier,
) {
    val graphicsLayer = rememberGraphicsLayer()

    Box(
        modifier = modifier
            .alpha(0f)
            .width(ShareCardWidth)
            .wrapContentHeight(unbounded = true)
            .drawWithContent {
                graphicsLayer.record {
                    this@drawWithContent.drawContent()
                }
                drawLayer(graphicsLayer)
            }
    ) {
        content()
    }

    LaunchedEffect(Unit) {
        withFrameNanos { }
        val pngBytes = graphicsLayer.toImageBitmap().toPngBytes()
        onCaptured(pngBytes)
    }
}


private val ShareCardWidth: Dp = 320.dp
