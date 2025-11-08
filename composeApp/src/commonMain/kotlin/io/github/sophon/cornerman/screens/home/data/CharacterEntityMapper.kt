package io.github.sophon.cornerman.screens.home.data

import io.github.sophon.core.wiki.domain.model.Character

internal fun Character.toEntity(): CharacterEntity {
    return CharacterEntity(
        id = id,
        displayName = displayName,
        wikiUrl = wikiUrl,
        aliases = aliasList.joinToString(";"),
        imageIconUrl = images?.iconUrl,
        imageBannerUrl = images?.bannerUrl,
    )
}

internal fun CharacterEntity.toDomain(): Character {
    return Character(
        id = id,
        displayName = displayName,
        wikiUrl = wikiUrl,
        aliasList = aliases.orEmpty().split(";"),
        images = Character.Images(
            iconUrl = imageIconUrl,
            bannerUrl = imageBannerUrl,
        )
    )
}