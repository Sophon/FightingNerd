package io.github.sophon.domain

import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val polarisId: String,
    val discordId: String?,
    val name: String?,
)
