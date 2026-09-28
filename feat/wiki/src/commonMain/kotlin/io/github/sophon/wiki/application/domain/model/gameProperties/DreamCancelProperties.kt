package io.github.sophon.wiki.application.domain.model.gameProperties

import io.github.sophon.core.wiki.model.MoveGameProperties
import kotlinx.serialization.Serializable

@Serializable
data class KOF15MoveProperties(
    val stun: String? = null,
): MoveGameProperties

@Serializable
data class COTWMoveProperties(
    val revDamage: String? = null,
): MoveGameProperties
