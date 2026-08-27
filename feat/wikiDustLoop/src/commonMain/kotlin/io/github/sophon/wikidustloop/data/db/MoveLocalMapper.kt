package io.github.sophon.wikidustloop.data.db

import io.github.sophon.core.wiki.data.toDomain
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wikidustloop.data.SelectBBCFByCharacter
import io.github.sophon.wikidustloop.data.SelectDBFZByCharacter
import io.github.sophon.wikidustloop.data.SelectGBVSRByCharacter
import io.github.sophon.wikidustloop.data.SelectGGSTByCharacter
import io.github.sophon.wikidustloop.data.SelectMTFSByCharacter
import io.github.sophon.wikidustloop.integration.model.BBMoveProperties
import io.github.sophon.wikidustloop.integration.model.DBFZMoveProperties
import io.github.sophon.wikidustloop.integration.model.GBVSRMoveProperties
import io.github.sophon.wikidustloop.integration.model.GGSTMoveProperties
import io.github.sophon.wikidustloop.integration.model.MTFSMoveProperties

internal fun SelectGGSTByCharacter.toDomain(): Move {
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
        type = type,
        notes = notes.toDomain(),
        aliases = aliases.toDomain(),
        urls = Move.Urls(
            wikiUrl = urlsWikiUrl.orEmpty(),
            videoId = urlsVideoId,
            videoUrl = urlsVideoUrl,
            hitboxImageList = urlsHitboxImageList.toDomain(),
            moveImageList = urlsMoveImageList.toDomain(),
        ),
        gameProperties = GGSTMoveProperties(
            riscGain = riscGain,
            riscLoss = riscLoss,
            wallDamage = wallDamage,
            inputTension = inputTension,
            chipRatio = chipRatio,
            otgType = otgType,
            prorate = prorate,
            level = level,
        ),
    )
    return move
}

internal fun SelectDBFZByCharacter.toDomain(): Move {
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
        type = type,
        notes = notes.toDomain(),
        aliases = aliases.toDomain(),
        urls = Move.Urls(
            wikiUrl = urlsWikiUrl.orEmpty(),
            videoId = urlsVideoId,
            videoUrl = urlsVideoUrl,
            hitboxImageList = urlsHitboxImageList.toDomain(),
            moveImageList = urlsMoveImageList.toDomain(),
        ),
        gameProperties = DBFZMoveProperties(
            attribute = attribute,
            smash = smash,
            kiGain = kiGain,
            prorate = prorate,
            blockStun = blockStun,
            groundHit = groundHit,
            airHit = airHit,
            level = level,
        ),
    )
    return move
}

internal fun SelectGBVSRByCharacter.toDomain(): Move {
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
        type = type,
        notes = notes.toDomain(),
        aliases = aliases.toDomain(),
        urls = Move.Urls(
            wikiUrl = urlsWikiUrl.orEmpty(),
            videoId = urlsVideoId,
            videoUrl = urlsVideoUrl,
            hitboxImageList = urlsHitboxImageList.toDomain(),
            moveImageList = urlsMoveImageList.toDomain(),
        ),
        gameProperties = GBVSRMoveProperties(
            meter = meter,
            level = level,
            cooldown = cooldown,
            cls = cls,
        ),
    )
    return move
}

internal fun SelectBBCFByCharacter.toDomain(): Move {
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
        type = type,
        notes = notes.toDomain(),
        aliases = aliases.toDomain(),
        urls = Move.Urls(
            wikiUrl = urlsWikiUrl.orEmpty(),
            videoId = urlsVideoId,
            videoUrl = urlsVideoUrl,
            hitboxImageList = urlsHitboxImageList.toDomain(),
            moveImageList = urlsMoveImageList.toDomain(),
        ),
        gameProperties = BBMoveProperties(
            onODR = onODR,
            attribute = attribute,
            p1 = p1,
            p2 = p2,
            starter = starter,
            level = level,
            blockstun = blockstun,
            groundHit = groundHit,
            airHit = airHit,
            groundCH = groundCH,
            airCH = airCH,
            blockstop = blockstop,
            hitstop = hitstop,
            chStop = chStop,
            cancelTiming = cancelTiming,
        ),
    )
    return move
}

internal fun SelectMTFSByCharacter.toDomain(): Move {
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
        type = type,
        notes = notes.toDomain(),
        aliases = aliases.toDomain(),
        urls = Move.Urls(
            wikiUrl = urlsWikiUrl.orEmpty(),
            videoId = urlsVideoId,
            videoUrl = urlsVideoUrl,
            hitboxImageList = urlsHitboxImageList.toDomain(),
            moveImageList = urlsMoveImageList.toDomain(),
        ),
        gameProperties = MTFSMoveProperties(
            simpleInput = simpleInput,
            level = level,
            prorate = prorate,
            meterGain = meterGain,
            untechAmount = untechAmount,
            hitboxCaption = hitboxCaption,
        ),
    )
    return move
}
