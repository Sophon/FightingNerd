package io.github.sophon.wikiwavu.data

import io.github.sophon.core.util.cleanHtml
import io.github.sophon.core.util.urlEncode
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.core.wiki.usecase.DownloadMoveListUseCase
import io.github.sophon.wikiwavu.MOVE_URL
import io.github.sophon.wikiwavu.VIDEO_URL
import io.github.sophon.wikiwavu.util.cleanMoveInput

internal fun MoveListResponseDto.toDomain(
    characterData: DownloadMoveListUseCase.CharacterData
): List<Move> {
    val downloadedMoves = extractMoveDto()
    val movesById = downloadedMoves.associateBy { it.id }
    val moveList = downloadedMoves.map { it.mapToDomain(characterData, movesById) }
    return moveList
}

internal fun MoveListResponseDto.extractMoveDto(): List<MoveDto> {
    return cargoQuery.map { it.title }
}

internal fun MoveDto.mapToDomain(
    characterData: DownloadMoveListUseCase.CharacterData,
    movesById: Map<String, MoveDto>,
): Move {
    val cleanedCrushes = splitCrush()
    val unifiedNotes = splitNotes() + cleanedCrushes
    val fullInput = formCompleteDataFromParent(movesById) { it.input }
        .orEmpty()
        .cleanMoveInput()

    val move = Move(
        charName = characterData.name,
        id = id.formId(),
        name = name?.cleanHtml(),

        input = fullInput,
        damage = formCompleteDataFromParent(movesById) { it.damage },
        startup = getRootStartup(movesById),
        recovery = recv,
        onBlock = block,
        onHit = hit,
        onCH = ch,
        guard = formCompleteDataFromParent(movesById) { it.target },

        notes = splitNotes() + cleanedCrushes,
        aliases = alias.formAliases(input = fullInput),

        urls = Move.Urls(
            videoId = video.formVideoUrl(),
            wikiUrl = formMoveWikiUrl(characterData.name, id),
            characterImage = characterData.imageUrl,
        ),

        t8Properties = formProperties(
            notes = unifiedNotes,
            crushes = cleanedCrushes,
            input = fullInput,
        )
    )

    return move
}

internal fun String.formId(): String {
    return this
        .split(' ')
        .joinToString("_") { it.lowercase() }
        .cleanMoveInput()
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
    notes: List<String>,
    crushes: List<String>,
    input: String,
): Move.T8Properties {
    return Move.T8Properties(
        isHeat = notes.any { it.contains("Heat Engager", ignoreCase = true) },
        isPowerCrush = crushes.any { it.contains("pc", ignoreCase = true) },
        isHoming = notes.any { it.contains("Homing", ignoreCase = true) },
        stance = input.isStance(),
    )
}

//TODO: write unit tests
internal fun String.isStance(): String? {
    val stance = take(3).takeIf {
        length >= 4 && it.all { char -> char.isLetter() } && get(3) == '.' && it != "otg"
    }
    return stance
}

internal fun String?.formAliases(input: String): List<String> {
    val aliases: MutableList<String> = this
        .orEmpty()
        .cleanHtml()
        .split("* ", "or")
        .map {
            it
                .trim()
                .cleanMoveInput(keepSpaces = true)
        }
        .filter { it.isNotEmpty() }
        .toMutableList()

    if (
        input.contains(".")
        && input.split(".").first().length == 3
    ) {
        aliases.add(input.replace(".", ""))
    }

    return aliases
}

internal fun String?.formVideoUrl(): String? {
    return this?.let { VIDEO_URL + it.urlEncode() }
}

//https://wavu.wiki/t/Armor_King_movelist#Armor_King-1+2
internal fun formMoveWikiUrl(charName: String, id: String): String {
    return "${MOVE_URL}/${charName.replace(" ", "_")}_movelist#${id.replace(" ", "_")}"
}