package io.github.sophon.glossaryinfil.data

import kotlinx.serialization.Serializable

@Serializable
internal data class GlossaryItemDto(
    val term: String,
    val def: String,
    val altterm: List<String>? = null,
    val video: List<String>? = null,
    val games: List<String>? = null,
    val jp: String? = null,
)
