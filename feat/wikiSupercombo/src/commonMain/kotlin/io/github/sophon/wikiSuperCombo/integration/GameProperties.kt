package io.github.sophon.wikiSuperCombo.integration

import io.github.sophon.core.wiki.model.CharacterGameProperties
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
