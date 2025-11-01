package io.github.sophon.cornerman.screens.moveList.util

import io.github.sophon.wikiwavu.domain.model.Move

internal fun Move.cleanComboLinks(): Move {
    return copy(
        onHit = onHit?.cleanWikiLink(),
        onCH = onCH?.cleanWikiLink(),
    )
}

private fun String.cleanWikiLink(): String {
    // Handle wiki links like [[Page#Section|+22a]]
    return if (startsWith("[[") && endsWith("]]")) {
        substringAfter("|", this).substringBefore("]]")
    } else {
        this
    }
}