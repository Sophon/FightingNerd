package io.github.sophon.cornerman.screens.moveList.domain

import io.github.sophon.core.util.orDash
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.cornerman.screens.moveList.util.cleanComboLinks

internal fun List<Move>.toDomain(): List<MoveCategory> {
    val categorizedMoves = this
        .groupBy { it.getCategoryName() }
        .map { (categoryName, moves) ->
            MoveCategory(
                name = categoryName,
                moves = moves.map {
                    it.cleanComboLinks().toUi()
                },
            )
        }
        .sortedBy { it.name.getCategorySortOrder() }

    return categorizedMoves
}

internal fun Move.getCategoryName(): String {
    return when {
        (type() != null) -> type()!!

        (t8Properties?.isHeat == true) -> "Heat"
        t8Properties?.stance.isNullOrEmpty().not() -> t8Properties?.stance!!.uppercase()
        (isDirectional() != null) -> isDirectional()!!
        isMotion() -> "Motion Input"
        input.startsWith("fc", ignoreCase = true) -> "Crouch"
        input.startsWith("ws", ignoreCase = true) -> "WS"
        input.startsWith("cd.", ignoreCase = true) -> "CD (Crouch Dash)"
        input.startsWith("bt", ignoreCase = true) -> "BT (Back Turned)"
        isThrow() -> "Throws"
        isNeutralInput() -> "n"
        else -> "Others"
    }
}

private fun String.getCategorySortOrder(): Int {
    return when (this) {
        "Heat" -> 1
        "n" -> 2
        "f" -> 3
        "df" -> 4
        "d" -> 5
        "db" -> 6
        "b" -> 7
        "u" -> 8
        "Motion Input" -> 9
        "Crouch" -> 10
        "WS" -> 11
        else -> 14  // All stances and unknown categories go here for now
    }
}

private fun Move.isDirectional(): String? {
    return when {
        input.startsWith("df") -> "df"
        input.startsWith("db") -> "db"
        input.startsWith("f") -> "f"
        input.startsWith("d") -> "d"
        input.startsWith("b") -> "b"
        input.startsWith("u") -> "u"
        else -> null
    }
}

private fun Move.isMotion(): Boolean {
    return input.startsWith("wr")
            || input.startsWith("ff")
            || input.startsWith("qcb")
            || input.startsWith("qcf")
}

private fun Move.isThrow(): Boolean = notes.any { it.contains("throw", ignoreCase = true) }

private fun Move.isNeutralInput(): Boolean = (input.firstOrNull()?.isDigit() == true)

private fun Move.toUi(): UiMove {
    return UiMove(
        id = id,
        input = input,
        mandatoryFields = buildList {
            add(UiMove.Field("Startup", startup))
            add(UiMove.Field("OH", onHit))
            add(UiMove.Field("OB", onBlock))
            onCH?.let { add(UiMove.Field("CH", it)) }
            sf6Properties?.active?.let { add(UiMove.Field("Active", it)) }
            add(UiMove.Field("Level", t8Properties?.level ?: sf6Properties?.guard))
        },
        optionalFields = buildList {
            add(UiMove.Field("Damage", damage))
            add(UiMove.Field("Whiff", recovery))
            add(UiMove.Field("Invul", sf6Properties?.invulnerability))

            add(UiMove.Field("Cancel", sf6Properties?.cancel))
            add(UiMove.Field("Range", sf6Properties?.attackRange))
            add(UiMove.Field("Proj spd", sf6Properties?.projectileSpeed))
        },
        details = getDetails(),
        notes = notes,
        properties = getProperties(),
    )
}

private fun Move.getProperties(): Set<UiMove.Property> = buildSet {
    if (t8Properties?.isHeat == true) add(UiMove.Property.HEAT)
    if (t8Properties?.isPowerCrush == true) add(UiMove.Property.PC)
    if (t8Properties?.isHoming == true) add(UiMove.Property.HOMING)

    if (sf6Properties?.invulnerability != null) add(UiMove.Property.INVULNERABLE)
    if (sf6Properties?.armor != null) add(UiMove.Property.ARMOR)
    if (sf6Properties?.projectileSpeed != null) add(UiMove.Property.PROJECTILE)

    notes.forEach { note ->
        when {
            note.contains("Tornado", ignoreCase = true) -> add(UiMove.Property.TORNADO)
            note.contains("Throw", ignoreCase = true) -> add(UiMove.Property.THROW)
            note.contains("Floor break", ignoreCase = true) -> add(UiMove.Property.FLOOR_BREAK)
            note.contains("Balcony break", ignoreCase = true) ||
                    note.contains("Wall break", ignoreCase = true) -> add(UiMove.Property.WALL_BREAK)
            note.contains("cs", ignoreCase = true) -> add(UiMove.Property.HIGH_CRUSH)
            note.contains("js", ignoreCase = true) -> add(UiMove.Property.LOW_CRUSH)
        }
    }
}

private fun Move.type(): String? {
    return sf6Properties?.type
        ?.split("_")
        ?.joinToString(" ") {
            it.replaceFirstChar(Char::uppercase)
        }
}

private fun Move.getDetails(): List<String> = buildList {
    sf6Properties?.let { properties ->
        if (properties.DROH != null || properties.DROB != null) {
            add("DR (OH | OB): ${properties.DROH.orDash()} | ${properties.DROB.orDash()}")
        }

        if (properties.DRcOH != null || properties.DRcOB != null) {
            add("DRc (OH | OB): ${properties.DRcOH.orDash()} | ${properties.DRcOB.orDash()}")
        }

        if (properties.driveDmgOnHit != null || properties.driveDmgOnBlock != null) {
            add("Drive damage (OH | OB): ${properties.driveDmgOnHit.orDash()} | ${properties.driveDmgOnBlock.orDash()}")
        }

        if (properties.driveGain != null) {
            add("Drive gain: ${properties.driveGain}")
        }

        if (properties.superGainOnHit != null || properties.superGainOnBlock != null) {
            add("SUP gain (OH | OB): ${properties.superGainOnHit.orDash()} | ${properties.superGainOnBlock.orDash()}")
        }

        add("JUG start: ${properties.jugStart.orDash()}")
        add("JUG limit: ${properties.jugLimit.orDash()}")
        add("JUG increase: ${properties.jugIncrease.orDash()}")
    }
}