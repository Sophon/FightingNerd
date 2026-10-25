package io.github.sophon.fightingnerd.app.model.game

data class KOF15MoveProperties(
    val stun: String? = null,
): MoveGameProperties

data class COTWMoveProperties(
    val revDamage: String? = null,
): MoveGameProperties
