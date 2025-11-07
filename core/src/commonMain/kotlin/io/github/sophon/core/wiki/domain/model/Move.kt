package io.github.sophon.core.wiki.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Move(
    val charName: String,
    val id: String,
    val input: String,
    val damage: String? = null,
    val startup: String? = null,
    val onBlock: String? = null,
    val onHit: String? = null,
    val onCH: String? = null,
    val name: String? = null,
    val recovery: String? = null,
    val notes: List<String> = listOf(),
    val aliases: List <String> = listOf(),
    val videoId: String? = null,

    val t8Properties: T8Properties? = null,
) {

    @Serializable
    data class T8Properties(
        val level: String? = null,
        val isHeat: Boolean = false,
        val isHoming: Boolean = false,
        val stance: String? = null,
        val isPowerCrush: Boolean = false,
        val isHighCrush: Boolean = false,
        val isLowCrush: Boolean = false,
    )
}