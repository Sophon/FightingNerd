package io.github.sophon.fightingnerd.infrastructure

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image

internal actual fun ImageBitmap.toPngBytes(): ByteArray {
    val skiaBitmap = asSkiaBitmap()
    val image = Image.makeFromBitmap(skiaBitmap)
    val data = requireNotNull(image.encodeToData(EncodedImageFormat.PNG)) {
        "PNG encoding failed"
    }
    val bytes = data.bytes
    return bytes
}
