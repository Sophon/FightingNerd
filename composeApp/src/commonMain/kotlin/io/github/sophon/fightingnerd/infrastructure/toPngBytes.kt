package io.github.sophon.fightingnerd.infrastructure

import androidx.compose.ui.graphics.ImageBitmap

internal expect fun ImageBitmap.toPngBytes(): ByteArray
