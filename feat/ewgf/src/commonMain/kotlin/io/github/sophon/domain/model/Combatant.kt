package io.github.sophon.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Combatant(
    val name: String,
    val polarisId: String,
    val character: String,
    val rank: String,
    val prowess: Int,
    val region: Region,
)
