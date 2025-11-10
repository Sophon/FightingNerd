package io.github.sophon.wikiSuperCombo.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CharacterListResponseDto(
    val cargoquery: List<CargoQueryItem>,
)

@Serializable
data class CargoQueryItem(
    val title: CharacterDto
)

@Serializable
data class CharacterDto(
    @SerialName("Character") val character: String,
    val chara: String,
    val name: String,
    val portrait: String,
    val icon: String,
    val hp: String,
    val throwRange: String,
    val throwHurtbox: String,
    val fwdWalkSpd: String,
    val bwdWalkSpd: String,
    val fwdDashSpd: String,
    val bwdDashSpd: String,
    val fwdDashDist: String,
    val bwdDashDist: String,
    val jumpSpd: String,
    val jumpApex: String,
    val fwdJumpDist: String,
    val bwdJumpDist: String,
    val dRushMin: String,
    val dRushBlock: String,
    val dRushMax: String,
)
