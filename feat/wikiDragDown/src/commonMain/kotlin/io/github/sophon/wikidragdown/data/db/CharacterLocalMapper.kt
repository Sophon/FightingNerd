package io.github.sophon.wikidragdown.data.db

import io.github.sophon.core.wiki.data.toDomain
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.wikidragdown.data.CharacterEntity

internal fun CharacterEntity.toDomain(): Character {
    val character = Character(
        id = id,
        displayName = displayName,
        remoteQueryId = remoteQueryId,
        wikiUrl = wikiUrl,
        aliasList = aliases.toDomain(),
        images = Character.Images(
            iconId = imagesIconId,
            iconUrl = imagesIconUrl,
            bannerUrl = imagesBannerUrl,
        ),
        hp = hp,
        umo = umo.toDomain(),
    )
    return character
}
