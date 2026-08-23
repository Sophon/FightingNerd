package io.github.sophon.wikidustloop.integration.model

import io.github.sophon.core.util.equalsIgnoreCase
import io.github.sophon.core.wiki.model.Group
import io.github.sophon.core.wiki.model.Move

object GGSTGroups {
    object Normal: Group {
        override val id: String = "Normal"
        override val predicate: (Move) -> Boolean = { it.ggstProperties?.type.isNormal() }
    }

    object Special: Group {
        override val id: String = "Special"
        override val predicate: (Move) -> Boolean = { it.ggstProperties?.type.isSpecial() }
    }

    object Universal: Group {
        override val id: String = "Universal"
        override val predicate: (Move) -> Boolean = {
            it.input.equalsIgnoreCase("6d/4d")
                    || it.input.equalsIgnoreCase("j6d/j4d")
        }
    }

    object Super: Group {
        override val id: String = "Overdrive"
        override val predicate: (Move) -> Boolean = {
            it.ggstProperties?.type.equalsIgnoreCase("super")
        }
    }
}

object BBCFGroups {
    object Normal: Group {
        override val id: String = "Normal"
        override val predicate: (Move) -> Boolean = { it.bbProperties?.type.isNormal() }
    }

    object Special: Group {
        override val id: String = "Special"
        override val predicate: (Move) -> Boolean = { it.bbProperties?.type.isSpecial() }
    }

    object Universal: Group {
        override val id: String = "Universal"
        override val predicate: (Move) -> Boolean = {
            val isThrow = it.input.equalsIgnoreCase("5B+C")
                    || it.input.equalsIgnoreCase("4B+C")
                    || it.input.equalsIgnoreCase("jB+C")
            val isCA = it.input.equalsIgnoreCase("6A+B")
            val isCT = it.input.equalsIgnoreCase("5A+B")
            val isTaunt = it.input.equalsIgnoreCase("ap")

            isThrow || isCA || isCT || isTaunt
        }
    }

    object Super: Group {
        override val id: String = "DD"
        override val predicate: (Move) -> Boolean = {
            it.bbProperties?.type.equalsIgnoreCase("drive")
        }
    }

    object Exceed: Group {
        override val id: String = "Exceed"
        override val predicate: (Move) -> Boolean = {
            it.bbProperties?.type.equalsIgnoreCase("exceed accel")
        }
    }

    object Astral: Group {
        override val id: String = "Astral"
        override val predicate: (Move) -> Boolean = {
            it.bbProperties?.type.equalsIgnoreCase("astral")
        }
    }
}

object DBFZGroups {
    object Normal: Group {
        override val id: String = "Normal"
        override val predicate: (Move) -> Boolean = { it.dbfzProperties?.type.isNormal() }
    }

    object Special: Group {
        override val id: String = "Special"
        override val predicate: (Move) -> Boolean = { it.dbfzProperties?.type.isSpecial() }
    }

    object Assist: Group {
        override val id: String = "Assist"
        override val predicate: (Move) -> Boolean = {
            it.dbfzProperties?.type.equalsIgnoreCase("assist")
        }
    }

    object Super: Group {
        override val id: String = "Super"
        override val predicate: (Move) -> Boolean = {
            it.dbfzProperties?.type.equalsIgnoreCase("super")
        }
    }
}

object GBVSRGroups {
    object Normal: Group {
        override val id: String = "Normal"
        override val predicate: (Move) -> Boolean = { it.gbvsrProperties?.type.isNormal() }
    }

    object Special: Group {
        override val id: String = "Special"
        override val predicate: (Move) -> Boolean = { it.gbvsrProperties?.type.isSpecial() }
    }

    object Universal: Group {
        override val id: String = "Universal"
        override val predicate: (Move) -> Boolean = { move ->
            val input = move.input
            val isThrow = input.equalsIgnoreCase("LU")
                    || input.equalsIgnoreCase("LM")
                    || input.equalsIgnoreCase("jLU")
                    || input.equalsIgnoreCase("jLM")
            val isRaging = input.equalsIgnoreCase("MH")
                    || input.equalsIgnoreCase("MH~MH")
            val isBraveCounter = input.equalsIgnoreCase("BC")

            isThrow || isRaging || isBraveCounter
        }
    }

    object Unique: Group {
        override val id: String = "Unique"
        override val predicate: (Move) -> Boolean = {
            it.gbvsrProperties?.type.equalsIgnoreCase("unique")
        }
    }

    object Super: Group {
        override val id: String = "Skybound"
        override val predicate: (Move) -> Boolean = {
            it.gbvsrProperties?.type.equalsIgnoreCase("super")
        }
    }
}

object MTFSGroups {
    object Normal: Group {
        override val id: String = "Normal"
        override val predicate: (Move) -> Boolean = { it.mtfsProperties?.type.isNormal() }
    }

    object Special: Group {
        override val id: String = "Special"
        override val predicate: (Move) -> Boolean = { it.mtfsProperties?.type.isSpecial() }
    }

    object Assist: Group {
        override val id: String = "Assist"
        override val predicate: (Move) -> Boolean = {
            it.mtfsProperties?.type.equalsIgnoreCase("assist")
        }
    }

    object Super: Group {
        override val id: String = "Ultimate"
        override val predicate: (Move) -> Boolean = {
            it.mtfsProperties?.type.equalsIgnoreCase("super")
        }
    }

    object Unique: Group {
        override val id: String = "Unique"
        override val predicate: (Move) -> Boolean = {
            it.mtfsProperties?.type.equalsIgnoreCase("unique")
        }
    }

    object Tokon: Group {
        override val id: String = "Tokon"
        override val predicate: (Move) -> Boolean = {
            it.mtfsProperties?.type.equalsIgnoreCase("tokon")
        }
    }
}


private fun String?.isNormal(): Boolean {
    return this.equalsIgnoreCase("normal")
}

private fun String?.isSpecial(): Boolean {
    return this.equalsIgnoreCase("special")
}