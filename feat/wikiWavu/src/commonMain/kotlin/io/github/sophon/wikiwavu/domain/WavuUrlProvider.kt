package io.github.sophon.wikiwavu.domain

import io.github.sophon.core.domain.model.Move
import io.github.sophon.core.util.urlEncode
import io.github.sophon.wikiwavu.MOVE_URL
import io.github.sophon.wikiwavu.VIDEO_URL

class WavuUrlProvider {
    fun charUrl(charName: String): String {
        return MOVE_URL + charName.replace(" ", "_")
    }

    fun videoUrl(move: Move): String? {
        return move.videoId?.let { VIDEO_URL + it.urlEncode() }
    }

    fun followUpUrl(query: String): String? {
        if (query.startsWith("[[").not() || query.endsWith("]]").not()) return null

        val formatted = query
            .substringAfter("[[")
            .substringBefore("|")
            .replace(" ", "_")

        return MOVE_URL + formatted
    }
}