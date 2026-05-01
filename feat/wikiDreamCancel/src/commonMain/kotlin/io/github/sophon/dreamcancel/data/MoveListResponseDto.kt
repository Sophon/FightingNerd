package io.github.sophon.dreamcancel.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class MoveListResponseDto(
    @SerialName("cargoquery")
    val cargoQuery: List<Title>
)

@Serializable
internal data class Title(
    val title: MoveDto
)

@Serializable
internal data class MoveDto(
    val chara: String,
    val moveId: String,
    val name: String? = null,
    val idle: String? = null,
    val rank: String? = null,
    val input: String? = null,
    val images: String? = null,
    val hitboxes: String? = null,
    val damage: String? = null,
    val guard: String? = null,
    val cancel: String? = null,
    val startup: String? = null,
    val active: String? = null,
    val recovery: String? = null,
    @SerialName("hitadv") val hitAdv: String? = null,
    @SerialName("blockadv") val blockAdv: String? = null,
    val invul: String? = null,
    val stun: String? = null,
    @SerialName("revdamage") val revDamage: String? = null,
    val guardDamage: String? = null,
)