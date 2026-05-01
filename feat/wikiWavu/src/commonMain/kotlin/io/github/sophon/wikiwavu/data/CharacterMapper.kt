package io.github.sophon.wikiwavu.data

import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.wikiwavu.domain.MOVE_URL

internal fun CharacterListResponseDto.toDomain(): List<Character> {
    return characters.map { dto ->
        Character(
            id = dto.id,
            displayName = dto.displayName,
            queryName = dto.displayName,
            wikiUrl = MOVE_URL + dto.wavuName.replace(" ", "_"),
            aliasList = dto.aliasList,
            images = Character.Images(
                iconUrl = dto.images?.officialLargePng,
            )
        )
    }
}