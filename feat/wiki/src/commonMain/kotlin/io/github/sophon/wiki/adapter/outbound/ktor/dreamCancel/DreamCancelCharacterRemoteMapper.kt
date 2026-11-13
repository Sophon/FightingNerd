package io.github.sophon.wiki.adapter.outbound.ktor.dreamCancel

import io.github.sophon.core.util.cleanHtml
import io.github.sophon.core.util.removeAccents
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.wiki.Game

internal fun DreamCancelCharacterJsonResponseDto.toDomain(game: Game): List<Character> {
    val characterList = characters.map { dto ->
        val iconUrl = dto.images?.officialLargePng

        val character = Character(
            id = CharacterId(game, dto.displayName),
            displayName = dto.displayName,
            remoteQueryId = dto.displayName,
            wikiUrl = "$WIKI_BASE_URL/${game.id}/${dto.displayName.createQueryName()}",
            aliasList = dto.aliasList,
            images = Character.Images(
                iconId = iconUrl?.substringAfterLast('/'),
                iconUrl = iconUrl,
            ),
        )
        character
    }
    return characterList
}

internal fun String.createQueryName(): String {
    val queryName = cleanHtml()
        .removeAccents()
        .split(' ')
        .joinToString("_")
    return queryName
}
