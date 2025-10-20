package dataRemote

import kotlinx.serialization.Serializable

@Serializable
data class GlossaryItemDto(
    val term: String,
    val def: String,
    val altterm: List<String> = listOf(),
    val video: List<String> = listOf(),
    val games: List<String> = listOf(),
    val jp: String? = null,
)
