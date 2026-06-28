package io.github.sophon.wikidragdown.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CharacterResponseDto(
    @SerialName("chara") val chara: String,
    @SerialName("DacusSpeedMultiplier") val dacusSpeedMultiplier: Float? = null,
    @SerialName("Weight") val weight: Int? = null,
    @SerialName("FrictionGround") val frictionGround: Float? = null,
    @SerialName("FrictionAir") val frictionAir: Float? = null,
    @SerialName("DashFrames") val dashFrames: Int? = null,
    @SerialName("DashSpeed") val dashSpeed: Float? = null,
    @SerialName("DashAcceleration") val dashAcceleration: Float? = null,
    @SerialName("RunSpeedMax") val runSpeedMax: Float? = null,
    @SerialName("RunTurnAcceleration") val runTurnAcceleration: Float? = null,
    @SerialName("RunTurnFrames") val runTurnFrames: Int? = null,
    @SerialName("WalkAccelerationMax") val walkAccelerationMax: Float? = null,
    @SerialName("WalkSpeedMax") val walkSpeedMax: Float? = null,
    @SerialName("Gravity") val gravity: Float? = null,
    @SerialName("HitstunGravity") val hitstunGravity: Float? = null,
    @SerialName("FallSpeedMax") val fallSpeedMax: Float? = null,
    @SerialName("FastFallSpeed") val fastFallSpeed: Float? = null,
    @SerialName("AirAcceleration") val airAcceleration: Float? = null,
    @SerialName("AirSpeedHorizontalMax") val airSpeedHorizontalMax: Float? = null,
    @SerialName("JumpSpeedHorizontalMax") val jumpSpeedHorizontalMax: Float? = null,
    @SerialName("FullHopSpeed") val fullHopSpeed: Float? = null,
    @SerialName("ShortHopSpeed") val shortHopSpeed: Float? = null,
    @SerialName("DoubleJumpSpeed") val doubleJumpSpeed: Float? = null,
    @SerialName("DoubleJumpMaxHorizontalSpeed") val doubleJumpMaxHorizontalSpeed: Float? = null,
    @SerialName("AirDodgeSpeed") val airDodgeSpeed: Float? = null,
    @SerialName("AirDodgeFriction") val airDodgeFriction: Float? = null,
    @SerialName("RollSpeed") val rollSpeed: Float? = null,
    @SerialName("ShieldSizeMultiplier") val shieldSizeMultiplier: Float? = null,
    @SerialName("LedgeStandSpeed") val ledgeStandSpeed: Float? = null,
    @SerialName("LedgeRollSpeed") val ledgeRollSpeed: Float? = null,
    @SerialName("LedgeJumpMaxHorizontalAirSpeed") val ledgeJumpMaxHorizontalAirSpeed: Float? = null,
    @SerialName("GetupRollSpeed") val getupRollSpeed: Float? = null,
    @SerialName("TechRollSpeed") val techRollSpeed: Float? = null,
    @SerialName("WallJumpSpeedY") val wallJumpSpeedY: Float? = null,
    @SerialName("WallJumpSpeedX") val wallJumpSpeedX: Float? = null,
)
