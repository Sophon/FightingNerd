package com.example.wikiwavu.domain

import com.example.core.util.cleanHtml
import com.example.wikiwavu.data.MoveDto
import com.example.wikiwavu.domain.model.Move
import com.example.wikiwavu.util.cleanMoveInput

internal fun MoveDto.mapToDomain(
    charName: String,
    movesById: Map<String, MoveDto>,
): Move {
    val move = Move(
        charName = charName,
        id = id.cleanMoveInput(),
        input = formCompleteDataFromParent(movesById) { it.input }
            .orEmpty()
            .cleanMoveInput(),
        level = formCompleteDataFromParent(movesById) { it.target },
        name = name,
        parent = parent,
        damage = formCompleteDataFromParent(movesById) { it.damage },
        startup = getRootStartup(movesById),
        recoveryOnWhiff = recv,
        totalFrames = tot,
        crushes = splitCrush(),
        onBlock = block,
        onHit = hit,
        onCH = ch,
        notes = splitNotes(),
        aliases = parseAliases(),
        image = image,
        videoId = video,
        alt = alt,
    )

    return move.copy(properties = move.getProperties())
}

/**
 * Kazuya's 112 is actually:
 *
 *  - input: ,2
 *  - damage: ,6
 *  - parent: Kazuya-1,1,
 *
 *  So we have to traverse through parents to form the complete string
 */
private fun MoveDto.formCompleteDataFromParent(
    movesById: Map<String, MoveDto>,
    fieldSelector: (MoveDto) -> String?,
): String? {
    var current: MoveDto? = this
    val reverseLevel = mutableListOf<String>()

    while (current != null) {
        fieldSelector(current)?.let { reverseLevel.add(it) }
        current = current.parent?.let { parent -> movesById[parent] }
    }

    return reverseLevel
        .reversed()
        .joinToString("")
        .takeIf { it.isNotEmpty() }
}

/**
 * Similar to the issue above, just with startup
 */
private fun MoveDto.getRootStartup(
    movesById: Map<String, MoveDto>
): String? {
    var current: MoveDto? = this
    var root: MoveDto = this

    // Traverse up to find the topmost parent
    while (current != null) {
        root = current
        current = current.parent?.let { movesById[it] }
    }

    return root.startup
}

private fun MoveDto.splitNotes(): List<String> {
    val finalNotes = notes.orEmpty()
        .trimIndent()
        .cleanHtml()
        .replace("\n\n", "\n")
        .lines()
        .filter { it.isNotEmpty() }
        .map { it.removePrefix("* ").trim() }

    return finalNotes
}

//input: <div class="plainlist">\n* is1~20\n* js25~39\n* fs40~42</div>
private fun MoveDto.splitCrush(): List<String> {
    val finalCrushes = crush.orEmpty()
        .trimIndent()
        .cleanHtml()
        .lines()
        .filterNot { it.isEmpty() }
        .map { it.removePrefix("* ").trim() }

    return finalCrushes
}

private fun Move.getProperties(): Move.Properties {
    return Move.Properties(
        isHeat = notes.any { it.contains("Heat Engager", ignoreCase = true) },
        isPowerCrush = crushes.any { it.contains("pc", ignoreCase = true) },
        isHoming = notes.any { it.contains("Homing", ignoreCase = true) },
        stance = input
            .take(3)
            .takeIf {
                it.all { char -> char.isLetter() } && input.isMotion().not()
            } ?: ""
    )
}

private fun String.isMotion(): Boolean {
    return startsWith("wr")
            || startsWith("ff")
            || startsWith("qcb")
            || startsWith("qcf")
}

private fun MoveDto.parseAliases(): List<String> {
    return alias.orEmpty()
        .cleanHtml()
        .lines()
        .map {
            it
                .removePrefix("* ")
                .trim()
                .cleanMoveInput()
        }
        .filter { it.isNotEmpty() }
}