package io.github.sophon.model

import kotlinx.serialization.Serializable

@Serializable
data class Command(
    val game: String,
    val name: String,
)
