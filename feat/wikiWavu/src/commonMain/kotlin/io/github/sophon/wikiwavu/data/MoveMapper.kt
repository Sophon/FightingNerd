package io.github.sophon.wikiwavu.data

import io.github.sophon.core.util.cleanHtml
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.wikiwavu.util.cleanMoveInput

internal fun MoveListResponseDto.toDomain(charName: String): List<Move> {
    val downloadedMoves = extractMoveDto()
    val movesById = downloadedMoves.associateBy { it.id }
    val moveList = downloadedMoves.map { it.mapToDomain(charName, movesById) }
    return moveList
}

internal fun MoveListResponseDto.extractMoveDto(): List<MoveDto> {
    return cargoQuery.map { it.title }
}

internal fun MoveDto.mapToDomain(
    charName: String,
    movesById: Map<String, MoveDto>,
): Move {
    val cleanedCrushes = splitCrush()
    val unifiedNotes = splitNotes() + cleanedCrushes
    val fullInput = formCompleteDataFromParent(movesById) { it.input }
        .orEmpty()
        .cleanMoveInput()

    val move = Move(
        charName = charName,
        id = id.cleanMoveInput(),
        input = fullInput,
        name = name,
        damage = formCompleteDataFromParent(movesById) { it.damage },
        startup = getRootStartup(movesById),
        recovery = recv,
        onBlock = block,
        onHit = hit,
        onCH = ch,
        notes = splitNotes() + cleanedCrushes,
        aliases = parseAliases(),
        videoId = video,
        t8Properties = formProperties(
            notes = unifiedNotes,
            crushes = cleanedCrushes,
            level = formCompleteDataFromParent(movesById) { it.target },
            input = fullInput,
        )
    )

    return move
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

private fun formProperties(
    level: String?,
    notes: List<String>,
    crushes: List<String>,
    input: String,
): Move.T8Properties {
    return Move.T8Properties(
        level = level,
        isHeat = notes.any { it.contains("Heat Engager", ignoreCase = true) },
        isPowerCrush = crushes.any { it.contains("pc", ignoreCase = true) },
        isHoming = notes.any { it.contains("Homing", ignoreCase = true) },
        stance = input.isStance(),
    )
}

private fun String.isStance(): String {
    return take(3).takeIf {
        length >= 4
                && it.all { char -> char.isLetter() }
                && !startsWith("wr") && !startsWith("ff") && !startsWith("qcb") && !startsWith("qcf") && !startsWith("fc")
                && drop(3).any { char -> char.isDigit() }
    } ?: ""
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