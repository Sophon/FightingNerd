package io.github.sophon.wikiSuperCombo.data.remote

import kotlinx.serialization.Serializable

@Serializable
internal data class CharacterListResponseDto(
    val cargoquery: List<CargoQueryItem>,
)

@Serializable
internal data class CargoQueryItem(
    val title: CharacterDto
)

@Serializable
internal data class CharacterDto(
    val Character: String? = null,
    val chara: String,
    val name: String? = null,
    val portrait: String? = null,
    val icon: String? = null,
    val hp: String? = null,
    val throwRange: String? = null,
    val throwHurtbox: String? = null,
    val fwdWalkSpd: String? = null,
    val bwdWalkSpd: String? = null,
    val fwdDashSpd: String? = null,
    val bwdDashSpd: String? = null,
    val fwdDashDist: String? = null,
    val bwdDashDist: String? = null,
    val jumpSpd: String? = null,
    val jumpApex: String? = null,
    val fwdJumpDist: String? = null,
    val bwdJumpDist: String? = null,
    val dRushMin: String? = null,
    val dRushBlock: String? = null,
    val dRushMax: String? = null,

    val hpmod: String? = null,
    val throwdmg: String? = null
)
