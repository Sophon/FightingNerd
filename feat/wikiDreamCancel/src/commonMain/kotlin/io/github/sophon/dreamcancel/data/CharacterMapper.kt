package io.github.sophon.dreamcancel.data

import io.github.sophon.core.util.cleanHtml
import io.github.sophon.core.util.removeAccents
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.dreamcancel.FEATURE_URL

internal fun String.toDomain(
    gameId: String,
): Character {
    val idName = this
        .cleanHtml()
        .removeAccents()
        .replace("'", "")
        .split(' ')
        .joinToString("_") { it.lowercase() }
    val displayName = this
        .cleanHtml()
    val queryName = this
        .cleanHtml()
        .removeAccents()
        .split(' ')
        .joinToString("_")

    val char = Character(
        id = idName,
        displayName = displayName,
        aliasList = displayName.createAliases(),
        queryName = queryName,
        wikiUrl = "$FEATURE_URL/$gameId/$queryName",
    )

    return char
}

private fun String.createAliases(): List<String> {
    val words = split(' ', '.')

    return if (words.size >= 2) {
        buildList {
            if (words.size > 2) {
                add(words.first().lowercase())
                add(words.last().lowercase())
            }

            var initials = ""
            words.forEach { word ->
                word.takeIf { it.length >= 2 }?.let { add(it.lowercase()) }
                initials += word.first().lowercase()
            }
            initials.takeIf { it.isNotBlank() }?.let { add(initials) }
        }.distinct()
    } else {
        emptyList()
    }
}