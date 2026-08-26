package io.github.sophon.wikiSuperCombo.integration.model

import io.github.sophon.core.wiki.model.CharacterGameProperties
import io.github.sophon.core.wiki.model.MoveGameProperties
import kotlinx.serialization.Serializable

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
): CharacterGameProperties

@Serializable
data class MK1Properties(
    val hpMod: String? = null,
    val throwDmg: String? = null,
): CharacterGameProperties


@Serializable
data class SF6MoveProperties(
    val type: Type? = null,
    val images: List<String>? = null,
    val chip: String? = null,
    val dmgScaling: String? = null,
    val total: String? = null,
    val hitConfirm: String? = null,
    val punishAdv: String? = null,
    val perfParryAdv: String? = null,
    val DRcOH: String? = null,
    val DRcOB: String? = null,
    val DROH: String? = null,
    val DROB: String? = null,
    val hitStun: String? = null,
    val blockStun: String? = null,
    val hitStop: String? = null,
    val driveDmgOnBlock: String? = null,
    val driveDmgOnHit: String? = null,
    val driveGain: String? = null,
    val superGainOnHit: String? = null,
    val superGainOnBlock: String? = null,
    val armor: String? = null,
    val airborne: String? = null,
    val jugStart: String? = null,
    val jugIncrease: String? = null,
    val jugLimit: String? = null,
    val projectileSpeed: String? = null,
    val attackRange: String? = null,
): MoveGameProperties {
    enum class Type {
        GROUND_NORMAL,
        AIR_NORMAL,
        SPECIAL,
        SUPER,
        THROW,
        DRIVE,
        TAUNT,
    }
}

@Serializable
data class AVLProperties(
    val chiDamage: String? = null,
    val flow: String? = null,
    val type: String? = null,
): MoveGameProperties

@Serializable
data class MKMoveProperties(
    val moveType: String? = null,
    val cost: List<String> = listOf(),
    val chip: String? = null,
    val flawlessBlockAdv: String? = null,
    val hitCancelAdv: String? = null,
    val blockCancelAdv: String? = null,
    val punish: String? = null,
): MoveGameProperties
