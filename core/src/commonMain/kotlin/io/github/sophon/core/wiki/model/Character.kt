package io.github.sophon.core.wiki.model

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
data class Character(
    val id: String, //used for local queries
    val displayName: String,
    val remoteQueryId: String,
    val wikiUrl: String,
    val aliasList: List<String> = listOf(),
    val images: Images? = null,

    val hp: String? = null,
    val umo: List<String> = listOf(),

    val sf6Properties: SF6Properties? = null,
    val ggstProperties: GGSTProperties? = null,
    val mkProperties: MK1Properties? = null,
    val dbfzProperties: DBFZProperties? = null,
    val gbvsrProperties: GBVSRProperties? = null,
    val bbProperties: BBProperties? = null,
    val uni2Properties: Uni2Properties? = null,
    val roa2Properties: Roa2Properties? = null,
    val mtfsProperties: MTFSProperties? = null,
) {
    @Serializable
    data class Images(
        val iconId: String? = null,
        val iconUrl: String? = null,
        val bannerUrl: String? = null,
    )

    @Serializable
    data class SF6Properties(
        val fwdWalkSpd: String?,
        val bwdWalkSpd: String?,
        val fwdDashSpd: String?,
        val bwdDashSpd: String?,
        val fwdDashDist: String?,
        val bwdDashDist: String?,

        val dRushMin: String?,
        val dRushBlock: String?,
        val dRushMax: String?,

        val throwRange: String?,
        val throwHurtbox: String?,
        val jumpSpd: String?,
        val jumpApex: String?,
        val fwdJumpDist: String?,
        val bwdJumpDist: String?,
    )

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
    )

    @Serializable
    data class DBFZProperties(
        val kiMod: String?,
    )

    @Serializable
    data class GBVSRProperties(
        val jump: Jump?,
        val backdash: String?,
        val walkSpeed: String?,
        val walkSpeedBack: String?,
        val dashInitial: String?,
        val dashAcceleration: String?,
        val closeRange: CloseRange?,
    ) {
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
    )

    @Serializable
    data class MK1Properties(
        val hpMod: String? = null,
        val throwDmg: String? = null,
    )

    @Serializable
    data class BBProperties(
        val preJump: String? = null,
        val backDash: String? = null,
        val forwardDash: String? = null,
    )

    @Serializable
    data class Uni2Properties(
        val smartSteer: String? = null,
        val fWalkSpeed: String? = null,
        val fWalkSpeedNote: String? = null,
        val bWalkSpeed: String? = null,
        val bWalkSpeedNote: String? = null,
        val jumpStartup: String? = null,
        val jumpDuration: String? = null,
        val jumpDurationNote: String? = null,
        val dashStartup: String? = null,
        val iDashSpeed: String? = null,
        val iDashSpeedNote: String? = null,
        val dashAccel: String? = null,
        val dashAccelNote: String? = null,
        val maxDashSpeed: String? = null,
        val bDashStartup: String? = null,
        val bDashDuration: String? = null,
        val bDashDurationNote: String? = null,
        val bDashDistance: String? = null,
        val bDashDistanceNote: String? = null,
        val bDashFullInvulStart: String? = null,
        val bDashFullInvulEnd: String? = null,
        val bDashThrowInvulStart: String? = null,
        val bDashThrowInvulEnd: String? = null,
        val throwWidth: String? = null,
        val throwRange: String? = null,
        val trait: String? = null,
        val vorpalTrait: String? = null,
    )

    @Serializable
    data class Roa2Properties(
        val dacusSpeedMultiplier: String? = null,
        val weight: String? = null,
        val frictionGround: String? = null,
        val frictionAir: String? = null,
        val dashFrames: String? = null,
        val dashSpeed: String? = null,
        val dashAcceleration: String? = null,
        val runSpeedMax: String? = null,
        val runTurnAcceleration: String? = null,
        val runTurnFrames: String? = null,
        val walkAccelerationMax: String? = null,
        val walkSpeedMax: String? = null,
        val gravity: String? = null,
        val hitstunGravity: String? = null,
        val fallSpeedMax: String? = null,
        val fastFallSpeed: String? = null,
        val airAcceleration: String? = null,
        val airSpeedHorizontalMax: String? = null,
        val jumpSpeedHorizontalMax: String? = null,
        val fullHopSpeed: String? = null,
        val shortHopSpeed: String? = null,
        val doubleJumpSpeed: String? = null,
        val doubleJumpMaxHorizontalSpeed: String? = null,
        val airDodgeSpeed: String? = null,
        val airDodgeFriction: String? = null,
        val rollSpeed: String? = null,
        val shieldSizeMultiplier: String? = null,
        val ledgeStandSpeed: String? = null,
        val ledgeRollSpeed: String? = null,
        val ledgeJumpMaxHorizontalAirSpeed: String? = null,
        val getupRollSpeed: String? = null,
        val techRollSpeed: String? = null,
        val wallJumpSpeedY: String? = null,
        val wallJumpSpeedX: String? = null,
    )
}

@JvmInline
value class CharacterId(val value: String)