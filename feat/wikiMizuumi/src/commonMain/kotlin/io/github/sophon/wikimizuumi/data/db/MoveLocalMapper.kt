package io.github.sophon.wikimizuumi.data.db

import io.github.sophon.core.wiki.data.toDomain
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wikimizuumi.data.SelectMBTLByCharacter
import io.github.sophon.wikimizuumi.data.SelectUni2ByCharacter
import io.github.sophon.wikimizuumi.data.SelectVSAVByCharacter

internal fun SelectMBTLByCharacter.toDomain(): Move {
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
        mbProperties = Move.MBProperties(
            inputInfo = inputInfo,
            subtitle = subtitle,
            minDamage = minDamage,
            property = property_,
            cost = cost,
            attribute = attribute,
            landing = landing,
            overall = overall,
        ),
    )
    return move
}

internal fun SelectUni2ByCharacter.toDomain(): Move {
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
        uni2Properties = Move.Uni2Properties(
            inputInfo = inputInfo,
            subtitle = subtitle,
            minDamage = minDamage,
            type = type,
            cancelWindow = cancelWindow,
            property = property_,
            cost = cost,
            attribute = attribute,
            landing = landing,
            overall = overall,
            assaultAdv = assaultAdv,
            blockstun = blockstun,
            groundHit = groundHit,
            airHit = airHit,
            groundCH = groundCH,
            airCH = airCH,
            hitstop = hitstop,
            CHstop = CHstop,
            proration = proration,
            comboP1 = comboP1,
            comboP2 = comboP2,
        ),
    )
    return move
}

internal fun SelectVSAVByCharacter.toDomain(): Move {
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
        vsavProperties = Move.VSAVProperties(
            inputInfo = inputInfo,
            subtitle = subtitle,
            whiteDmg = whiteDmg,
            renda = renda,
            meter = meter,
            reaction = reaction,
            curseTime = curseTime,
        ),
    )
    return move
}
