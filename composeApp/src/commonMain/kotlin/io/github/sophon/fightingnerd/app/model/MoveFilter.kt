package io.github.sophon.fightingnerd.app.model

import io.github.sophon.core.util.firstIntOrNull

sealed interface MoveFilter {
    fun matches(move: Move): Boolean

    /**
     * One of the game's own filters - only the wiki can evaluate it, so it's matched by [Move.filterNameSet].
     */
    data class Named(val name: String): MoveFilter {
        override fun matches(move: Move): Boolean = (name in move.filterNameSet)
    }

    data class Startup(
        val from: Int?,
        val to: Int?,
    ): MoveFilter {
        override fun matches(move: Move): Boolean = move.startup.isWithin(from, to)
    }

    data class OnHit(
        val from: Int?,
        val to: Int?,
    ): MoveFilter {
        override fun matches(move: Move): Boolean = move.onHit.isWithin(from, to)
    }

    data class OnBlock(
        val from: Int?,
        val to: Int?,
    ): MoveFilter {
        override fun matches(move: Move): Boolean = move.onBlock.isWithin(from, to)
    }
}


private fun String?.isWithin(from: Int?, to: Int?): Boolean {
    val value = this?.firstIntOrNull()
    val isWithin = if (value == null) {
        false
    } else {
        (from == null || value >= from) && (to == null || value <= to)
    }
    return isWithin
}
