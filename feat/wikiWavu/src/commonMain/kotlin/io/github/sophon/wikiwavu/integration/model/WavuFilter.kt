package io.github.sophon.wikiwavu.integration.model

import io.github.sophon.core.wiki.domain.Filter
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.wikiwavu.util.isHitThrow
import io.github.sophon.wikiwavu.util.isThrow

object WavuFilter {
    object PowerCrush: Filter {
        override val predicate: (Move) -> Boolean = {
            it.t8Properties?.isPowerCrush == true
        }
    }

    object Heat: Filter {
        override val predicate: (Move) -> Boolean = {
            it.t8Properties?.isHeat == true
        }
    }

    object Homing: Filter {
        override val predicate: (Move) -> Boolean = {
            it.t8Properties?.isHoming == true
        }
    }

    object Throw: Filter {
        override val predicate: (Move) -> Boolean = { move ->
            move.isThrow() && move.notes.isHitThrow().not()
        }
    }

    data class Strings(val startingMoveInput: String): Filter {
        override val predicate: (Move) -> Boolean = { move ->
            val nextChar = move.input.drop(startingMoveInput.length).getOrNull(0)
            val inputStartsWithQuery = move.input.startsWith(startingMoveInput)
                    || move.aliases.any { it.startsWith(startingMoveInput) }

            inputStartsWithQuery && nextChar != '+'
        }
    }
}