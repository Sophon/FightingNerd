package io.github.sophon.core.wiki.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Character(
    val id: String, //used for local queries
    val displayName: String,
    val queryName: String, //used for remote queries
    val wikiUrl: String,
    val aliasList: List<String> = listOf(),
    val images: Images? = null,

    val sf6Properties: SF6Properties? = null,
    val airDashProperties: AirDashProperties? = null,
) {
    @Serializable
    data class Images(
        val iconUrl: String? = null,
        val bannerUrl: String? = null,
    )

    @Serializable
    data class SF6Properties(
        val fwdWalkSpd: String,
        val bwdWalkSpd: String,
        val fwdDashSpd: String,
        val bwdDashSpd: String,
        val fwdDashDist: String,
        val bwdDashDist: String,

        val dRushMin: String,
        val dRushBlock: String,
        val dRushMax: String,

        val hp: String,
        val throwRange: String,
        val throwHurtbox: String,
        val jumpSpd: String,
        val jumpApex: String,
        val fwdJumpDist: String,
        val bwdJumpDist: String,
    )

    @Serializable
    data class AirDashProperties(
        val defense: String?,
        val guts: String?,
        val guardBalance: String?,
        val prejump: String?,
        val bwdDash: String?,
        val bwdDashDuration: String?,
        val bwdDashInvulnerability: String?,
        val bwdDashAirborne: String?,
        val bwdDashDist: String?,
        val fwdDash: String?,
        val umo: List<String> = listOf(),
        val jumpDuration: String?,
        val highJumpDuration: String?,
        val jumpHeight: String?,
        val highJumpHeight: String?,
        val earliestIAD: String?,
        val adDuration: String?,
        val abdDuration: String?,
        val adDist: String?,
        val abdDist: String?,
        val movementTension: String?,
        val jumpTension: String?,
        val airDashTension: String?,
        val walkSpd: String?,
        val bwdWalkSpd: String?,
        val dashInitialSpd: String?,
        val dashAcceleration: String?,
        val dashFriction: String?,
        val jumpGravity: String?,
        val highJumpGravity: String?,
        val boostAttack: String?,
        val boostDefense: String?,
    )
}