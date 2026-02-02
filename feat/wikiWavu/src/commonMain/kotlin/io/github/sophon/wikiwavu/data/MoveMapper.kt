package io.github.sophon.wikiwavu.data

import io.github.sophon.core.util.cleanHtml
import io.github.sophon.core.util.cleanHtmlOrNull
import io.github.sophon.core.util.urlEncode
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.core.wiki.usecase.DownloadMoveListUseCase
import io.github.sophon.wikiwavu.MOVE_URL
import io.github.sophon.wikiwavu.VIDEO_URL
import io.github.sophon.wikiwavu.domain.cleanMoveInput

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
    if (this.name == "Cloud Gates") {
        val a = 3
    }

    val cleanedCrushes = splitCrush()
    val unifiedNotes = notes.formNotes() + cleanedCrushes
    val fullInput = formCompleteDataFromParent(movesById) { it.input }
        .orEmpty()
        .cleanHtml()
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
        onHit = hit.formatClickable(),
        onCH = ch.formatClickable(),
        guard = formCompleteDataFromParent(movesById) { it.target },

        notes = notes.formNotes() + cleanedCrushes,
        aliases = fullInput.formAliases(alias, alt),

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

internal fun String?.formNotes(): List<String> {
    val finalNotes = this.orEmpty()
        .trimIndent()
        .cleanHtml()
        .replace("\n\n", "\n")
        .lines()
        .filter { it.isNotEmpty() }
        .map { it.removePrefix("* ").trim() }
        .mapNotNull { it.formatClickable() }

    return finalNotes
}

internal fun String.getStance(): String? {
    when {
        startsWith("BT", ignoreCase = true) -> return "BT"
        startsWith("CD", ignoreCase = true) -> return "CD"
    }

    val stance = take(3).takeIf {
        (length >= 4 && it.all { char -> char.isLetter() } && get(3) == '.' && it != "otg")
    }
    return stance
}

internal fun String.formAliases(alias: String?, alt: String?): List<String> {
    val cleanedAliases = alias
        .cleanHtmlOrNull()
        ?.replace("\n", "")
        ?.replace("\\n", "")
        ?.lowercase()
    val cleanedAlts = alt
        .cleanHtmlOrNull()
        ?.replace("\n", "")
        ?.replace("\\n", "")
        ?.lowercase()

    val aliases: MutableList<String> = listOfNotNull(cleanedAliases, cleanedAlts)
        .flatMap { it.split("* ", "or") }
        .map {
            it
                .trim()
                .cleanMoveInput(keepSpaces = true)
        }
        .filter { it.isNotEmpty() }
        .flatMap { alias ->
            if (alias.startsWith("cd.")) {
                listOf(alias, alias.replace(".", ""))
            } else {
                listOf(alias)
            }
        }
        .toMutableList()

    when {
        this.startsWith("cd.df#") -> {
            aliases.add(this.replaceFirst("cd.df#", "cd#"))
        }
        this.startsWith("cd.df") -> {
            aliases.add(this.replaceFirst("cd.df", "cd"))
            aliases.add(this.replaceFirst("cd.df", "cd."))
        }
        this.startsWith("cd.") -> aliases.add(this.replace("cd.", "cd"))
    }

    if (
        this.contains(".")
        && this.split(".").first().length == 3
    ) {
        aliases.add(this.replace(".", ""))
    }

    if (this.startsWith("ss.")) {
        aliases.add(this.replace("ss.", "ss"))
    }

    if (
        this.contains("h.", ignoreCase = true)
        && this.startsWith("h.", ignoreCase = true).not()
    ) {
        val heatless = this.replace("h.", "")
        aliases.add("h.$heatless")
    }

    if (this == "h.2+3") {
        aliases.addAll(listOf("hs", "heatsmash"))
    }

    return aliases.distinct()
}

internal fun String?.formVideoUrl(): String? {
    return this?.let { VIDEO_URL + it.urlEncode() }
}

internal fun formMoveWikiUrl(charName: String, id: String): String {
    return "${MOVE_URL}/${charName.replace(" ", "_")}_movelist#${id.replace(" ", "_")}"
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
internal fun MoveDto.getRootStartup(
    movesById: Map<String, MoveDto>
): String? {
    val history = mutableListOf<String>()
    var current: MoveDto? = this
    var root: MoveDto = this

    // Traverse up to find the topmost parent
    while (current != null) {
        root = current
        current = current.parent?.let {
            val parent = movesById[it]
            current.startup?.let { startup -> history.add(startup) }
            parent
        }
    }

    val formattedHistory = history
        .reversed()
        .joinToString(", ") { it.replace(",", "") }

    val result = when {
        root.startup == null -> null
        history.isEmpty() -> root.startup
        else -> "${root.startup} ($formattedHistory)"
    }

    return result
}

private fun MoveDto.splitCrush(): List<String> {
    val finalCrushes = crush.orEmpty()
        .trimIndent()
        .cleanHtml()
        .lines()
        .filterNot { it.isEmpty() }
        .map { it.removePrefix("* ").trim() }

    return finalCrushes
}

private fun String?.formatClickable(): String? {
    if (this == null) return null

    return replace(Regex("""\[\[([^|]+)\|([^\]]+)\]\]""")) { matchResult ->
        val description = matchResult.groupValues[2]
        val destination = matchResult.groupValues[1].replace(" ", "_")
        "[$description]($MOVE_URL/$destination)"
    }
}

private fun formProperties(
    notes: List<String>,
    crushes: List<String>,
    input: String,
): Move.T8Properties {
    val isHeat = notes.any { it.contains("Heat Engager", ignoreCase = true) }
            || notes.any { it.contains("Heat Smash", ignoreCase = true) }
            || input.contains("H.", ignoreCase = true)

    val isPowerCrush = crushes.any { it.contains("pc", ignoreCase = true) }
    val isHoming = notes.any { it.contains("Homing", ignoreCase = true) }
    val stance = input.getStance()

    return Move.T8Properties(isHeat, isHoming, stance, isPowerCrush)
}
