package io.github.sophon.wiki.model.game

import io.github.sophon.wiki.model.MoveGameProperties
import kotlinx.serialization.Serializable

@Serializable
data class KOF15MoveProperties(
    val stun: String? = null,
): MoveGameProperties

@Serializable
data class COTWMoveProperties(
    val revDamage: String? = null,
): MoveGameProperties
