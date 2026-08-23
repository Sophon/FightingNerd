package io.github.sophon.wikiSuperCombo.domain

import io.github.sophon.core.util.equalsIgnoreCase
import io.github.sophon.core.wiki.model.Group
import io.github.sophon.core.wiki.model.Move

object SFGroups {
    object Normal: Group {
        override val id: String = "Normal"
        override val predicate: (Move) -> Boolean = {
            it.sf6Properties?.type == Move.SF6Properties.Type.GROUND_NORMAL
                    || it.sf6Properties?.type == Move.SF6Properties.Type.AIR_NORMAL
        }
    }

    object Throw: Group {
        override val id: String = "Throw"
        override val predicate: (Move) -> Boolean = {
            it.sf6Properties?.type == Move.SF6Properties.Type.THROW
        }
    }

    object Special: Group {
        override val id: String = "Special"
        override val predicate: (Move) -> Boolean = {
            it.sf6Properties?.type == Move.SF6Properties.Type.SPECIAL
        }
    }

    object Drive: Group {
        override val id: String = "Drive"
        override val predicate: (Move) -> Boolean = {
            it.sf6Properties?.type == Move.SF6Properties.Type.DRIVE
        }
    }

    object Super: Group {
        override val id: String = "Super"
        override val predicate: (Move) -> Boolean = {
            it.sf6Properties?.type == Move.SF6Properties.Type.SUPER
        }
    }

    object Taunt: Group {
        override val id: String = "Taunt"
        override val predicate: (Move) -> Boolean = {
            it.sf6Properties?.type == Move.SF6Properties.Type.TAUNT
        }
    }
}

object AVLGroups {
    object Normal: Group {
        override val id: String = "Normal"
        override val predicate: (Move) -> Boolean = {
            it.avlProperties?.type.equalsIgnoreCase("ground_normal")
                    || it.avlProperties?.type.equalsIgnoreCase("air_normal")
                    || it.avlProperties?.type.equalsIgnoreCase("command normal")
        }
    }

    object Special: Group {
        override val id: String = "Special"
        override val predicate: (Move) -> Boolean = {
            it.avlProperties?.type.equalsIgnoreCase("special")
        }
    }

    object Flow: Group {
        override val id: String = "Flow"
        override val predicate: (Move) -> Boolean = {
            it.avlProperties?.type.equalsIgnoreCase("flow")
                    || it.avlProperties?.type.equalsIgnoreCase("flow stance")
        }
    }

    object Super: Group {
        override val id: String = "Super"
        override val predicate: (Move) -> Boolean = {
            it.avlProperties?.type.equalsIgnoreCase("super")
        }
    }
}