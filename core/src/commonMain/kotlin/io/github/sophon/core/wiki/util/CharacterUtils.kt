package io.github.sophon.core.wiki.util

import io.github.sophon.core.wiki.model.Character

fun List<Character>.findMatching(query: String): Character? {
    val normalizedQuery = query.normalizeForMatch()

    firstOrNull { it.id == normalizedQuery }?.let { return it }
    firstOrNull {
        it.displayName.normalizeForMatch() == normalizedQuery
    }?.let { return it }
    firstOrNull { character ->
        character.aliasList.any { it.normalizeForMatch() == normalizedQuery }
    }?.let { return it }

    return null
}
