package io.github.sophon.wikimizuumi.integration.model

import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Move

object MBFilters {
    object Invincible: Filter {
        override val predicate: (Move) -> Boolean = { move ->
            move.invulnerability.orEmpty().run {
                isNotEmpty()
                        && isReversal()
                        && isFullyInv()
            } && move.input.isLastArc().not() && move.input.isShieldCounter().not()
        }
    }

    fun getAllBinaryFilters(): Set<Filter> {
        return setOf(
            Invincible,
        )
    }


    private fun String.isLastArc(): Boolean = this.contains("ABCD", ignoreCase = true)

    private fun String.isShieldCounter(): Boolean = this.startsWith("D~", ignoreCase = true)

    private fun String.isReversal(): Boolean = this.contains("1-")

    private fun String.isFullyInv(): Boolean = this.contains("Full", ignoreCase = true)
}

object UniFilters {
    object Invincible: Filter {
        override val predicate: (Move) -> Boolean = { it.invulnerability?.isNotEmpty() == true }
    }

    fun getAllBinaryFilters(): Set<Filter> {
        return setOf(
            Invincible,
        )
    }
}

object VSAVFilters {
    object Invincible: Filter {
        override val predicate: (Move) -> Boolean = { move ->
            move.invulnerability.orEmpty().run {
                isNotEmpty() && isFullBodyInv()
            }
        }
    }

    fun getAllBinaryFilters(): Set<Filter> {
        return setOf(
            Invincible,
        )
    }


    private fun String.isFullBodyInv(): Boolean = this.contains("whole body", ignoreCase = true)
}
