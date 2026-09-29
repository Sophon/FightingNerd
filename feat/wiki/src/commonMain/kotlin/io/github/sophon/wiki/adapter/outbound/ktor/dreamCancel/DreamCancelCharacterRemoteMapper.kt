package io.github.sophon.wiki.adapter.outbound.ktor.dreamCancel

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.util.cleanHtml
import io.github.sophon.core.util.createAliases
import io.github.sophon.core.util.removeAccents
import io.github.sophon.wiki.application.domain.model.Character
import io.github.sophon.wiki.application.domain.model.CharacterId

/**
 * DreamCancel has no character table - the character is built from the move table's `chara` column.
 */
internal fun String.toCharacter(
    game: Game,
    iconUrlMap: Map<String, String>,
): Character {
    val displayName = cleanHtml()
    val queryName = createQueryName()
    val iconKeys = listOf(substringBefore(" "), substringAfterLast(" "))

    val character = Character(
        id = CharacterId(queryName),
        displayName = displayName,
        remoteQueryId = queryName,
        aliasList = displayName.createAliases(),
        wikiUrl = "$WIKI_BASE_URL/${game.id}/$queryName",
        images = Character.Images(
            iconId = iconKeys.firstOrNull { iconUrlMap.contains(it) },
            iconUrl = (iconKeys.firstNotNullOfOrNull { iconUrlMap[it] } ?: game.iconUrl),
        ),
    )
    return character
}

internal fun String.createQueryName(): String {
    val queryName = cleanHtml()
        .removeAccents()
        .split(' ')
        .joinToString("_")
    return queryName
}

/**
 * `chara` spellings that differ only in accents, case, apostrophes or separators form the same key.
 */
internal fun String.formGroupingKey(): String {
    val groupingKey = cleanHtml()
        .removeAccents()
        .replace("'", "")
        .replace(groupingSeparatorRegex, "_")
        .lowercase()
    return groupingKey
}


private val groupingSeparatorRegex = Regex("[\\s._']+")
