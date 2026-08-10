package io.github.sophon.wikiwavu.data.db

import io.github.sophon.core.wiki.model.Character
import io.github.sophon.wikiwavu.data.CharacterEntity

internal fun CharacterEntity.toDomain(): Character {
    val character = Character(
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
    return character
}
