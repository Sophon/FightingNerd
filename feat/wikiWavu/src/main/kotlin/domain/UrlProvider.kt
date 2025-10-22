package domain

import MOVE_URL
import VIDEO_URL
import domain.model.Move

class WavuUrlProvider {
    fun charUrl(charName: String): String {
        return MOVE_URL + charName.replace(" ", "_")
    }

    fun videoUrl(move: Move): String? {
        return move.videoId?.let { VIDEO_URL + it }
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