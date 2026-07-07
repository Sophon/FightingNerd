package io.github.sophon.wikidragdown.data

import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.model.Move.Roa2Properties.Mode
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
        input = attack.orEmpty().lowercase(),
        name = attack,

        startup = startup,
        active = totalActive,
        recovery = endlag,
        cancel = cancel
            ?.filter { it.isNotBlank() }
            ?.joinToString(";")
            ?.ifEmpty { null },

        urls = Move.Urls(
            hitboxImageList = hitboxImageList,
            moveImageList = moveImageList,
            wikiUrl = WIKI_BASE_URL,
        ),

        roa2Properties = Move.Roa2Properties(
            mode = mode?.toType(),
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
            notes = notes,
            advNotes = advNotes,
        )
    )

    //TODO: move prefix based on Mode

    return move
}

private fun MoveResponseDto.formId(character: Character): String {
    val modifier = when (val modeEnum = mode.orEmpty().toType()) {
        Mode.Default -> ""
        else -> modeEnum.name.lowercase()
    }
    val id = "${character.id}_${this.attackID?.lowercase()}${modifier}"
    return id
}

private fun String.toType(): Mode {
    return when {
        this.equals("airborne", ignoreCase = true) -> Mode.Airborne
        this.equals("grounded", ignoreCase = true) -> Mode.Grounded
        this.equals("armor", ignoreCase = true) -> Mode.Armor
        this.equals("armored", ignoreCase = true) -> Mode.Armor
        this.equals("regular", ignoreCase = true) -> Mode.Regular
        this.equals("punch", ignoreCase = true) -> Mode.Punch
        this.equals("cooldown", ignoreCase = true) -> Mode.Cooldown
        this.equals("hitgrab", ignoreCase = true) -> Mode.HitThrow
        this.startsWith("hit", ignoreCase = true) -> Mode.Multihit
        this.contains("jab", ignoreCase = true) -> Mode.Jab
        this.contains("fadc", ignoreCase = true) -> Mode.FADC
        this.contains("airborne gem", ignoreCase = true) -> Mode.AirGem
        this.contains("airslam gem", ignoreCase = true) -> Mode.AirGem
        this.contains("landing slam", ignoreCase = true) -> Mode.LandingSlam
        this.contains("air slam", ignoreCase = true) -> Mode.AirSlam
        this.contains("airslam", ignoreCase = true) -> Mode.AirSlam
        this.contains("gem landing", ignoreCase = true) -> Mode.GemLanding
        this.contains("gem dash", ignoreCase = true) -> Mode.GemDash
        this.contains("gem pop", ignoreCase = true) -> Mode.GemPop
        this.contains("throw gem", ignoreCase = true) -> Mode.ThrowGem
        this.equals("full charge", ignoreCase = true) -> Mode.FullCharge
        this.contains("grab", ignoreCase = true) -> Mode.Throw
        this.contains("throw", ignoreCase = true) -> Mode.Throw
        else -> Mode.Default
    }
}

private fun List<String>?.filterOutJunk(): List<String>? {
    return this?.filter { it.count() > 3 }
}
