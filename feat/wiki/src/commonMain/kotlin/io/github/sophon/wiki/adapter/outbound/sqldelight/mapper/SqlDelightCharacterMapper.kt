package io.github.sophon.wiki.adapter.outbound.sqldelight.mapper

import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.CharacterGameProperties
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.wiki.Game
import io.github.sophon.wiki.data.Character as CharacterEntity

/**
 * [game] is the one the rows were selected by - the `game` column holds its [Game.id].
 */
internal fun CharacterEntity.toDomain(
    game: Game,
    aliasList: List<String>,
    gameProperties: CharacterGameProperties?,
): Character {
    val images = Character.Images(
        iconId = icon_id,
        iconUrl = icon_url,
        bannerUrl = banner_url,
    )

    val character = Character(
        id = CharacterId(game, natural_id),
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
