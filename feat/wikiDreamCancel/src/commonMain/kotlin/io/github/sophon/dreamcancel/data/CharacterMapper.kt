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
        .replace(Regex("[\\s.']+"), "_")
        .lowercase()
    val displayName = this.cleanHtml()
    val queryName = this.createQueryName()

    val char = Character(
        id = idName,
        displayName = displayName,
        aliasList = displayName.createAliases(),
        queryName = queryName,
        wikiUrl = "$FEATURE_URL/$gameId/$queryName",
    )

    return char
}

internal fun String.createAliases(): List<String> {
    val words = this
        .lowercase()
        .replace(" ", "_")
        .replace(".", "_")
        .replace("-", "_")
        .split('_')
        .filter { it.isNotEmpty() }

    return if (words.size >= 2) {
        buildList {
            words.first().takeIf { it.length >= 2 }?.let { add(it) }
            words.last().takeIf { it.length >= 2 }?.let { add(it) }

            var initials = ""
            words.forEach { word -> initials += word.first() }
            initials.takeIf { it.isNotBlank() }?.let { add(initials) }
        }.distinct()
    } else {
        emptyList()
    }
}

internal fun String.createQueryName(): String {
    return this
        .cleanHtml()
        .removeAccents()
        .split(' ')
        .joinToString("_")
}