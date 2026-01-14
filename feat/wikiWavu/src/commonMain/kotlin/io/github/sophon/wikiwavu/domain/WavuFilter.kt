package io.github.sophon.wikiwavu.domain

import io.github.sophon.core.wiki.domain.Filter
import io.github.sophon.core.wiki.domain.model.Move

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
}