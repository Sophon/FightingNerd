package io.github.sophon.core.wiki.model

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
data class Character(
    val id: String, //used for local queries
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

@JvmInline
value class CharacterId(val value: String)