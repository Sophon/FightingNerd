package domain

import kotlinx.serialization.Serializable

@Serializable
data class GlossaryItem(
    val term: String,
    val definition: String,
    val altTerm: List<String> = listOf(),
    val video: List<String> = listOf(),
    val games: List<String> = listOf(),
    val jpTranslation: List<String> = listOf(),
)