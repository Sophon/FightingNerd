package io.github.sophon.wiki.adapter.outbound.sqldelight

import io.github.sophon.wiki.application.domain.model.Character
import io.github.sophon.wiki.application.domain.model.CharacterGameProperties
import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.data.Character as CharacterEntity

internal fun CharacterEntity.toDomain(
    aliasList: List<String>,
    gameProperties: CharacterGameProperties?,
): Character {
    val images = Character.Images(
        iconId = icon_id,
        iconUrl = icon_url,
        bannerUrl = banner_url,
    )

    val character = Character(
        id = CharacterId(natural_id),
        displayName = display_name,
        remoteQueryId = remote_query_id,
        wikiUrl = wiki_url,
        aliasList = aliasList,
        images = images.takeUnless { it == Character.Images() },
        hp = hp,
        umo = umo,
        gameProperties = gameProperties,
    )
    return character
}
