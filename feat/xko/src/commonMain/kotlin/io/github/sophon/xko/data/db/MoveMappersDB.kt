package io.github.sophon.xko.data.db

import io.github.sophon.core.wiki.data.toDomain
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.xko.data.MoveEntity

internal fun MoveEntity.toDomain(): Move {
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
    )
    return move
}
