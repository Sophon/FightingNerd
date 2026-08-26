package io.github.sophon.wikidustloop.integration.model

import io.github.sophon.core.wiki.model.CharacterGameProperties
import io.github.sophon.core.wiki.model.MoveGameProperties
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

@Serializable
data class GGSTMoveProperties(
    val type: String? = null,
    val riscGain: String? = null,
    val riscLoss: String? = null,
    val wallDamage: String? = null,
    val inputTension: String? = null,
    val chipRatio: String? = null,
    val otgType: String? = null,
    val prorate: String? = null,
    val level: String? = null,
): MoveGameProperties


@Serializable
data class BBMoveProperties(
    val onODR: String? = null,
    val attribute: String? = null,
    val p1: String? = null,
    val p2: String? = null,
    val starter: String? = null,
    val level: String? = null,
    val blockstun: String? = null,
    val groundHit: String? = null,
    val airHit: String? = null,
    val groundCH: String? = null,
    val airCH: String? = null,
    val blockstop: String? = null,
    val hitstop: String? = null,
    val chStop: String? = null,
    val cancelTiming: String? = null,
    val type: String? = null
): MoveGameProperties

@Serializable
data class DBFZMoveProperties(
    val attribute: String? = null,
    val smash: String? = null,
    val kiGain: String? = null,
    val prorate: String? = null,
    val blockStun: String? = null,
    val groundHit: String? = null,
    val airHit: String? = null,
    val type: String? = null,
    val level: String? = null,
): MoveGameProperties

@Serializable
data class GBVSRMoveProperties(
    val meter: String? = null,
    val level: String? = null,
    val cooldown: String? = null,
    val cls: String? = null,
    val type: String? = null,
): MoveGameProperties

@Serializable
data class MTFSMoveProperties(
    val simpleInput: String? = null,
    val type: String? = null,
    val level: String? = null,
    val prorate: String? = null,
    val meterGain: String? = null,
    val untechAmount: String? = null,
    val hitboxCaption: String? = null,
): MoveGameProperties
