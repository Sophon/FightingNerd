package io.github.sophon.cornerman.screens.moveList.domain

import io.github.sophon.cornerman.screens.moveList.data.MoveEntity
import io.github.sophon.cornerman.screens.moveList.util.cleanComboLinks
import io.github.sophon.wikiwavu.domain.model.Move

internal fun List<Move>.toDomain(): List<MoveCategory> {
    val categorizedMoves = this
        .groupBy { it.getCategoryName() }
        .map { (categoryName, moves) ->
            MoveCategory(
                name = categoryName,
                moves = moves.map {
                    it.cleanComboLinks().toDomain()
                },
            )
        }
        .sortedBy { it.name.getCategorySortOrder() }

    return categorizedMoves
}

internal fun Move.getCategoryName(): String {
    return when {
        (properties.isHeat == true) -> "Heat"
        properties.stance.isNullOrEmpty().not() -> properties.stance!!.uppercase()
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

private fun Move.toDomain(): UiMove {
    return UiMove(
        id = id,
        input = input,
        mandatoryFields = listOf(
            UiMove.Field("Startup", startup),
            UiMove.Field("OH", onHit),
            UiMove.Field("OB", onBlock),
            UiMove.Field("CH", onCH),
            UiMove.Field("Level", level),
        ),
        optionalFields = listOf(
            UiMove.Field("Damage", damage),
            UiMove.Field("Whiff", recoveryOnWhiff),
        ),
        notes = notes,
        properties = getProperties(),
    )
}

private fun Move.getProperties(): Set<UiMove.Property> = buildSet {
    if (properties.isHeat == true) add(UiMove.Property.HEAT)
    if (properties.isPowerCrush == true) add(UiMove.Property.PC)
    if (properties.isHoming == true) add(UiMove.Property.HOMING)

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

//region Entity
internal fun Move.toEntity(): MoveEntity {
    return MoveEntity(
        charName = charName,
        id = id,
        input = input,
        level = level,
        name = name,
        parent = parent,
        damage = damage,
        startup = startup,
        recoveryOnWhiff = recoveryOnWhiff,
        totalFrames = totalFrames,
        crushes = crushes.joinToString(";"),
        onBlock = onBlock,
        onHit = onHit,
        onCH = onCH,
        notes = notes.joinToString(";"),
        aliases = aliases.joinToString(";"),
        image = image,
        videoId = videoId,
        alt = alt,
        isHeat = properties.isHeat,
        isPowerCrush = properties.isPowerCrush,
        isHoming = properties.isHoming,
        stance = properties.stance,
    )
}

internal fun MoveEntity.toDomain(): Move {
    return Move(
        charName = charName,
        id = id,
        input = input,
        level = level,
        name = name,
        parent = parent,
        damage = damage,
        startup = startup,
        recoveryOnWhiff = recoveryOnWhiff,
        totalFrames = totalFrames,
        crushes = crushes?.split(";").orEmpty(),
        onBlock = onBlock,
        onHit = onHit,
        onCH = onCH,
        notes = notes?.split(";").orEmpty(),
        aliases = aliases?.split(";").orEmpty(),
        image = image,
        videoId = videoId,
        alt = alt,
        properties = Move.Properties(
            isHeat = isHeat,
            isPowerCrush = isPowerCrush,
            isHoming = isHoming,
            stance = stance,
        )
    )
}
//endregion