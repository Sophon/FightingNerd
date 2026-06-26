package io.github.sophon.wikidustloop.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CharacterListResponseDto(
    @SerialName("cargoquery") val cargoQuery: List<CargoQueryItem>,
)

@Serializable
internal data class CargoQueryItem(
    val title: CharacterDto,
)

@Serializable
internal data class CharacterDto(
    val name: String? = null,
    val portrait: String? = null,
    val icon: String? = null,
    @SerialName("nav image")
    val navImage: String? = null,
    val umo: String? = null,
    val aliases: String? = null,

    //gg
    val defense: String? = null,
    val guts: String? = null,
    val guardBalance: String? = null,
    val prejump: String? = null,
    val backdash: String? = null,
    val backdashDuration: String? = null,
    val backdashInvuln: String? = null,
    val backdashAirborne: String? = null,
    val backdashDistance: String? = null,

    //db
    @SerialName("forwarddash")
    val forwardDash: String? = null,
    @SerialName("jump duration")
    val jumpDuration: String? = null,
    @SerialName("high jump duration")
    val highJumpDuration: String? = null,
    @SerialName("jump height")
    val jumpHeight: String? = null,
    @SerialName("high jump height")
    val highJumpHeight: String? = null,
    @SerialName("earliest iad")
    val earliestIad: String? = null,
    @SerialName("ad duration")
    val adDuration: String? = null,
    @SerialName("abd duration")
    val abdDuration: String? = null,
    @SerialName("ad distance")
    val adDistance: String? = null,
    @SerialName("abd distance")
    val abdDistance: String? = null,
    @SerialName("movement tension")
    val movementTension: String? = null,
    @SerialName("jump tension")
    val jumpTension: String? = null,
    @SerialName("airdash tension")
    val airDashTension: String? = null,
    @SerialName("walk speed")
    val walkSpeed: String? = null,
    @SerialName("back walk speed")
    val backWalkSpeed: String? = null,
    @SerialName("dash initial speed")
    val dashInitialSpeed: String? = null,
    @SerialName("dash acceleration")
    val dashAcceleration: String? = null,
    @SerialName("dash friction")
    val dashFriction: String? = null,
    @SerialName("jump gravity")
    val jumpGravity: String? = null,
    @SerialName("high jump gravity")
    val highJumpGravity: String? = null,
    @SerialName("boost attack")
    val boostAttack: String? = null,
    @SerialName("boost defense")
    val boostDefense: String? = null,
    val kimod: String? = null,

    //gbvs
    val walk_speed: Float? = null,
    val backwalk_speed: Float? = null,
    val dash_initial_speed: Float? = null,
    val dash_acceleration: String? = null,
    val jump_height: Float? = null,
    val f_jump_distance: Float? = null,
    val b_jump_distance: Float? = null,
    val jump_gravity: Float? = null,
    val superjump_height: Float? = null,
    val f_superjump_distance: Float? = null,
    val b_superjump_distance: Float? = null,
    val superjump_gravity: Float? = null,
    val close_l_range: Float? = null,
    val close_m_range: Float? = null,
    val close_h_range: Float? = null,

    //bb
    val health: String? = null,
)