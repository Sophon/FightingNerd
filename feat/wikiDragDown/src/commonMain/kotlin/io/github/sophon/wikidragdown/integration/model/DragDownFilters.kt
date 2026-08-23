package io.github.sophon.wikidragdown.integration.model

import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Move

object DragDownFilters {
    object Specials: Filter {
        override val name: String = "Specials"
        override val predicate: (Move) -> Boolean = { move ->
            move.input.contains("special", ignoreCase = true)
        }
    }


    fun getAllBinaryFilters(): Set<Filter> {
        return setOf(
            Specials,
        )
    }
}