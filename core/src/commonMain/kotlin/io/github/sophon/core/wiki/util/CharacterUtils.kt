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

//rename these to filter
fun List<Character>.findApprox(query: String): Character? {
    val normalizedQuery = query.normalizeForMatch()

    firstOrNull { it.id == normalizedQuery }?.let { return it }
    firstOrNull {
        it.displayName.normalizeForMatch().contains(normalizedQuery)
    }?.let { return it }
    firstOrNull { character ->
        character.aliasList.any { it.normalizeForMatch().contains(normalizedQuery) }
    }?.let { return it }

    return null
}

fun Character.isMatching(query: String): Boolean {
    val normalizedQuery = query.normalizeForMatch()

    if (id == normalizedQuery) return true
    if (displayName.normalizeForMatch() == normalizedQuery) return true

    val matchesAlias = aliasList.any { it.normalizeForMatch() == normalizedQuery }
    return matchesAlias
}

fun Character.isApprox(query: String): Boolean {
    val normalizedQuery = query.normalizeForMatch()

    if (id == normalizedQuery) return true
    if (displayName.normalizeForMatch().contains(normalizedQuery)) return true

    val matchesAlias = aliasList.any { it.normalizeForMatch().contains(normalizedQuery) }
    return matchesAlias
}
