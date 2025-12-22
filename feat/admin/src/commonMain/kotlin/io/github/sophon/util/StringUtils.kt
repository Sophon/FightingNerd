package io.github.sophon.util

import io.github.sophon.domain.Source

fun String.toSourceAndMessage(): Pair<Source, String>? {
    val queryFields = this.split(" ")
    if (queryFields.size < 2) return null

    val recipientFields = queryFields.first()
    val message = queryFields.drop(1).joinToString(" ")

    val recipientParts = recipientFields.split("-").apply {
        if (size != 3) return null
    }

    val (userName, id, channelId) = recipientParts

    if (userName.isBlank() || id.isBlank() || channelId.isBlank()) {
        return null
    }

    return Pair(Source(userName, id, channelId), message)
}

fun String.toSource(): Source? {
    val recipientParts = split("-").apply {
        if (size != 3) return null
    }

    val (userName, id, channelId) = recipientParts

    if (userName.isBlank() || id.isBlank() || channelId.isBlank()) {
        return null
    }

    return Source(userName, id, channelId)
}