package io.github.sophon.wikimizuumi.integration.model

import io.github.sophon.core.wiki.model.CharacterGameProperties
import io.github.sophon.core.wiki.model.MoveGameProperties
import kotlinx.serialization.Serializable

@Serializable
data class Uni2CharProperties(
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
): CharacterGameProperties

@Serializable
data class MBTLMoveProperties(
    val inputInfo: String? = null,
    val subtitle: String? = null,
    val minDamage: String? = null,
    val property: String? = null,
    val cost: String? = null,
    val attribute: String? = null,
    val landing: String? = null,
    val overall: String? = null,
): MoveGameProperties

@Serializable
data class Uni2MoveProperties(
    val inputInfo: String? = null,
    val subtitle: String? = null,
    val minDamage: String? = null,
    val cancelWindow: String? = null,
    val property: String? = null,
    val cost: String? = null,
    val attribute: String? = null,
    val landing: String? = null,
    val overall: String? = null,
    val assaultAdv: String? = null,
    val blockstun: String? = null,
    val groundHit: String? = null,
    val airHit: String? = null,
    val groundCH: String? = null,
    val airCH: String? = null,
    val hitstop: String? = null,
    val CHstop: String? = null,
    val proration: String? = null,
    val comboP1: String? = null,
    val comboP2: String? = null,
): MoveGameProperties

@Serializable
data class VSAVMoveProperties(
    val inputInfo: String? = null,
    val subtitle: String? = null,
    val whiteDmg: String? = null,
    val renda: String? = null,
    val meter: String? = null,
    val reaction: String? = null,
    val curseTime: String? = null,
): MoveGameProperties
