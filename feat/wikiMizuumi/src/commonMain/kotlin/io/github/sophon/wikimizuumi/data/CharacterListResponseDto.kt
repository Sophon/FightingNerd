package io.github.sophon.wikimizuumi.data

import kotlinx.serialization.Serializable

@Serializable
internal data class CharacterListResponseDto(
    val cargoquery: List<CharacterTitle>
)

@Serializable
internal data class CharacterTitle(
    val title: UniCharacterDto
)

@Serializable
internal data class UniCharacterDto(
    val chara: String,
    val smartSteer: String? = null,
    val health: String? = null,
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