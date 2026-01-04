package io.github.sophon.wikimizuumi.data

import kotlinx.serialization.Serializable

@Serializable
data class MoveListResponseDto(
    val cargoquery: List<Title>
)

@Serializable
data class Title(
    val title: MoveDto
)

@Serializable
data class MoveDto(
    val moveId: String,
    val chara: String,
    val input: String? = null,
    val inputInfo: String? = null,
    val name: String? = null,
    val subtitle: String? = null,
    val images: String? = null,
    val hitboxes: String? = null,
    val damage: String? = null,
    val minDamage: String? = null,
    val guard: String? = null,
    val cancel: String? = null,
    val property: String? = null,
    val cost: String? = null,
    val attribute: String? = null,
    val startup: String? = null,
    val active: String? = null,
    val recovery: String? = null,
    val landing: String? = null,
    val overall: String? = null,
    val frameAdv: String? = null,
    val invul: String? = null
)