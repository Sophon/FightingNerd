package io.github.sophon.model

import kotlinx.serialization.Serializable

@Serializable
data class Usage(
    val game: String?,
    val command: String,
    val count: Long,
)
