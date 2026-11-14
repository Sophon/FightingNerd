package io.github.sophon.wiki.adapter.outbound.ktor.dragDown

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class DragDownCharacterResponseDto(
    @SerialName("chara") val chara: String,
    @SerialName("dacus_speed_multiplier") val dacusSpeedMultiplier: Double? = null,
    @SerialName("weight") val weight: Int? = null,
    @SerialName("friction_ground") val frictionGround: Double? = null,
    @SerialName("friction_air") val frictionAir: Double? = null,
    @SerialName("dash_frames") val dashFrames: Int? = null,
    @SerialName("dash_speed") val dashSpeed: Double? = null,
    @SerialName("dash_acceleration") val dashAcceleration: Double? = null,
    @SerialName("run_speed_max") val runSpeedMax: Double? = null,
    @SerialName("run_turn_acceleration") val runTurnAcceleration: Double? = null,
    @SerialName("run_turn_frames") val runTurnFrames: Int? = null,
    @SerialName("walk_acceleration_max") val walkAccelerationMax: Double? = null,
    @SerialName("walk_speed_max") val walkSpeedMax: Double? = null,
    @SerialName("gravity") val gravity: Double? = null,
    @SerialName("hitstun_gravity") val hitstunGravity: Double? = null,
    @SerialName("fall_speed_max") val fallSpeedMax: Double? = null,
    @SerialName("hitstun_fall_speed_max") val hitstunFallSpeedMax: Double? = null,
    @SerialName("fast_fall_speed") val fastFallSpeed: Double? = null,
    @SerialName("air_acceleration") val airAcceleration: Double? = null,
    @SerialName("air_speed_horizontal_max") val airSpeedHorizontalMax: Double? = null,
    @SerialName("jump_speed_horizontal_max") val jumpSpeedHorizontalMax: Double? = null,
    @SerialName("full_hop_speed") val fullHopSpeed: Double? = null,
    @SerialName("short_hop_speed") val shortHopSpeed: Double? = null,
    @SerialName("double_jump_speed") val doubleJumpSpeed: Double? = null,
    @SerialName("double_jump_max_horizontal_speed") val doubleJumpMaxHorizontalSpeed: Double? = null,
    @SerialName("air_dodge_speed") val airDodgeSpeed: Double? = null,
    @SerialName("air_dodge_friction") val airDodgeFriction: Double? = null,
    @SerialName("roll_speed") val rollSpeed: Double? = null,
    @SerialName("shield_size_multiplier") val shieldSizeMultiplier: Double? = null,
    @SerialName("ledge_stand_speed") val ledgeStandSpeed: Double? = null,
    @SerialName("ledge_roll_speed") val ledgeRollSpeed: Double? = null,
    @SerialName("ledge_jump_max_horizontal_air_speed") val ledgeJumpMaxHorizontalAirSpeed: Double? = null,
    @SerialName("getup_roll_speed") val getupRollSpeed: Double? = null,
    @SerialName("tech_roll_speed") val techRollSpeed: Double? = null,
    @SerialName("wall_jump_speed_y") val wallJumpSpeedY: Double? = null,
    @SerialName("wall_jump_speed_x") val wallJumpSpeedX: Double? = null,
)
