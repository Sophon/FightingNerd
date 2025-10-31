package com.example.cornerman.screens.moveList.domain

import com.example.cornerman.screens.moveList.data.MoveEntity
import com.example.cornerman.screens.moveList.util.cleanComboLinks
import com.example.wikiwavu.domain.model.Move

internal fun List<Move>.toDomain(): List<MoveCategory> {
    val categorizedMoves = this
        .groupBy { it.getCategoryName() }
        .map { (categoryName, moves) ->
            MoveCategory(name = categoryName, moves = moves.map { it.cleanComboLinks() })
        }
        .sortedBy { it.name.getCategorySortOrder() }

    return categorizedMoves
}

internal fun Move.getCategoryName(): String {
    return when {
        (properties.isHeat == true) -> "Heat"
        properties.stance.isNullOrEmpty().not() -> properties.stance!!
        (isDirectional() != null) -> isDirectional()!!
        isMotion() -> "Motion Input"
        isCrouch() -> "Crouch"
        isWS() -> "WS"
        isCD() -> "CD (Crouch Dash)"
        isBT() -> "BT (Back Turned)"
        isThrow() -> "Throws"
        isNeutralInput() -> "n"
        else -> "Others"
    }
}

internal fun String.getCategorySortOrder(): Int {
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

internal fun Move.isDirectional(): String? {
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

internal fun Move.isMotion(): Boolean {
    return input.startsWith("wr")
            || input.startsWith("ff")
            || input.startsWith("qcb")
            || input.startsWith("qcf")
}

internal fun Move.isCrouch(): Boolean = input.startsWith("fc")

internal fun Move.isWS(): Boolean = input.startsWith("ws")

internal fun Move.isCD(): Boolean = input.startsWith("cd.")

internal fun Move.isBT(): Boolean = input.startsWith("bt")

internal fun Move.isThrow(): Boolean = notes.any { it.contains("throw", ignoreCase = true) }

internal fun Move.isNeutralInput(): Boolean = (input.firstOrNull()?.isDigit() == true)

//TODO: this should prob be a property of Move
internal fun Move.isStance(): String? {
    if (input.startsWith("FC.")) return null
    if (input.contains(".").not()) return null

    val prefix = input.substringBefore(".")

    // Check if it's all uppercase letters (stances are uppercase like CD, JGS, BT, JGR, FC)
    return if (prefix.all { it.isUpperCase() || it.isDigit() }) {
        prefix
    } else {
        null
    }
}

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