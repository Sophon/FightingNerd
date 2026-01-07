package io.github.sophon.xko.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MoveListResponseDto(
    val bucketQuery: String,
    val bucket: List<MoveDto> = listOf()
)

@Serializable
data class MoveDto(
    @SerialName("page_name") val pageName: String,
    val input: String? = null,
    val damage: String? = null,
    val guard: String? = null,
    val startup: String,
    val active: String? = null,
    val recovery: String,
    @SerialName("onblock") val onBlock: String? = null,
    val cancel: String? = null,
    val invuln: String? = null
)