package io.github.sophon.integration.model

import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val polarisId: String,
    val discordId: String?,
    val name: String? = null,
)