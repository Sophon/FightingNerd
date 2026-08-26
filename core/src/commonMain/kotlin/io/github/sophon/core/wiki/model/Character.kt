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

    val gameProperties: CharacterGameProperties? = null,

    val uni2Properties: Uni2Properties? = null,
    val roa2Properties: Roa2Properties? = null,
) {
    @Serializable
    data class Images(
        val iconId: String? = null,
        val iconUrl: String? = null,
        val bannerUrl: String? = null,
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