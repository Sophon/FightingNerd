package io.github.sophon.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Command(
    val feature: String,
    val name: String,
)
