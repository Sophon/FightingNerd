package io.github.sophon.wiki.model

import io.github.sophon.core.util.equalsIgnoreCase

object GGSTGroups {
    object Normal: Group {
        override val id: String = "Normal"
        override val predicate: (Move) -> Boolean = { it.type.isNormal() }
    }

    object Special: Group {
        override val id: String = "Special"
        override val predicate: (Move) -> Boolean = { it.type.isSpecial() }
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
            it.type.equalsIgnoreCase("super")
        }
    }
}

object BBCFGroups {
    object Normal: Group {
        override val id: String = "Normal"
        override val predicate: (Move) -> Boolean = { it.type.isNormal() }
    }

    object Special: Group {
        override val id: String = "Special"
        override val predicate: (Move) -> Boolean = { it.type.isSpecial() }
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
            it.type.equalsIgnoreCase("drive")
        }
    }

    object Exceed: Group {
        override val id: String = "Exceed"
        override val predicate: (Move) -> Boolean = {
            it.type.equalsIgnoreCase("exceed accel")
        }
    }

    object Astral: Group {
        override val id: String = "Astral"
        override val predicate: (Move) -> Boolean = {
            it.type.equalsIgnoreCase("astral")
        }
    }
}

object BBTagGroups {
    object Normal: Group {
        override val id: String = "Normal"
        override val predicate: (Move) -> Boolean = { it.type.isNormal() }
    }

    object Universal: Group {
        override val id: String = "Universal Mechanics"
        override val predicate: (Move) -> Boolean = {
            it.type.equalsIgnoreCase("other")
        }
    }

    // drive (Noel's Chain Revolver) and item (Teddie's items) are character-specific skills
    object Skill: Group {
        override val id: String = "Skills"
        override val predicate: (Move) -> Boolean = {
            it.type.isSpecial()
                    || it.type.equalsIgnoreCase("drive")
                    || it.type.equalsIgnoreCase("item")
        }
    }

    object ExtraSkill: Group {
        override val id: String = "Extra Skills"
        override val predicate: (Move) -> Boolean = {
            it.type.equalsIgnoreCase("ex")
        }
    }

    object PartnerSkill: Group {
        override val id: String = "Partner Skills"
        override val predicate: (Move) -> Boolean = {
            it.type.equalsIgnoreCase("assist")
        }
    }

    object DistortionSkill: Group {
        override val id: String = "Distortion Skills"
        override val predicate: (Move) -> Boolean = {
            it.type.equalsIgnoreCase("super") && it.isDistortionSkillDuo().not()
        }
    }

    object DistortionSkillDuo: Group {
        override val id: String = "Distortion Skill Duo"
        override val predicate: (Move) -> Boolean = {
            it.type.equalsIgnoreCase("super") && it.isDistortionSkillDuo()
        }
    }

    object AstralHeat: Group {
        override val id: String = "Astral Heat"
        override val predicate: (Move) -> Boolean = {
            it.type.equalsIgnoreCase("astral")
        }
    }
}

object DBFZGroups {
    object Normal: Group {
        override val id: String = "Normal"
        override val predicate: (Move) -> Boolean = { it.type.isNormal() }
    }

    object Special: Group {
        override val id: String = "Special"
        override val predicate: (Move) -> Boolean = { it.type.isSpecial() }
    }

    object Assist: Group {
        override val id: String = "Assist"
        override val predicate: (Move) -> Boolean = {
            it.type.equalsIgnoreCase("assist")
        }
    }

    object Super: Group {
        override val id: String = "Super"
        override val predicate: (Move) -> Boolean = {
            it.type.equalsIgnoreCase("super")
        }
    }
}

object GBVSRGroups {
    object Normal: Group {
        override val id: String = "Normal"
        override val predicate: (Move) -> Boolean = { it.type.isNormal() }
    }

    object Special: Group {
        override val id: String = "Special"
        override val predicate: (Move) -> Boolean = { it.type.isSpecial() }
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
            it.type.equalsIgnoreCase("unique")
        }
    }

    object Super: Group {
        override val id: String = "Skybound"
        override val predicate: (Move) -> Boolean = {
            it.type.equalsIgnoreCase("super")
        }
    }
}

object MTFSGroups {
    object Normal: Group {
        override val id: String = "Normal"
        override val predicate: (Move) -> Boolean = { it.type.isNormal() }
    }

    object Special: Group {
        override val id: String = "Special"
        override val predicate: (Move) -> Boolean = { it.type.isSpecial() }
    }

    object Assist: Group {
        override val id: String = "Assist"
        override val predicate: (Move) -> Boolean = {
            it.type.equalsIgnoreCase("assist")
        }
    }

    object Super: Group {
        override val id: String = "Ultimate"
        override val predicate: (Move) -> Boolean = {
            it.type.equalsIgnoreCase("super")
        }
    }

    object Unique: Group {
        override val id: String = "Unique"
        override val predicate: (Move) -> Boolean = {
            it.type.equalsIgnoreCase("unique")
        }
    }

    object Tokon: Group {
        override val id: String = "Tokon"
        override val predicate: (Move) -> Boolean = {
            it.type.equalsIgnoreCase("tokon")
        }
    }
}


private fun String?.isNormal(): Boolean {
    return this.equalsIgnoreCase("normal")
}

private fun String?.isSpecial(): Boolean {
    return this.equalsIgnoreCase("special")
}

private fun Move.isDistortionSkillDuo(): Boolean {
    return input.equalsIgnoreCase("Distortion Skill Duo")
}
