package io.github.sophon.wikidragdown.data

import io.github.sophon.core.util.cleanHtml
import io.github.sophon.core.util.cleanHtmlOrNull
import io.github.sophon.core.util.toClickable
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wikidragdown.domain.WIKI_BASE_URL

internal fun List<MoveResponseDto>.toDomain(
    character: Character,
    imageUrlMap: Map<String, String>,
): List<Move> {
    return this.map { dto ->
        val move = dto.toDomain(character, imageUrlMap)
        move
    }
}

internal fun MoveResponseDto.toDomain(
    character: Character,
    imageUrlMap: Map<String, String>,
): Move {
    val id = formId(character)
    val input = formInput()
    val hitboxImageList = hitbox.orEmpty()
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .mapNotNull { imageUrlMap[it] }

    val moveImageList = image.orEmpty()
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .mapNotNull { imageUrlMap[it] }

    val move = Move(
        id = id,
        characterId = character.id,
        input = input,
        name = attack,

        startup = startup,
        active = totalActive,
        recovery = endlag,
        cancel = cancel
            ?.filter { it.isNotBlank() }
            ?.joinToString(";")
            .cleanHtmlOrNull()
            ?.ifEmpty { null },
        notes = notes
            ?.cleanHtml()
            ?.split("\n")
            ?.filter { it.isNotBlank() }
            ?.mapNotNull { it.toClickable(WIKI_BASE_URL) }
            .orEmpty(),

        urls = Move.Urls(
            hitboxImageList = hitboxImageList,
            moveImageList = moveImageList,
            wikiUrl = WIKI_BASE_URL,
        ),

        roa2Properties = Move.Roa2Properties(
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
    )

    return move
}

private fun MoveResponseDto.formInput(): String {
    val isDefault = mode.equals("default", ignoreCase = true) || mode.equals("regular", ignoreCase = true)
    val modifier = if (mode.isNullOrBlank() || isDefault) {
        ""
    } else {
        mode
            .replace(Regex("\\(.*?\\)"), "")
            .replace(" ", "")
            .lowercase()
    }
    val id = "${this.attackID?.lowercase()}$modifier"
    return id
}

private fun MoveResponseDto.formId(character: Character): String {
    val id = "${character.id}_${this.formInput()}"
    return id
}

private fun List<String>?.filterOutJunk(): List<String>? {
    return this?.filter { it.count() > 3 }
}
