package io.github.sophon.wikiwavu.domain.model

import kotlinx.serialization.Serializable

/**
 * @see <a href="https://wavu.wiki/t/Template:Move">Wavu Wiki Move Template</a>
 */
@Serializable
data class Move(
    val charName: String,
    val id: String,
    val input: String,
    val level: String? = null,
    val name: String? = null,
    val parent: String? = null,
    val damage: String? = null,
    val startup: String? = null,
    val recoveryOnWhiff: String? = null,
    val totalFrames: String? = null,
    val crushes: List<String> = listOf(),
    val onBlock: String? = null,
    val onHit: String? = null,
    val onCH: String? = null,
    val notes: List<String> = listOf(),
    val aliases: List <String> = listOf(),
    val image: String? = null,
    val videoId: String? = null,
    val alt: String? = null,

    val properties: Properties = Properties(),
) {
    //null means non applicable
    @Serializable
    data class Properties(
        val isHeat: Boolean? = null,
        val isPowerCrush: Boolean? = null,
        val isHoming: Boolean? = null,
        val stance: String? = null,
    )
}