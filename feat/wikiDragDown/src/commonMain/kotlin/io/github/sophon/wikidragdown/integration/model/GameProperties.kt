package io.github.sophon.wikidragdown.integration.model

import io.github.sophon.core.wiki.model.CharacterGameProperties
import kotlinx.serialization.Serializable

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
): CharacterGameProperties