package io.github.sophon.wiki.adapter.outbound.ktor.xko

import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.wiki.Game

internal fun XkoCharacterJsonResponseDto.toDomain(game: Game): List<Character> {
    val characterList = characters.map { dto ->
        val character = Character(
            id = CharacterId(game, dto.displayName),
            displayName = dto.displayName,
            remoteQueryId = dto.displayName,
            wikiUrl = "$WIKI_BASE_URL/${dto.displayName}",
            aliasList = dto.aliasList,
            images = Character.Images(
                portraitId = dto.images?.portrait?.substringAfterLast('/'),
                portrait = dto.images?.portrait,
                full = dto.images?.full,
            ),
        )
        character
    }
    return characterList
}
