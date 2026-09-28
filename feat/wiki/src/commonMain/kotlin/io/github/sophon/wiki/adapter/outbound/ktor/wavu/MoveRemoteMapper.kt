package io.github.sophon.wiki.adapter.outbound.ktor.wavu

import io.github.sophon.core.util.cleanHtml
import io.github.sophon.core.util.cleanHtmlOrNull
import io.github.sophon.core.util.urlEncode
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wiki.application.domain.model.gameProperties.T8Properties

internal fun MoveListResponseDto.toDomain(character: Character): List<Move> {
    val downloadedMoves = cargoQuery.map { it.title }
    val movesById = downloadedMoves.associateBy { it.id }
    val moveList = downloadedMoves.map { it.toDomain(character, movesById) }
    return moveList
}

private fun MoveDto.toDomain(
    character: Character,
    movesById: Map<String, MoveDto>,
): Move {
    val cleanedCrushes = splitCrush()
    val unifiedNotes = (notes.formNotes() + cleanedCrushes)
    val parentalProperties = formCompleteDataFromParent(movesById)
    val fullInput = parentalProperties.input.cleanHtml()
    val aliases = formAliases(alias, alt)

    val move = Move(
        characterId = character.id,
        id = id,
        name = name?.cleanHtml(),

        input = fullInput,
        damage = parentalProperties.damage,
        startup = parentalProperties.startup,
        recovery = recv,
        onBlock = block,
        onHit = hit.formatClickable(),
        onCH = ch.formatClickable(),
        guard = parentalProperties.guard,
        isThrow = isThrow(target, unifiedNotes),

        notes = unifiedNotes,
        aliases = aliases,

        urls = Move.Urls(
            videoId = video?.toStorageSafeFileName(),
            videoUrl = video.formVideoUrl(),
            wikiUrl = formMoveWikiUrl(characterRemoteQueryId = character.remoteQueryId, moveId = id),
        ),

        gameProperties = formProperties(
            notes = unifiedNotes,
            crushes = cleanedCrushes,
        )
    )

    return move
}

/**
 * Kazuya's `1,1,2` is actually:
 *
 *  - input: ,2
 *  - damage: ,6
 *  - parent: Kazuya-1,1,
 *
 *  So we have to traverse through parents to form the complete string.
 *  Same for other properties.
 */
private fun MoveDto.formCompleteDataFromParent(movesById: Map<String, MoveDto>): ParentalProperties {
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

    val input = properties.joinToString("") { it.input }

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

    val parentalProperties = ParentalProperties(input = input, startup = startup, damage = damage, guard = guard)
    return parentalProperties
}

private fun formAliases(alias: String?, alt: String?): List<String> {
    val aliases = (alias.toAliases() + alt.toAliases()).distinct()
    return aliases
}

private fun String?.toAliases(): List<String> {
    val cleaned = this
        .cleanHtmlOrNull()
        ?.replace("\n", "")
        ?.replace("\\n", "")
        ?: return emptyList()

    val entries = cleaned
        .split("* ", " or ", ignoreCase = true)
        .map { it.trim() }
        .filter { it.isNotEmpty() }

    return entries
}

private fun String?.formVideoUrl(): String? {
    val videoUrl = this?.let { URL_PREFIX_VIDEO + it.urlEncode() }
    return videoUrl
}

private fun String.toStorageSafeFileName(): String {
    val stripped = removePrefix("File:").replace(":", "_")
    return stripped
}

private fun formMoveWikiUrl(characterRemoteQueryId: String, moveId: String): String {
    val formattedCharacterName = characterRemoteQueryId.replace(" ", "_")
    val wikiUrl = "${URL_PREFIX_MOVE}/${formattedCharacterName}_movelist#${moveId.replace(" ", "_")}"
    return wikiUrl
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

    val formatted = replace(Regex("""\[\[([^|]+)\|([^\]]+)\]\]""")) { matchResult ->
        val description = matchResult.groupValues[2]
        val destination = matchResult.groupValues[1].replace(" ", "_")
        "[$description]($URL_PREFIX_MOVE/$destination)"
    }
    return formatted
}

/**
 * Only the properties that don't depend on the notation - heat and stance are derived by the normalizer.
 */
private fun formProperties(
    notes: List<String>,
    crushes: List<String>,
): T8Properties {
    val isPowerCrush = crushes.any { it.contains("pc", ignoreCase = true) }
    val isHoming = notes.any { it.contains("Homing", ignoreCase = true) }
    val isHighCrush = notes.any { it.contains("cs") }
    val isLowCrush = notes.any { it.contains("js") }
    val hasWallInteraction = notes.any { it.contains("balcony break", ignoreCase = true) }
    val hasFloorInteraction = notes.any { it.contains("floor break", ignoreCase = true) }

    val properties = T8Properties(
        isHoming = isHoming,
        isPowerCrush = isPowerCrush,
        isHighCrush = isHighCrush,
        isLowCrush = isLowCrush,
        hasWallInteraction = hasWallInteraction,
        hasFloorInteraction = hasFloorInteraction,
    )
    return properties
}

private fun isThrow(guard: String?, notes: List<String>): Boolean {
    val level = guard.orEmpty().lowercase()
    val isThrow = (level.contains("t")
            || level.contains("th(")
            || notes.any { it.contains("throw break", ignoreCase = true) })
    return isThrow
}

private fun String?.formNotes(): List<String> {
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

private data class ParentalProperties(
    val input: String,
    val startup: String?,
    val damage: String?,
    val guard: String?,
)
