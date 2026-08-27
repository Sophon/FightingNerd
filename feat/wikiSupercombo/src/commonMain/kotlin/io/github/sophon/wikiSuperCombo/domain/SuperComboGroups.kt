package io.github.sophon.wikiSuperCombo.domain

import io.github.sophon.core.util.equalsIgnoreCase
import io.github.sophon.core.wiki.model.Group
import io.github.sophon.core.wiki.model.Move

object SFGroups {
    object Normal: Group {
        override val id: String = "Normal"
        override val predicate: (Move) -> Boolean = {
            it.type == "ground_normal" || it.type == "air_normal"
        }
    }

    object Throw: Group {
        override val id: String = "Throw"
        override val predicate: (Move) -> Boolean = {
            it.type == "throw"
        }
    }

    object Special: Group {
        override val id: String = "Special"
        override val predicate: (Move) -> Boolean = {
            it.type == "special"
        }
    }

    object Drive: Group {
        override val id: String = "Drive"
        override val predicate: (Move) -> Boolean = {
            it.type == "drive"
        }
    }

    object Super: Group {
        override val id: String = "Super"
        override val predicate: (Move) -> Boolean = {
            it.type == "super"
        }
    }

    object Taunt: Group {
        override val id: String = "Taunt"
        override val predicate: (Move) -> Boolean = {
            it.type == "taunt"
        }
    }
}

object AVLGroups {
    object Normal: Group {
        override val id: String = "Normal"
        override val predicate: (Move) -> Boolean = {
            it.type.equalsIgnoreCase("ground_normal")
                    || it.type.equalsIgnoreCase("air_normal")
                    || it.type.equalsIgnoreCase("command normal")
        }
    }

    object Special: Group {
        override val id: String = "Special"
        override val predicate: (Move) -> Boolean = {
            it.type.equalsIgnoreCase("special")
        }
    }

    object Flow: Group {
        override val id: String = "Flow"
        override val predicate: (Move) -> Boolean = {
            it.type.equalsIgnoreCase("flow")
                    || it.type.equalsIgnoreCase("flow stance")
        }
    }

    object Super: Group {
        override val id: String = "Super"
        override val predicate: (Move) -> Boolean = {
            it.type.equalsIgnoreCase("super")
        }
    }
}
