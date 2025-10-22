package model

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
    val crush: String? = null,
    val onBlock: String? = null,
    val onHit: String? = null,
    val onCH: String? = null,
    val notes: List<String> = listOf(),
    val alias: String? = null,
    val image: String? = null,
    val videoId: String? = null,
    val alt: String? = null,

    val isHeat: Boolean = false,
    val isPowerCrush: Boolean = false,
    val isHoming: Boolean = false,
)