package io.github.sophon.fightingnerd.app.model.game

import io.github.sophon.fightingnerd.app.model.MoveGameProperties

data class KOF15MoveProperties(
    val stun: String? = null,
): MoveGameProperties

data class COTWMoveProperties(
    val revDamage: String? = null,
): MoveGameProperties
