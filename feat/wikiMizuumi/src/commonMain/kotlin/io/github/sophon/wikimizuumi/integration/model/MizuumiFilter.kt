package io.github.sophon.wikimizuumi.integration.model

import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Move

object MizuumiFilter {
    object MBTLInvincible: Filter {
        override val predicate: (Move) -> Boolean = { move ->
            move.invulnerability.orEmpty().run {
                isNotEmpty()
                        && isReversal()
                        && isFullyInv()
            } && move.input.isLastArc().not() && move.input.isShieldCounter().not()
        }
    }

    object Uni2Invincible: Filter {
        override val predicate: (Move) -> Boolean = { it.invulnerability?.isNotEmpty() == true }
    }

    object VSAVInvincible: Filter {
        override val predicate: (Move) -> Boolean = { move ->
            move.invulnerability.orEmpty().run {
                isNotEmpty() && isFullBodyInv()
            }
        }
    }


    //region MBTL
    private fun String.isLastArc(): Boolean = this.contains("ABCD", ignoreCase = true)

    private fun String.isShieldCounter(): Boolean = this.startsWith("D~", ignoreCase = true)

    private fun String.isReversal(): Boolean = this.contains("1-")

    private fun String.isFullyInv(): Boolean = this.contains("Full", ignoreCase = true)
    //endregion

    private fun String.isFullBodyInv(): Boolean = this.contains("whole body", ignoreCase = true)
}