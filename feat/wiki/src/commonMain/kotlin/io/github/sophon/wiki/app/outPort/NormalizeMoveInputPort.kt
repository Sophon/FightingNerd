package io.github.sophon.wiki.app.outPort

import io.github.sophon.wiki.model.wiki.Game

/**
 * The same input normalization the game's wiki adapter applies to the moves it maps.
 */
internal interface NormalizeMoveInputPort {
    fun normalizeMoveInput(game: Game, input: String): String
}
