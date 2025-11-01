package com.example.wikiwavu.domain

import io.github.sophon.core.util.urlEncode
import com.example.wikiwavu.MOVE_URL
import com.example.wikiwavu.VIDEO_URL
import com.example.wikiwavu.domain.model.Move

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