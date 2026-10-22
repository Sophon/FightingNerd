package io.github.sophon.fightingnerd.adapter.inbound.share

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import java.io.ByteArrayOutputStream

internal actual fun ImageBitmap.toPngBytes(): ByteArray {
    val out = ByteArrayOutputStream()
    asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, PNG_QUALITY, out)
    val bytes = out.toByteArray()
    return bytes
}


private const val PNG_QUALITY = 100
