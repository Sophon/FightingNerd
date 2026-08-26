package io.github.sophon.dreamcancel.data.remote

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.util.cleanHtml
import io.github.sophon.core.util.createAliases
import io.github.sophon.core.util.removeAccents
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.dreamcancel.domain.FEATURE_URL

internal fun String.toDomain(
    gameId: String,
    imageUrlMap: Map<String, String>,
): Character {
    val idName = this
        .cleanHtml()
        .removeAccents()
        .replace("'", "")
        .replace(Regex("[\\s._']+"), "_")
        .lowercase()
    val displayName = this.cleanHtml()
    val queryName = this.createQueryName()
    val iconKeys = listOf(this.substringBefore(" "), this.substringAfterLast(" "))
    val iconId = iconKeys.firstOrNull { imageUrlMap.contains(it) }
    val iconUrl = iconKeys.firstNotNullOfOrNull { imageUrlMap[it] } ?: Game.fromId(gameId)?.iconUrl

    val char = Character(
        id = idName,
        displayName = displayName,
        aliasList = displayName.createAliases(),
        remoteQueryId = queryName,
        wikiUrl = "$FEATURE_URL/$gameId/$queryName",
        images = Character.Images(
            iconId = iconId,
            iconUrl = iconUrl,
        )
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
