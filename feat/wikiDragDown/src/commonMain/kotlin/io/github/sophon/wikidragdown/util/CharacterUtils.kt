package io.github.sophon.wikidragdown.util

import io.github.sophon.core.featureConfig.model.Game

internal fun String.formIcon(game: Game = Game.ROA2): String {
    val prefix = when (game) {
        Game.ROA2 -> "RoA2"
        else -> error("formIcon not configured for game: ${game.id}")
    }
    val normalized = this.replace(" ", "_")
    val filename = "${prefix}_${normalized}_Portrait.png"
    return filename
}
