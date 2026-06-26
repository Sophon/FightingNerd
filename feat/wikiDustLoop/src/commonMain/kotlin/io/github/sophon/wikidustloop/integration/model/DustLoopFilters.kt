package io.github.sophon.wikidustloop.integration.model

import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Move


object BBFilters {
    object Invincible: Filter {
        override val name: String = "Invincible"
        override val predicate: (Move) -> Boolean = { move ->
            val isFullyFromFrameOne = move.invulnerability.orEmpty()
                .split(",")
                .any {
                    it.startsWith("1~") && it.endsWith("All", ignoreCase = true)
                }
            val isOverdrive = move.input.contains("a+b+c+d", ignoreCase = true)
                    || move.input.endsWith("od", ignoreCase = true)
            val isCounterAssault = move.input.contains("6a+b", ignoreCase = true)
            val costsMeter = move.bbProperties?.type.orEmpty().contains("super", ignoreCase = true)
                    || move.input.endsWith("od", ignoreCase = true)
                    || move.bbProperties?.type.orEmpty().contains("astral", ignoreCase = true)
                    || move.input.endsWith("special", ignoreCase = true)
            val isJump = move.input.startsWith("j.") || move.input.startsWith("d.")
            val attack = move.input.endsWith("attack")

            isFullyFromFrameOne
                    && isCounterAssault.not() && isOverdrive.not()
                    && costsMeter.not()
                    && isJump.not() && attack.not()
        }
    }

    fun getAllBinaryFilters(): Set<Filter> {
        return setOf(
            Invincible,
        )
    }
}

object GGFilters {
    object Invincible: Filter {
        override val name: String = "Invincible"
        override val predicate: (Move) -> Boolean = { move ->
            move.invulnerability?.isNotBlank() == true
        }
    }

    fun getAllBinaryFilters(): Set<Filter> {
        return setOf(
            Invincible,
        )
    }
}
