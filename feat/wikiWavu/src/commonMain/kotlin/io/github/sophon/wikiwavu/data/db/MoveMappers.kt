package io.github.sophon.wikiwavu.data.db

import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wikiwavu.data.MoveQueries
import io.github.sophon.wikiwavu.data.SelectTekkenByCharacter
import io.github.sophon.wikiwavu.data.TekkenMoveQueries

internal fun SelectTekkenByCharacter.toDomain(): Move {
    return Move(
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
        t8Properties = Move.T8Properties(
            isHeat = isHeat,
            isHoming = isHoming,
            stance = stance,
            isPowerCrush = isPowerCrush,
            isHighCrush = isHighCrush,
            isLowCrush = isLowCrush,
            hasWallInteraction = hasWallInteraction,
            hasFloorInteraction = hasFloorInteraction,
        ),
    )
}

internal fun persistMove(
    move: Move,
    moveQueries: MoveQueries,
    tekkenQueries: TekkenMoveQueries,
) {
    moveQueries.insertMove(
        id = move.id,
        characterId = move.characterId,
        name = move.name,
        input = move.input,
        damage = move.damage,
        startup = move.startup,
        active = move.active,
        recovery = move.recovery,
        onBlock = move.onBlock,
        onHit = move.onHit,
        onCH = move.onCH,
        guard = move.guard,
        cancel = move.cancel,
        invulnerability = move.invulnerability,
        isThrow = move.isThrow,
        notes = move.notes.fromDomain(),
        aliases = move.aliases.fromDomain(),
        urlsWikiUrl = move.urls.wikiUrl,
        urlsVideoId = move.urls.videoId,
        urlsHitboxImageList = move.urls.hitboxImageList.fromDomain(),
        urlsMoveImageList = move.urls.moveImageList.fromDomain(),
    )
    val t8 = move.t8Properties
    tekkenQueries.insertTekkenMove(
        moveId = move.id,
        isHeat = t8?.isHeat ?: false,
        isHoming = t8?.isHoming ?: false,
        stance = t8?.stance,
        isPowerCrush = t8?.isPowerCrush ?: false,
        isHighCrush = t8?.isHighCrush ?: false,
        isLowCrush = t8?.isLowCrush ?: false,
        hasWallInteraction = t8?.hasWallInteraction ?: false,
        hasFloorInteraction = t8?.hasFloorInteraction ?: false,
    )
}
