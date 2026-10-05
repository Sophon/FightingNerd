package io.github.sophon.wiki.adapter.outbound.ktor.dragDown

import io.github.sophon.core.util.cleanHtml
import io.github.sophon.core.util.cleanHtmlOrNull
import io.github.sophon.core.util.toClickable
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.Move
import io.github.sophon.wiki.model.game.Roa2MoveProperties

internal fun List<DragDownMoveResponseDto>.toDomain(
    character: Character,
    imageUrlMap: Map<String, String>,
): List<Move> {
    val moveList = map { dto -> dto.toDomain(character, imageUrlMap) }
    return moveList
}

/**
 * RoA2 has no input notation - the input is the attack id and its mode (`dspecialfadc`), so there's nothing to normalize.
 */
private fun DragDownMoveResponseDto.toDomain(
    character: Character,
    imageUrlMap: Map<String, String>,
): Move {
    val input = formInput()

    val move = Move(
        input = input,
        remoteId = attackID,
        name = attack,
        startup = startup,
        active = totalActive,
        recovery = endlag,
        cancel = cancel
            ?.filter { it.isNotBlank() }
            ?.joinToString(";")
            .cleanHtmlOrNull()
            ?.ifEmpty { null },
        notes = notes.formNotes(),
        urls = Move.Urls(
            hitboxImageList = hitbox.toImageUrlList(imageUrlMap),
            moveImageList = image.toImageUrlList(imageUrlMap),
            wikiUrl = character.wikiUrl,
        ),
        gameProperties = toGameProperties(),
    )
    return move
}

private fun DragDownMoveResponseDto.toGameProperties(): Roa2MoveProperties {
    val properties = Roa2MoveProperties(
        mode = mode.formMode(),
        caption = caption,
        hitboxCaption = hitboxCaption,
        startupNotes = startupNotes,
        totalActiveNotes = totalActiveNotes,
        endlagNotes = endlagNotes,
        cancelNotes = cancelNotes,
        landingLag = landingLag,
        landingLagNotes = landingLagNotes,
        iasa = iasa,
        iasaNotes = iasaNotes,
        totalDuration = totalDuration,
        totalDurationNotes = totalDurationNotes,
        ledgeGrabFrame = ledgeGrabFrame,
        ledgeGrabFrameNotes = ledgeGrabFrameNotes,
        hitID = hitID,
        hitMoveID = hitMoveID,
        hitName = hitName,
        hitActive = hitActive,
        customShieldSafety = customShieldSafety.filterOutJunk(),
        uniqueField = uniqueField.filterOutJunk(),
        articleID = articleID,
        advNotes = advNotes,
    )
    return properties
}

/**
 * The default mode adds nothing; other modes drop their parenthesised part - `Empty Inhale (Yippee!)` → `emptyinhale`.
 */
private fun DragDownMoveResponseDto.formInput(): String {
    val isDefault = (mode.equals("default", ignoreCase = true) || mode.equals("regular", ignoreCase = true))
    val modifier = if (mode.isNullOrBlank() || isDefault) {
        ""
    } else {
        mode
            .replace(parenthesesRegex, "")
            .replace(" ", "")
            .lowercase()
    }
    val input = "${attackID?.lowercase()}$modifier"
    return input
}

private fun String?.formMode(): String? {
    val mode = this
        ?.takeIf { it.lowercase() !in excludedModeSet }
        ?.replace(parenthesesRegex, "")
        ?.replace(" ", "")
        ?.lowercase()
    return mode
}

private fun String?.formNotes(): List<String> {
    val noteList = this
        ?.cleanHtml()
        ?.split("\n")
        ?.filter { it.isNotBlank() }
        ?.mapNotNull { it.toClickable(WIKI_BASE_URL) }
        .orEmpty()
    return noteList
}

private fun List<String>?.toImageUrlList(imageUrlMap: Map<String, String>): List<String> {
    val imageUrlList = this
        .orEmpty()
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .mapNotNull { imageUrlMap[it] }
    return imageUrlList
}

private fun List<String>?.filterOutJunk(): List<String>? {
    val filtered = this?.filter { it.count() > 3 }
    return filtered
}


private val parenthesesRegex = Regex("\\(.*?\\)")

private val excludedModeSet = setOf("default", "regular", "grounded")
