package io.github.sophon.wiki.adapter.outbound.ktor.wavu

import io.github.sophon.wiki.application.domain.model.Character
import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.wiki.Game

internal fun WavuCharacterListResponseDto.toDomain(game: Game): List<Character> {
    return characters.map { dto ->
        Character(
            id = CharacterId(game, dto.displayName),
            displayName = dto.displayName,
            remoteQueryId = dto.displayName,
            wikiUrl = "$URL_PREFIX_MOVE/${dto.wavuName.replace(" ", "_")}",
            aliasList = dto.aliasList,
            images = Character.Images(
                iconId = dto.images?.officialLargePng?.substringAfterLast('/'),
                iconUrl = dto.images?.officialLargePng,
            )
        )
    }
}
