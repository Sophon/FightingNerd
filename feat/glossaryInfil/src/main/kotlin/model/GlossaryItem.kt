package model

import TERM_URL
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GlossaryItem(
    val term: String,
    @SerialName("def") val definition: String,
    @SerialName("altterm") val altTerm: List<String> = listOf(),
    val video: List<String> = listOf(),
    val games: List<String> = listOf(),
    @SerialName("jp") val jpTranslation: List<String> = listOf(),
)
