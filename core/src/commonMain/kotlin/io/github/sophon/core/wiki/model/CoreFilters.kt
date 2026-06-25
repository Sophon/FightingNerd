package io.github.sophon.core.wiki.model

import io.github.sophon.core.util.firstIntOrNull

object CoreFilters {
    data class Startup(
        val from: Int?,
        val to: Int?,
    ): Filter {
        override val name: String = "Startup"
        override val predicate: (Move) -> Boolean = { move ->
            val value = move.startup?.firstIntOrNull()
            if (value == null) {
                false
            } else {
                (from == null || value >= from) && (to == null || value <= to)
            }
        }
    }

    data class OnHit(
        val from: Int?,
        val to: Int?,
    ): Filter {
        override val name: String = "OnHit"
        override val predicate: (Move) -> Boolean = { move ->
            val value = move.onHit?.firstIntOrNull()
            if (value == null) {
                false
            } else {
                (from == null || value >= from) && (to == null || value <= to)
            }
        }
    }

    data class OnBlock(
        val from: Int?,
        val to: Int?,
    ): Filter {
        override val name: String = "OnBlock"
        override val predicate: (Move) -> Boolean = { move ->
            val value = move.onBlock?.firstIntOrNull()
            if (value == null) {
                false
            } else {
                (from == null || value >= from) && (to == null || value <= to)
            }
        }
    }
}
