package io.github.sophon.wiki.model

import io.github.sophon.wiki.model.wiki.Game
import kotlinx.serialization.Serializable

@Serializable
data class Character(
    val id: CharacterId,
    val displayName: String,
    val remoteQueryId: String,
    val wikiUrl: String,
    val aliasList: List<String> = listOf(),
    val images: Images? = null,

    val hp: String? = null,
    val umo: List<String> = listOf(),

    val gameProperties: CharacterGameProperties? = null,
) {
    @Serializable
    data class Images(
        val iconId: String? = null,
        val iconUrl: String? = null,
        val bannerUrl: String? = null,
    )
}

/**
 * A character in a game - crossovers (Mai in SF, KOF and COTW) are different characters.
 * [naturalId] is the normalized `remoteQueryId` - lowercase, spaces as `_` (`Armor King` -> `armor_king`),
 * unique only within [game].
 * Stable across refreshes and wipes; never shown to the user.
 */
@Serializable
data class CharacterId(
    val game: Game,
    val naturalId: String,
)
