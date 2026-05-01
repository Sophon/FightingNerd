package io.github.sophon.glossaryinfil.integration.model

import kotlinx.serialization.Serializable

@Serializable
data class GlossaryItem(
    val term: String,
    val definition: String,
    val altTerm: List<String> = listOf(),
    val games: List<String> = listOf(),
    val jpTranslation: List<String> = listOf(),
    val url: Url,
) {
    @Serializable
    data class Url(
        val term: String,
        val video: String? = null,
        val image: String? = null,
    )
}