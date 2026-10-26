package io.github.sophon.fightingnerd.app.model.game

import io.github.sophon.fightingnerd.app.model.Wiki

data class Game(
    val id: String,
    val displayName: String,
    val iconUrl: String,
    val wiki: Wiki,
) {
    val shortDisplayName: String
        get() {
            return displayName.substringBefore(":")
        }
}
