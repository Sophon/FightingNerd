package io.github.sophon.wikiwavu.data.db

import io.github.sophon.core.wiki.data.toDomain
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wikiwavu.data.SelectTekkenByCharacter
import io.github.sophon.wikiwavu.integration.model.T8Properties

internal fun SelectTekkenByCharacter.toDomain(): Move {
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
            videoUrl = urlsVideoUrl,
            hitboxImageList = urlsHitboxImageList.toDomain(),
            moveImageList = urlsMoveImageList.toDomain(),
        ),
        gameProperties = T8Properties(
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
    return move
}
