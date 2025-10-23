package com.example.wikiwavu.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class MoveListResponseDto(
    @SerialName("cargoquery") val cargoQuery: List<Title>,
) {
    @Serializable
    data class Title(
        @SerialName("title") val title: MoveDto
    )
}

@Serializable
internal data class MoveDto(
    val id: String,
    val input: String,
    val target: String? = null,
    val name: String? = null,
    val parent: String? = null,
    val damage: String? = null,
    val startup: String? = null,
    val recv: String? = null,
    val tot: String? = null,
    val crush: String? = null,
    val block: String? = null,
    val hit: String? = null,
    val ch: String? = null,
    val notes: String? = null,
    val alias: String? = null,
    val image: String? = null,
    val video: String? = null,
    val alt: String? = null,
)