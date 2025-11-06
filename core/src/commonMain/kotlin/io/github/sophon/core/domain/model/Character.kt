package io.github.sophon.core.domain.model

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
        val iconUrl: String? = null,
        val bannerUrl: String? = null,
    )
}