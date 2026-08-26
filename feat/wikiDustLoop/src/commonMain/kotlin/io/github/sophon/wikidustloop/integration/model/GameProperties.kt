package io.github.sophon.wikidustloop.integration.model

import io.github.sophon.core.wiki.model.CharacterGameProperties
import kotlinx.serialization.Serializable

@Serializable
data class GGSTProperties(
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
): CharacterGameProperties

@Serializable
data class BBProperties(
    val preJump: String? = null,
    val backDash: String? = null,
    val forwardDash: String? = null,
): CharacterGameProperties

@Serializable
data class DBFZProperties(
    val kiMod: String?,
): CharacterGameProperties

@Serializable
data class GBVSRProperties(
    val jump: Jump?,
    val backdash: String?,
    val walkSpeed: String?,
    val walkSpeedBack: String?,
    val dashInitial: String?,
    val dashAcceleration: String?,
    val closeRange: CloseRange?,
): CharacterGameProperties {
    @Serializable
    data class Jump(
        val pre: String?,
        val forwardDistance: String?,
        val superForwardDistance: String?,
        val backDistance: String?,
        val superBackDistance: String?,
        val gravity: String?,
        val superGravity: String?,
        val superHeight: String?,
    )

    @Serializable
    data class CloseRange(
        val l: String?,
        val m: String?,
        val h: String?,
    )
}

@Serializable
data class MTFSProperties(
    val prejump: String? = null,
    val backdash: String? = null,
    val team: String? = null,
): CharacterGameProperties

