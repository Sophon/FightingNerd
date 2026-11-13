package io.github.sophon.wiki.adapter.outbound.ktor.xko

import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.wiki.Game

/**
 * Xko has no character table - the character is built from the move's page name.
 */
@ExcludeFromCoverage("only from string")
internal fun String.toCharacter(game: Game): Character {
    val character = Character(
        id = CharacterId(game, this),
        displayName = this,
        remoteQueryId = this,
        wikiUrl = "$WIKI_BASE_URL/$this",
    )
    return character
}

internal fun XkoCharacterJsonResponseDto.toDomain(game: Game): List<Character> {
    val characterList = characters.map { dto ->
        val iconUrl = dto.images?.officialLargePng

        val character = Character(
            id = CharacterId(game, dto.displayName),
            displayName = dto.displayName,
            remoteQueryId = dto.displayName,
            wikiUrl = "$WIKI_BASE_URL/${dto.displayName}",
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
