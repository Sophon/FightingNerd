package io.github.sophon.wikiSuperCombo.domain

import io.github.sophon.core.util.equalsIgnoreCase
import io.github.sophon.core.wiki.model.Group
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wikiSuperCombo.integration.model.AVLProperties
import io.github.sophon.wikiSuperCombo.integration.model.SF6MoveProperties

object SFGroups {
    object Normal: Group {
        override val id: String = "Normal"
        override val predicate: (Move) -> Boolean = {
            (it.gameProperties as? SF6MoveProperties)?.type == SF6MoveProperties.Type.GROUND_NORMAL
                    || (it.gameProperties as? SF6MoveProperties)?.type == SF6MoveProperties.Type.AIR_NORMAL
        }
    }

    object Throw: Group {
        override val id: String = "Throw"
        override val predicate: (Move) -> Boolean = {
            (it.gameProperties as? SF6MoveProperties)?.type == SF6MoveProperties.Type.THROW
        }
    }

    object Special: Group {
        override val id: String = "Special"
        override val predicate: (Move) -> Boolean = {
            (it.gameProperties as? SF6MoveProperties)?.type == SF6MoveProperties.Type.SPECIAL
        }
    }

    object Drive: Group {
        override val id: String = "Drive"
        override val predicate: (Move) -> Boolean = {
            (it.gameProperties as? SF6MoveProperties)?.type == SF6MoveProperties.Type.DRIVE
        }
    }

    object Super: Group {
        override val id: String = "Super"
        override val predicate: (Move) -> Boolean = {
            (it.gameProperties as? SF6MoveProperties)?.type == SF6MoveProperties.Type.SUPER
        }
    }

    object Taunt: Group {
        override val id: String = "Taunt"
        override val predicate: (Move) -> Boolean = {
            (it.gameProperties as? SF6MoveProperties)?.type == SF6MoveProperties.Type.TAUNT
        }
    }
}

object AVLGroups {
    object Normal: Group {
        override val id: String = "Normal"
        override val predicate: (Move) -> Boolean = {
            (it.gameProperties as? AVLProperties)?.type.equalsIgnoreCase("ground_normal")
                    || (it.gameProperties as? AVLProperties)?.type.equalsIgnoreCase("air_normal")
                    || (it.gameProperties as? AVLProperties)?.type.equalsIgnoreCase("command normal")
        }
    }

    object Special: Group {
        override val id: String = "Special"
        override val predicate: (Move) -> Boolean = {
            (it.gameProperties as? AVLProperties)?.type.equalsIgnoreCase("special")
        }
    }

    object Flow: Group {
        override val id: String = "Flow"
        override val predicate: (Move) -> Boolean = {
            (it.gameProperties as? AVLProperties)?.type.equalsIgnoreCase("flow")
                    || (it.gameProperties as? AVLProperties)?.type.equalsIgnoreCase("flow stance")
        }
    }

    object Super: Group {
        override val id: String = "Super"
        override val predicate: (Move) -> Boolean = {
            (it.gameProperties as? AVLProperties)?.type.equalsIgnoreCase("super")
        }
    }
}
