package io.github.sophon.fightingnerd.feat.move.model

import androidx.compose.runtime.Immutable

@Immutable
sealed interface MediaAvailability {
    data object NotDownloaded: MediaAvailability

    data class Downloading(
        val downloaded: Int,
        val total: Int,
    ) : MediaAvailability {
        val fraction: Float
            get() = if (total > 0) downloaded.toFloat() / total else 0f
    }

    data object Downloaded: MediaAvailability
}
