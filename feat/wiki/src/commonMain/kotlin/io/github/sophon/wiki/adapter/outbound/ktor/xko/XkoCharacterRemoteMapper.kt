package io.github.sophon.wiki.adapter.outbound.ktor.xko

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.wiki.application.domain.model.Character
import io.github.sophon.wiki.application.domain.model.CharacterId

/**
 * Xko has no character table - the character is built from the move's page name.
 */
internal fun String.toCharacter(game: Game): Character {
    val character = Character(
        id = CharacterId(game, this),
        displayName = this,
        remoteQueryId = this,
        wikiUrl = "$WIKI_BASE_URL/$this",
    )
    return character
}
