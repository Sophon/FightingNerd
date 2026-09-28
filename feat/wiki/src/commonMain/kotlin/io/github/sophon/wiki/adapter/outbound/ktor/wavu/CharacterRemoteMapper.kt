package io.github.sophon.wiki.adapter.outbound.ktor.wavu

import io.github.sophon.core.wiki.model.Character

internal fun CharacterListResponseDto.toDomain(): List<Character> {
    return characters.map { dto ->
        Character(
            id = dto.id,
            displayName = dto.displayName,
            remoteQueryId = dto.displayName,
            wikiUrl = MOVE_URL + dto.wavuName.replace(" ", "_"),
            aliasList = dto.aliasList,
            images = Character.Images(
                iconId = dto.images?.officialLargePng?.substringAfterLast('/'),
                iconUrl = dto.images?.officialLargePng,
            )
        )
    }
}
