package io.github.sophon.wiki.model

import io.github.sophon.wiki.model.game.T8Properties

object WavuFilters {
    object PowerCrush: Filter {
        override val name: String = "PowerCrush"
        override val predicate: (Move) -> Boolean = { move ->
            (move.gameProperties as? T8Properties)?.isPowerCrush == true
        }
    }

    object Heat: Filter {
        override val name: String = "Heat"
        override val predicate: (Move) -> Boolean = { move ->
            (move.gameProperties as? T8Properties)?.isHeat == true
        }
    }

    object Homing: Filter {
        override val name: String = "Homing"
        override val predicate: (Move) -> Boolean = { move ->
            (move.gameProperties as? T8Properties)?.isHoming == true
        }
    }

    object Throw: Filter {
        override val name: String = "Throw"
        override val predicate: (Move) -> Boolean = { move ->
            move.isThrow && move.notes.isHitThrow().not()
        }
    }

    object Stance: Filter {
        override val name: String = "Stance"
        override val predicate: (Move) -> Boolean = { move ->
            (move.gameProperties as? T8Properties)?.stance?.isNotBlank() == true
        }
    }

    object HighCrush: Filter {
        override val name: String = "HighCrush"
        override val predicate: (Move) -> Boolean = { move ->
            (move.gameProperties as? T8Properties)?.isHighCrush == true
        }
    }

    object LowCrush: Filter {
        override val name: String = "LowCrush"
        override val predicate: (Move) -> Boolean = { move ->
            (move.gameProperties as? T8Properties)?.isLowCrush == true
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
}


private fun List<String>.isHitThrow(): Boolean {
    val isHitThrow = any { note ->
        val lowercaseNote = note.lowercase()
        lowercaseNote.contains("attack throw") || lowercaseNote.contains("hit throw")
    }
    return isHitThrow
}
