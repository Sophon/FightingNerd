package io.github.sophon.wiki.adapter.outbound.ktor.xko

import io.github.sophon.core.wiki.model.Character

/**
 * Xko has no character table - the character is built from the move's page name.
 */
internal fun String.toCharacter(): Character {
    val character = Character(
        id = lowercase(),
        displayName = this,
        remoteQueryId = this,
        wikiUrl = "$WIKI_BASE_URL/$this",
    )
    return character
}
