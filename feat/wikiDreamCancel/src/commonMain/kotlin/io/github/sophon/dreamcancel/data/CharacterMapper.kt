package io.github.sophon.dreamcancel.data

import io.github.sophon.core.util.cleanHtml
import io.github.sophon.core.util.createAliases
import io.github.sophon.core.util.removeAccents
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.dreamcancel.domain.FEATURE_URL

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
        remoteQueryId = queryName,
        wikiUrl = "$FEATURE_URL/$gameId/$queryName",
    )

    return char
}

internal fun String.createQueryName(): String {
    return this
        .cleanHtml()
        .removeAccents()
        .split(' ')
        .joinToString("_")
}

internal fun String.createThumbnailUrl(gameId: String): String? {
    val prefix: String
    val suffix: String

    when (gameId) {
        DreamCancelTables.TABLE_COTW_MOVES -> {
            prefix = "FF_COTW"
            suffix = "Icon.png"
        }
        DreamCancelTables.TABLE_KOF15_MOVES -> {
            prefix = "KOFXV"
            suffix = "Portrait.png"
        }
        else -> return null
    }

    return "${prefix}_${this}_${suffix}"
}