package io.github.sophon.wikiwavu.integration.model

import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wikiwavu.util.isHitThrow
import io.github.sophon.wikiwavu.util.isThrow

object TekkenFilters {
    object PowerCrush: Filter {
        override val name: String = "PowerCrush"
        override val predicate: (Move) -> Boolean = {
            (it.gameProperties as? T8Properties)?.isPowerCrush == true
        }
    }

    object Heat: Filter {
        override val name: String = "Heat"
        override val predicate: (Move) -> Boolean = {
            (it.gameProperties as? T8Properties)?.isHeat == true
        }
    }

    object Homing: Filter {
        override val name: String = "Homing"
        override val predicate: (Move) -> Boolean = {
            (it.gameProperties as? T8Properties)?.isHoming == true
        }
    }

    object Throw: Filter {
        override val name: String = "Throw"
        override val predicate: (Move) -> Boolean = { move ->
            move.isThrow() && move.notes.isHitThrow().not()
        }
    }

    object Stance: Filter {
        override val name: String = "Stance"
        override val predicate: (Move) -> Boolean = {
            (it.gameProperties as? T8Properties)?.stance?.isNotBlank() == true
        }
    }

    object HighCrush: Filter {
        override val name: String = "HighCrush"
        override val predicate: (Move) -> Boolean = {
            (it.gameProperties as? T8Properties)?.isHighCrush == true
        }
    }

    object LowCrush: Filter {
        override val name: String = "LowCrush"
        override val predicate: (Move) -> Boolean = {
            (it.gameProperties as? T8Properties)?.isLowCrush == true
        }
    }

    data class Strings(val startingMoveInput: String): Filter {
        override val name: String = "Strings"
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
            LowCrush,
            HighCrush,
        )
    }
}
