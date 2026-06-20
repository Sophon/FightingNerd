package io.github.sophon.wikiwavu.integration.model

import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wikiwavu.util.isHitThrow
import io.github.sophon.wikiwavu.util.isThrow

object TekkenFilters {
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

    object Stance: Filter {
        override val predicate: (Move) -> Boolean = { move ->
            move.t8Properties?.stance?.isNotBlank() == true
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


    fun getAllBinaryFilters(): Set<Filter> {
        return setOf(
            PowerCrush,
            Heat,
            Homing,
            Throw,
            Stance,
//            Strings,
        )
    }
}
