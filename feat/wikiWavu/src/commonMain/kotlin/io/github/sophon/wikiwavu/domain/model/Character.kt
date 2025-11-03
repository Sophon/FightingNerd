package io.github.sophon.wikiwavu.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Character(
    val id: String,
    val displayName: String,
    val wikiUrl: String,
    val aliasList: List<String> = listOf(),
    val images: Images? = null,
) {
    @Serializable
    data class Images(
        val url: String? = null,
        val officialUrl: String? = null,
    )
}