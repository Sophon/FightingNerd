package io.github.sophon.wikidragdown.data.db

import io.github.sophon.core.wiki.data.toDomain
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wikidragdown.data.SelectROA2ByCharacter

internal fun SelectROA2ByCharacter.toDomain(): Move {
    val move = Move(
        id = id,
        characterId = characterId,
        name = name,
        input = input,
        damage = damage,
        startup = startup,
        active = active,
        recovery = recovery,
        onBlock = onBlock,
        onHit = onHit,
        onCH = onCH,
        guard = guard,
        cancel = cancel,
        invulnerability = invulnerability,
        isThrow = isThrow,
        notes = notes.toDomain(),
        aliases = aliases.toDomain(),
        urls = Move.Urls(
            wikiUrl = urlsWikiUrl.orEmpty(),
            videoId = urlsVideoId,
            hitboxImageList = urlsHitboxImageList.toDomain(),
            moveImageList = urlsMoveImageList.toDomain(),
        ),
        roa2Properties = Move.Roa2Properties(
            mode = mode,
            caption = caption?.toDomain(),
            hitboxCaption = hitboxCaption?.toDomain(),
            startupNotes = startupNotes,
            totalActiveNotes = totalActiveNotes,
            endlagNotes = endlagNotes,
            cancelNotes = cancelNotes?.toDomain(),
            landingLag = landingLag,
            landingLagNotes = landingLagNotes,
            iasa = iasa,
            iasaNotes = iasaNotes,
            totalDuration = totalDuration,
            totalDurationNotes = totalDurationNotes,
            ledgeGrabFrame = ledgeGrabFrame,
            ledgeGrabFrameNotes = ledgeGrabFrameNotes,
            hitID = hitID?.toDomain(),
            hitMoveID = hitMoveID?.toDomain(),
            hitName = hitName?.toDomain(),
            hitActive = hitActive?.toDomain(),
            customShieldSafety = customShieldSafety?.toDomain(),
            uniqueField = uniqueField?.toDomain(),
            articleID = articleID?.toDomain(),
            notes = roa2Notes,
            advNotes = advNotes,
        ),
    )
    return move
}
