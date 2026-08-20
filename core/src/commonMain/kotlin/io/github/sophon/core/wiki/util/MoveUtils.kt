package io.github.sophon.core.wiki.util

import io.github.sophon.core.wiki.model.Move

fun Move.getLevel(): String? {
    return ggstProperties?.level
        ?: dbfzProperties?.level
}

fun List<Move>.findMatching(query: String): Move? {
    val normalizedQuery = query.normalizeForMatch()

    firstOrNull { it.id == normalizedQuery }
        ?.let { return it }
    firstOrNull { it.input.normalizeForMatch() == normalizedQuery }
        ?.let { return it }
    firstOrNull { move ->
        move.aliases.any { it.normalizeForMatch() == normalizedQuery }
    }?.let { return it }

    firstOrNull { move ->
        move.name?.normalizeForMatch() == normalizedQuery
    }?.let { return it }

    return null
}

fun List<Move>.filterMatching(query: String?): List<Move> {
    val filtered = this.filter { move ->
        query?.let { query ->
            move.id.equals(query, ignoreCase = true) ||
                    move.input.startsWith(query, ignoreCase = true) ||
                    move.name?.contains(query, ignoreCase = true) == true ||
                    move.aliases.any { alias -> alias.contains(query, ignoreCase = true) }
        } ?: true
    }
    return filtered
}
