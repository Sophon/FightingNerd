package io.github.sophon.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Hit(
    val feature: String,
    val command: String,
)