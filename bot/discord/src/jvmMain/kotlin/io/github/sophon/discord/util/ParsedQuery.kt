package io.github.sophon.discord.util

import io.github.sophon.core.util.dropFirstAndJoin

internal data class ParsedQuery(
    val charName: String,
    val move: String,
)

internal fun String.parseQuery(): ParsedQuery? {
    if (split(" ").size < 2) return null

    val charName = substringBefore(' ')
    val move = dropFirstAndJoin(' ')

    return ParsedQuery(charName, move)
}