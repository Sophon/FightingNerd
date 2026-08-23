package io.github.sophon.dreamcancel.integration

import io.github.sophon.core.util.equalsIgnoreCase
import io.github.sophon.core.util.isSpecial
import io.github.sophon.core.wiki.model.Group
import io.github.sophon.core.wiki.model.Move

object Normal: Group {
    override val id: String = "Normal"
    override val predicate: (Move) -> Boolean = { move ->
        val input = move.input
        val isFar = listOf("fA", "fB", "fC", "fD").any { input.equalsIgnoreCase(it) }
        val isClose = listOf("clA", "clB", "clC", "clD").any { input.equalsIgnoreCase(it) }
        val result = isFar || isClose
        result
    }
}

object Special: Group {
    override val id: String = "Special"
    override val predicate: (Move) -> Boolean = { it.input.isSpecial() }
}

object Super: Group {
    override val id: String = "Super"
    override val predicate: (Move) -> Boolean = { it.input.isSuper() }
}

object KofGroups {
    //+ normal, special, super

    object Rush: Group {
        override val id: String = "Rush"
        override val predicate: (Move) -> Boolean = {
            it.input.startsWith("cAA", ignoreCase = true)
        }
    }

    object Throw: Group {
        override val id: String = "Throw"
        override val predicate: (Move) -> Boolean = {
            it.input.equalsIgnoreCase("c4/6c")
                    || it.input.equalsIgnoreCase("c4/6c")
        }
    }

    object Climax: Group {
        override val id: String = "Climax"
        override val predicate: (Move) -> Boolean = { move ->
            val digitPrefix = move.input.takeWhile { it.isDigit() }
            val isLongInput = (digitPrefix.length >= 6)
            val climaxSuffix = move.input.endsWith("CD", ignoreCase = true)

            isLongInput && climaxSuffix
        }
    }
}

object COTWGroups {
    //+ normal, special, super

    object Combination: Group {
        override val id: String = "Combination"
        override val predicate: (Move) -> Boolean = {
            it.input.contains(">") || it.input.contains("~")
        }
    }

    object Throw: Group {
        override val id: String = "Throw"
        override val predicate: (Move) -> Boolean = {
            it.input.equalsIgnoreCase("AB") || it.input.equalsIgnoreCase("4AB")
        }
    }

    object Rev: Group {
        override val id: String = "Rev"
        override val predicate: (Move) -> Boolean = {
            it.input.equalsIgnoreCase("CD") || it.input.equalsIgnoreCase("jCD")
        }
    }

    object FeintDodge: Group {
        override val id: String = "Feint/Dodge"
        override val predicate: (Move) -> Boolean = {
            it.input.endsWith("+Rev", ignoreCase = true)
        }
    }

    object HiddenGear: Group {
        override val id: String = "Hidden Gear"
        override val predicate: (Move) -> Boolean = { move ->
            val digitPrefix = move.input.takeWhile { it.isDigit() }
            val isLongInput = (digitPrefix.length >= 6)
            val climaxSuffix = move.input.endsWith("Rev", ignoreCase = true)

            isLongInput && climaxSuffix
        }
    }
}


private fun String.isSuper(): Boolean {
    if (this.endsWith("CD", ignoreCase = true)) return false
    if (this.endsWith("Rev", ignoreCase = true)) return false
    val digitPrefix = takeWhile { it.isDigit() }
    val result = (digitPrefix.length >= 6) //long motion = starts with 6+ numbers
    return result
}