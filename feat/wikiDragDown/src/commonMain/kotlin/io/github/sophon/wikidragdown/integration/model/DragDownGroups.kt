package io.github.sophon.wikidragdown.integration.model

import io.github.sophon.core.wiki.model.Group
import io.github.sophon.core.wiki.model.Move

object RoaGroups {
    object Normal: Group {
        override val id: String = "Normal"
        override val predicate: (Move) -> Boolean = { move ->
            val input = move.input
            val isJab = input.startsWith("jab", ignoreCase = true)
            val isTilt = input.endsWith("tilt", ignoreCase = true)

            isJab || isTilt
        }
    }

    object Strong: Group {
        override val id: String = "Strong"
        override val predicate: (Move) -> Boolean = { move ->
            move.input.contains("strong", ignoreCase = true)
        }
    }

    object Aerial: Group {
        override val id: String = "Aerial"
        override val predicate: (Move) -> Boolean = {
            it.input.endsWith("air", ignoreCase = true)
        }
    }

    object Special: Group {
        override val id: String = "Special"
        override val predicate: (Move) -> Boolean = {
            it.input.endsWith("special", ignoreCase = true)
        }
    }

    object Throw: Group {
        override val id: String = "Throw"
        override val predicate: (Move) -> Boolean = {
            it.input.contains("grab", ignoreCase = true)
        }
    }
}