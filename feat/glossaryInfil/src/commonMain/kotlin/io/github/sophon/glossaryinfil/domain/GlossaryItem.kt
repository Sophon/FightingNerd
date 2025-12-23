package io.github.sophon.glossaryinfil.domain

import kotlinx.serialization.Serializable

@Serializable
data class GlossaryItem(
    val term: String,
    val definition: String,
    val altTerm: List<String> = listOf(),
    val videoUrl: String? = null,
    val imageUrl: String? = null,
    val games: List<String> = listOf(),
    val jpTranslation: List<String> = listOf(),
)
