package io.github.sophon.wiki.application.domain.model

import io.github.sophon.wiki.application.domain.model.gameProperties.T8Properties

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
}
