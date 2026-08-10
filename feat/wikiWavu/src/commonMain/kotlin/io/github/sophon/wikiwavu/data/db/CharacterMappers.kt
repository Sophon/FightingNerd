package io.github.sophon.wikiwavu.data.db

import io.github.sophon.core.wiki.model.Character
import io.github.sophon.wikiwavu.data.CharacterEntity
import io.github.sophon.wikiwavu.data.CharacterQueries

internal fun CharacterEntity.toDomain(): Character {
    return Character(
        id = id,
        displayName = displayName,
        remoteQueryId = remoteQueryId,
        wikiUrl = wikiUrl,
        aliasList = aliases.toDomain(),
        images = Character.Images(
            iconUrl = imagesIconUrl,
            bannerUrl = imagesBannerUrl,
        ),
        hp = hp,
        umo = umo.toDomain(),
    )
}

internal fun CharacterQueries.persist(character: Character, gameId: String) {
    insertCharacter(
        id = character.id,
        gameId = gameId,
        remoteQueryId = character.remoteQueryId,
        wikiUrl = character.wikiUrl,
        displayName = character.displayName,
        aliases = character.aliasList.fromDomain(),
        hp = character.hp,
        umo = character.umo.fromDomain(),
        imagesIconUrl = character.images?.iconUrl,
        imagesBannerUrl = character.images?.bannerUrl,
    )
}
