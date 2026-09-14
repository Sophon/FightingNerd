package io.github.sophon.fightingnerd.feat.share

internal interface ShareSheet {
    suspend fun shareImage(pngBytes: ByteArray, fileName: String)
}
