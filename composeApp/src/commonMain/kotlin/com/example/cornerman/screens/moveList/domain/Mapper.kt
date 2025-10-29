package com.example.cornerman.screens.moveList.domain

import com.example.cornerman.screens.moveList.util.cleanComboLinks
import com.example.wikiwavu.domain.model.CharacterMoveList
import com.example.wikiwavu.domain.model.Move

internal fun CharacterMoveList.toDomain(): List<MoveCategory> {
    val categorizedMoves = moveList
        .groupBy { it.getCategoryName() }
        .map { (categoryName, moves) ->
            MoveCategory(name = categoryName, moves = moves.map { it.cleanComboLinks() })
        }
        .sortedBy { it.name.getCategorySortOrder() }

    return categorizedMoves
}

internal fun Move.getCategoryName(): String {
    return when {
        isHeat -> "Heat"
        (isStance() != null) -> isStance()!!
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

internal fun Move.isCrouch(): Boolean = input.startsWith("FC.")

internal fun Move.isWS(): Boolean = input.startsWith("ws")

internal fun Move.isCD(): Boolean = input.startsWith("CD.")

internal fun Move.isBT(): Boolean = input.startsWith("BT.")

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