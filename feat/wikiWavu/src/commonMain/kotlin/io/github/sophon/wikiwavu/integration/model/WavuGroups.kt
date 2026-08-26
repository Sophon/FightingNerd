package io.github.sophon.wikiwavu.integration.model

import io.github.sophon.core.util.equalsIgnoreCase
import io.github.sophon.core.wiki.model.Group
import io.github.sophon.core.wiki.model.Move

object TekkenGroups {
    object Heat: Group {
        override val id: String = "Heat"
        override val predicate: (Move) -> Boolean = {
            it.input.contains("H.", ignoreCase = true)
        }
    }

    object Neutral: Group {
        override val id: String = "n"
        override val predicate: (Move) -> Boolean = { it.input.startsWithButton() }
    }

    object Forward: Group {
        override val id: String = "f"
        override val predicate: (Move) -> Boolean = { it.input.startsWithBeforeNumber("f") }
    }

    object DownForward: Group {
        override val id: String = "df"
        override val predicate: (Move) -> Boolean = { it.input.startsWithBeforeNumber("df") }
    }

    object Down: Group {
        override val id: String = "d"
        override val predicate: (Move) -> Boolean = { it.input.startsWithBeforeNumber("d") }
    }

    object DownBack: Group {
        override val id: String = "db"
        override val predicate: (Move) -> Boolean = { it.input.startsWithBeforeNumber("db") }
    }

    object Back: Group {
        override val id: String = "b"
        override val predicate: (Move) -> Boolean = { it.input.startsWithBeforeNumber("b") }
    }

    object Up: Group {
        override val id: String = "u"
        override val predicate: (Move) -> Boolean = { it.input.startsWithBeforeNumber("u") }
    }

    object UpBack: Group {
        override val id: String = "ub"
        override val predicate: (Move) -> Boolean = { it.input.startsWithBeforeNumber("ub") }
    }

    object Motion: Group {
        override val id: String = "Motion input"
        override val predicate: (Move) -> Boolean = { move ->
            val input = move.input
            input.startsWithBeforeNumber("wr")
                    || input.startsWithBeforeNumber("ff")
                    || input.startsWithBeforeNumber("qcf")
                    || input.startsWithBeforeNumber("qcb")
                    || input.startsWithBeforeNumber("hcf")
                    || input.startsWithBeforeNumber("hcb")
                    || input.startsWithBeforeNumber("bb")
        }
    }

    object Crouch: Group {
        override val id: String = "Crouch"
        override val predicate: (Move) -> Boolean = { move ->
            move.input.startsWith("hFC", ignoreCase = true)
                    || move.input.startsWith("FC", ignoreCase = true)
        }
    }

    object WS: Group {
        override val id: String = "WS"
        override val predicate: (Move) -> Boolean = { it.input.startsWithBeforeNumber("ws") }
    }

    object SS: Group {
        override val id: String = "SS"
        override val predicate: (Move) -> Boolean = { it.input.startsWithBeforeNumber("ss")}
    }

    object BT: Group {
        override val id: String = "BT (back-turned)"
        override val predicate: (Move) -> Boolean = { it.input.startsWithBeforeNumber("BT")}
    }

    data class Stance(override val id: String): Group {
        override val predicate: (Move) -> Boolean = {
            (it.gameProperties as? T8Properties)?.stance.equalsIgnoreCase(id)
        }
    }

    object Throws: Group {
        override val id: String = "Throws"
        override val predicate: (Move) -> Boolean = { it.isThrow }
    }
}


private fun String.startsWithButton(): Boolean {
    return startsWith("1")
            || startsWith("2")
            || startsWith("3")
            || startsWith("4")
}

// starts with a prefix and followed by a number
private fun String.startsWithBeforeNumber(prefix: String): Boolean {
    val matches = startsWith(prefix, ignoreCase = true)
            && getOrNull(prefix.length)?.isDigit() == true
    return matches
}
