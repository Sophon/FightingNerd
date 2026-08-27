package io.github.sophon.wikimizuumi.integration.model

import io.github.sophon.core.util.equalsIgnoreCase
import io.github.sophon.core.util.isSpecial
import io.github.sophon.core.wiki.model.Group
import io.github.sophon.core.wiki.model.Move

object Normal: Group {
    override val id: String = "Normal"
    override val predicate: (Move) -> Boolean = { it.input.isNormal() }
}

object Special: Group {
    override val id: String = "Special"
    override val predicate: (Move) -> Boolean = { it.input.isSpecial() }
}

object MBGroups {
    object Universal: Group {
        override val id: String = "Universal"
        override val predicate: (Move) -> Boolean = { move ->
            val input = move.input
            val isThrow = input.endsWith("AD", ignoreCase = true)
            val isShieldCounter = input.endsWith("D~A", ignoreCase = true)
                    || input.endsWith("D~B", ignoreCase = true)
                    || input.endsWith("D~B+C", ignoreCase = true)
            val isRapid = input.equalsIgnoreCase("rapid1")
                    || input.equalsIgnoreCase("3c")

            isThrow || isShieldCounter || isRapid
        }
    }

    object Super: Group {
        override val id: String = "Super"
        override val predicate: (Move) -> Boolean = { move ->
            move.input.equalsIgnoreCase("236BC")
                    || move.input.equalsIgnoreCase("ABCD")
        }
    }
}

object UniGroups {
    object Normal: Group {
        override val id: String = "Normal"
        override val predicate: (Move) -> Boolean = {
            it.type.equalsIgnoreCase("normal")
        }
    }

    object Universal: Group {
        override val id: String = "Universal"
        override val predicate: (Move) -> Boolean = { move ->
            val input = move.input
            val isForce = input.endsWith("B+C", ignoreCase = true)
            val isThrow = input.equalsIgnoreCase("A+D")
            val isSteerEnder = input.equalsIgnoreCase("A+B")
            val isGuardThrust = input.equalsIgnoreCase("A+B+C")

            isForce || isThrow || isSteerEnder || isGuardThrust
        }
    }

    object Special: Group {
        override val id: String = "Special"
        override val predicate: (Move) -> Boolean = {
            it.type.equalsIgnoreCase("special")
        }
    }

    object Super: Group {
        override val id: String = "Super"
        override val predicate: (Move) -> Boolean = {
            it.type.equalsIgnoreCase("super")
        }
    }
}

//VSAV only has normal and specials


private fun String.isNormal(): Boolean {
    if (length < 2) return false
    val hasNormalPrefix = this[0] in "52j6"
    val result = hasNormalPrefix && this[1].isLetter()
    return result
}
