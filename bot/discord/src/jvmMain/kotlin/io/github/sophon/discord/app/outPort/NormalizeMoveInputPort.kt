package io.github.sophon.discord.app.outPort

import io.github.sophon.wiki.model.wiki.Game

internal interface NormalizeMoveInputPort {
    fun normalizeMoveInput(game: Game, input: String): String
}
