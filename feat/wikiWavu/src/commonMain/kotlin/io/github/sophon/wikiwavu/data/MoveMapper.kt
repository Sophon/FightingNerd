package io.github.sophon.wikiwavu.data

import io.github.sophon.core.util.cleanHtml
import io.github.sophon.core.util.cleanHtmlOrNull
import io.github.sophon.core.util.urlEncode
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wikiwavu.domain.MOVE_URL
import io.github.sophon.wikiwavu.domain.VIDEO_URL
import io.github.sophon.wikiwavu.domain.cleanMoveInput

internal fun MoveListResponseDto.toDomain(character: Character): List<Move> {
    val downloadedMoves = extractMoveDto()
    val movesById = downloadedMoves.associateBy { it.id }
    val moveList = downloadedMoves.map { it.toDomain(character, movesById) }
    return moveList
}

internal fun MoveListResponseDto.extractMoveDto(): List<MoveDto> {
    return cargoQuery.map { it.title }
}

internal fun MoveDto.toDomain(
    character: Character,
    movesById: Map<String, MoveDto>,
): Move {
    val cleanedCrushes = splitCrush()
    val unifiedNotes = notes.formNotes() + cleanedCrushes
    val parentalProperties = formCompleteDataFromParent(movesById)
    val fullInput = parentalProperties.input
        .cleanHtml()
        .cleanMoveInput()
    val aliases = fullInput.formAliases(alias, alt)

    val move = Move(
        characterId = character.id,
        id = id.formId(),
        name = name?.cleanHtml(),

        input = fullInput,
        damage = parentalProperties.damage,
        startup = parentalProperties.startup,
        recovery = recv,
        onBlock = block,
        onHit = hit.formatClickable(),
        onCH = ch.formatClickable(),
        guard = parentalProperties.guard,

        notes = notes.formNotes() + cleanedCrushes,
        aliases = aliases,

        urls = Move.Urls(
            videoId = video.formVideoUrl(),
            wikiUrl = formMoveWikiUrl(characterRemoteQueryId = character.remoteQueryId, moveId = id),
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
        .asSequence()
        .flatMap { it.split("* ", " or ") }
        .map {
            it
                .trim()
                .cleanMoveInput(keepSpaces = true)
        }
        .filter { it.isNotEmpty() }
        .flatMap { alias ->
            if (alias.contains("cd.")) {
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

    if (this.contains(".") && this.split(".").first().length == 3) {
        aliases.add(this.replace(".", ""))
    }

    if (this.startsWith("ss.")) {
        aliases.add(this.replace("ss.", "ss"))
    }

    if (this.contains("h.", ignoreCase = true) && this.startsWith("h.", ignoreCase = true).not()) {
        val heatless = this.replace("h.", "")
        aliases.add("h.$heatless")
    }

    if (this == "h.2+3") {
        aliases.addAll(listOf("hs", "heatsmash"))
    }

    if (this.contains("cd.", ignoreCase = true)) {
        aliases.add(this.replace("cd.", "cd"))
    }

    if (this.startsWith("hfc", ignoreCase = true)) {
        aliases.add(this.replace("hfc", "fc"))
    }

    val result = aliases
        .distinct()
        .filterNot { it == this }

    return result
}

internal fun String?.formVideoUrl(): String? {
    return this?.let { VIDEO_URL + it.urlEncode() }
}

internal fun formMoveWikiUrl(characterRemoteQueryId: String, moveId: String): String {
    val formattedCharacterName = characterRemoteQueryId.replace(" ", "_")
    return "${MOVE_URL}/${formattedCharacterName}_movelist#${moveId.replace(" ", "_")}"
}


/**
 * Kazuya's `112` is actually:
 *
 *  - input: ,2
 *  - damage: ,6
 *  - parent: Kazuya-1,1,
 *
 *  So we have to traverse through parents to form the complete string.
 *  Same for other properties.
 */
internal fun MoveDto.formCompleteDataFromParent(movesById: Map<String, MoveDto>): ParentalProperties {
    var current: MoveDto? = this
    val reversed = mutableListOf<ParentalProperties>()
    val visited = mutableSetOf<String>()

    reversed.add(ParentalProperties(input = input, startup = startup, damage = damage, guard = target))
    visited.add(id)

    while (current != null) {
        when {
            current.parent == null -> break
            visited.contains(current.parent) -> break
            else -> {
                current = movesById[current.parent]?.let { parent ->
                    val parentalProperties = ParentalProperties(
                        input = parent.input,
                        startup = parent.startup,
                        damage = parent.damage,
                        guard = parent.target,
                    )
                    reversed.add(parentalProperties)
                    visited.add(parent.id)
                    parent
                }
            }
        }
    }

    val properties = reversed.reversed()

    val input = properties
        .map { it.input }
        .filter { it.isNotEmpty() }
        .joinToString("") { it.replace(",", "").replace(" ", "") }

    val startupList = properties.mapNotNull {
        it.startup
            ?.removePrefix(",")
            ?.ifEmpty { null }
    }
    val startup = when {
        startupList.isEmpty() -> null
        startupList.size == 1 -> startupList.first()
        else -> "${startupList.first()} (${startupList.drop(1).joinToString(", ")})"
    }

    val damage = properties
        .mapNotNull {
            it.damage
                ?.removePrefix(",")
                ?.ifEmpty { null }
        }
        .joinToString(", ")
        .takeIf { it.isNotEmpty() }

    val guard = properties
        .mapNotNull {
            it.guard
                ?.removePrefix(",")
                ?.ifEmpty { null }
        }
        .joinToString(", ")
        .takeIf { it.isNotEmpty() }

    return ParentalProperties(input = input, startup = startup, damage = damage, guard = guard)
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

internal data class ParentalProperties(
    val input: String,
    val startup: String?,
    val damage: String?,
    val guard: String?,
)
