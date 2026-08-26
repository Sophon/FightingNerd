package io.github.sophon.dreamcancel.data.db

import io.github.sophon.core.wiki.data.toDomain
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.dreamcancel.data.SelectCOTWByCharacter
import io.github.sophon.dreamcancel.data.SelectKoFXVByCharacter

internal fun SelectKoFXVByCharacter.toDomain(): Move {
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
        koF15Properties = Move.KOF15Properties(
            stun = stun,
        ),
    )
    return move
}

internal fun SelectCOTWByCharacter.toDomain(): Move {
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
        cotwProperties = Move.COTWProperties(
            revDamage = revDamage,
        ),
    )
    return move
}
