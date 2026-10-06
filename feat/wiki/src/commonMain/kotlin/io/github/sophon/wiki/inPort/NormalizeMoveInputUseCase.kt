package io.github.sophon.wiki.inPort

import io.github.sophon.wiki.model.wiki.Game

/**
 * Stored inputs are normalized per game on refresh - a user's `1,1,3` has to become `113` before it can match.
 */
interface NormalizeMoveInputUseCase {
    operator fun invoke(game: Game, input: String): String
}
