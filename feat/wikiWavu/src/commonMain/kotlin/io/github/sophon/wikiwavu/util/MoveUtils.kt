package io.github.sophon.wikiwavu.util

import io.github.sophon.core.wiki.domain.model.Move

//TODO: this should be a property of Move
internal fun Move.isThrow(): Boolean {
    val guard = this.guard.orEmpty().lowercase()

    return guard.contains("t")
            || guard.contains("th(")
            || notes.any { it.contains("throw break", ignoreCase = true) }
}

//TODO: this should be a property of Move
internal fun List<String>.isHitThrow(): Boolean {
    return this.any {
        val note = it.lowercase()
        note.contains("attack throw") || note.contains("hit throw")
    }
}