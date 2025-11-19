package io.github.sophon.xko.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MoveListResponseDto(
    val bucketQuery: String,
    val bucket: List<MoveDto>
)

@Serializable
data class MoveDto(
    @SerialName("page_name")
    val pageName: String,
    val input: String,
    val damage: String,
    val guard: String,
    val startup: String,
    val active: String,
    val recovery: String,
    val onBlock: String,
    val cancel: String,
    val invuln: String? = null
)