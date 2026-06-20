package io.github.sophon.core.wiki.model

import io.github.sophon.core.util.firstIntOrNull

object CoreFilters {
    data class Startup(
        val from: Int,
        val to: Int,
    ): Filter {
        override val predicate: (Move) -> Boolean = { move ->
            val value = move.startup?.firstIntOrNull()
            (value != null) && (value in from..to)
        }
    }

    data class OnHit(
        val from: Int,
        val to: Int,
    ): Filter {
        override val predicate: (Move) -> Boolean = { move ->
            val value = move.onHit?.firstIntOrNull()
            (value != null) && (value in from..to)
        }
    }

    data class OnBlock(
        val from: Int,
        val to: Int,
    ): Filter {
        override val predicate: (Move) -> Boolean = { move ->
            val value = move.onBlock?.firstIntOrNull()
            (value != null) && (value in from..to)
        }
    }
}
