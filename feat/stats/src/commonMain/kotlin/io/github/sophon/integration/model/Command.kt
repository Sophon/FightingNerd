package io.github.sophon.integration.model

import kotlinx.serialization.Serializable

@Serializable
data class Command(
    val feature: String,
    val name: String,
)
