package io.github.sophon.fightingnerd.core.data

import io.github.sophon.core.wiki.model.Move
import move.SelectAllAvl
import move.SelectAllBb
import move.SelectAllCotw
import move.SelectAllDbfz
import move.SelectAllGbvsr
import move.SelectAllGgst
import move.SelectAllKof15
import move.SelectAllMb
import move.SelectAllMk
import move.SelectAllSf6
import move.SelectAllT8
import move.SelectAllUni2
import move.SelectAllVsav

internal fun move.Move.toDomain(): Move {
    return Move(
        id = id,
        charName = charName,
        name = name,
        input = input,
        damage = damage,
        startup = startup,
        onBlock = onBlock,
        onHit = onHit,
        onCH = onCH,
        active = active,
        cancel = cancel,
        recovery = recovery,
        guard = guard,
        invulnerability = invulnerability,
        notes = notes.toDomain(),
        aliases = aliases.toDomain(),
        urls = buildUrls(),
    )
}

internal fun move.Move.buildUrls(): Move.Urls {
    return Move.Urls(
        characterWiki = urlsCharacterWiki,
        characterImage = urlsCharacterImage,
        videoId = urlsVideoId,
        hitboxImageList = urlsHitboxImageList.toDomain(),
        moveImageList = urlsMoveImageList.toDomain(),
        wikiUrl = urlsWikiUrl,
    )
}

internal fun SelectAllT8.toDomain(): Move {
    return Move(
        id = id,
        charName = charName,
        name = name,
        input = input,
        damage = damage,
        startup = startup,
        onBlock = onBlock,
        onHit = onHit,
        onCH = onCH,
        active = active,
        cancel = cancel,
        recovery = recovery,
        guard = guard,
        invulnerability = invulnerability,
        notes = notes.toDomain(),
        aliases = aliases.toDomain(),
        urls = Move.Urls(
            characterWiki = urlsCharacterWiki,
            characterImage = urlsCharacterImage,
            videoId = urlsVideoId,
            hitboxImageList = urlsHitboxImageList.toDomain(),
            moveImageList = urlsMoveImageList.toDomain(),
            wikiUrl = urlsWikiUrl,
        ),
        t8Properties = if (moveId == null) {
            null
        } else {
            Move.T8Properties(
                isHeat = (isHeat == true),
                isHoming = (isHoming == true),
                stance = stance,
                isPowerCrush = (isPowerCrush == true),
                isHighCrush = (isHighCrush == true),
                isLowCrush = (isLowCrush == true),
            )
        },
    )
}

internal fun SelectAllSf6.toDomain(): Move {
    return Move(
        id = id,
        charName = charName,
        name = name,
        input = input,
        damage = damage,
        startup = startup,
        onBlock = onBlock,
        onHit = onHit,
        onCH = onCH,
        active = active,
        cancel = cancel,
        recovery = recovery,
        guard = guard,
        invulnerability = invulnerability,
        notes = notes.toDomain(),
        aliases = aliases.toDomain(),
        urls = Move.Urls(
            characterWiki = urlsCharacterWiki,
            characterImage = urlsCharacterImage,
            videoId = urlsVideoId,
            hitboxImageList = urlsHitboxImageList.toDomain(),
            moveImageList = urlsMoveImageList.toDomain(),
            wikiUrl = urlsWikiUrl,
        ),
        sf6Properties = if (moveId == null) {
            null
        } else {
            Move.SF6Properties(
                type = type?.let { Move.SF6Properties.Type.valueOf(it) },
                images = images?.toDomain(),
                chip = chip,
                dmgScaling = dmgScaling,
                total = total,
                hitConfirm = hitConfirm,
                punishAdv = punishAdv,
                perfParryAdv = perfParryAdv,
                DRcOH = DRcOH,
                DRcOB = DRcOB,
                DROH = DROH,
                DROB = DROB,
                hitStun = hitStun,
                blockStun = blockStun,
                hitStop = hitStop,
                driveDmgOnBlock = driveDmgOnBlock,
                driveDmgOnHit = driveDmgOnHit,
                driveGain = driveGain,
                superGainOnHit = superGainOnHit,
                superGainOnBlock = superGainOnBlock,
                armor = armor,
                airborne = airborne,
                jugStart = jugStart,
                jugIncrease = jugIncrease,
                jugLimit = jugLimit,
                projectileSpeed = projectileSpeed,
                attackRange = attackRange,
            )
        },
    )
}

internal fun SelectAllKof15.toDomain(): Move {
    return Move(
        id = id,
        charName = charName,
        name = name,
        input = input,
        damage = damage,
        startup = startup,
        onBlock = onBlock,
        onHit = onHit,
        onCH = onCH,
        active = active,
        cancel = cancel,
        recovery = recovery,
        guard = guard,
        invulnerability = invulnerability,
        notes = notes.toDomain(),
        aliases = aliases.toDomain(),
        urls = Move.Urls(
            characterWiki = urlsCharacterWiki,
            characterImage = urlsCharacterImage,
            videoId = urlsVideoId,
            hitboxImageList = urlsHitboxImageList.toDomain(),
            moveImageList = urlsMoveImageList.toDomain(),
            wikiUrl = urlsWikiUrl,
        ),
        koF15Properties = if (moveId == null) {
            null
        } else {
            Move.KOF15Properties(
                stun = stun,
            )
        },
    )
}

internal fun SelectAllCotw.toDomain(): Move {
    return Move(
        id = id,
        charName = charName,
        name = name,
        input = input,
        damage = damage,
        startup = startup,
        onBlock = onBlock,
        onHit = onHit,
        onCH = onCH,
        active = active,
        cancel = cancel,
        recovery = recovery,
        guard = guard,
        invulnerability = invulnerability,
        notes = notes.toDomain(),
        aliases = aliases.toDomain(),
        urls = Move.Urls(
            characterWiki = urlsCharacterWiki,
            characterImage = urlsCharacterImage,
            videoId = urlsVideoId,
            hitboxImageList = urlsHitboxImageList.toDomain(),
            moveImageList = urlsMoveImageList.toDomain(),
            wikiUrl = urlsWikiUrl,
        ),
        cotwProperties = if (moveId == null) {
            null
        } else {
            Move.COTWProperties(
                revDamage = revDamage,
            )
        },
    )
}

internal fun SelectAllGgst.toDomain(): Move {
    return Move(
        id = id,
        charName = charName,
        name = name,
        input = input,
        damage = damage,
        startup = startup,
        onBlock = onBlock,
        onHit = onHit,
        onCH = onCH,
        active = active,
        cancel = cancel,
        recovery = recovery,
        guard = guard,
        invulnerability = invulnerability,
        notes = notes.toDomain(),
        aliases = aliases.toDomain(),
        urls = Move.Urls(
            characterWiki = urlsCharacterWiki,
            characterImage = urlsCharacterImage,
            videoId = urlsVideoId,
            hitboxImageList = urlsHitboxImageList.toDomain(),
            moveImageList = urlsMoveImageList.toDomain(),
            wikiUrl = urlsWikiUrl,
        ),
        ggstProperties = if (moveId == null) {
            null
        } else {
            Move.GGSTProperties(
                type = type,
                riscGain = riscGain,
                riscLoss = riscLoss,
                wallDamage = wallDamage,
                inputTension = inputTension,
                chipRatio = chipRatio,
                otgType = otgType,
                prorate = prorate,
                level = level,
            )
        },
    )
}

internal fun SelectAllDbfz.toDomain(): Move {
    return Move(
        id = id,
        charName = charName,
        name = name,
        input = input,
        damage = damage,
        startup = startup,
        onBlock = onBlock,
        onHit = onHit,
        onCH = onCH,
        active = active,
        cancel = cancel,
        recovery = recovery,
        guard = guard,
        invulnerability = invulnerability,
        notes = notes.toDomain(),
        aliases = aliases.toDomain(),
        urls = Move.Urls(
            characterWiki = urlsCharacterWiki,
            characterImage = urlsCharacterImage,
            videoId = urlsVideoId,
            hitboxImageList = urlsHitboxImageList.toDomain(),
            moveImageList = urlsMoveImageList.toDomain(),
            wikiUrl = urlsWikiUrl,
        ),
        dbfzProperties = if (moveId == null) {
            null
        } else {
            Move.DBFZProperties(
                attribute = attribute,
                smash = smash,
                kiGain = kiGain,
                prorate = prorate,
                blockStun = blockStun,
                groundHit = groundHit,
                airHit = airHit,
                type = type,
                level = level,
            )
        },
    )
}

internal fun SelectAllGbvsr.toDomain(): Move {
    return Move(
        id = id,
        charName = charName,
        name = name,
        input = input,
        damage = damage,
        startup = startup,
        onBlock = onBlock,
        onHit = onHit,
        onCH = onCH,
        active = active,
        cancel = cancel,
        recovery = recovery,
        guard = guard,
        invulnerability = invulnerability,
        notes = notes.toDomain(),
        aliases = aliases.toDomain(),
        urls = Move.Urls(
            characterWiki = urlsCharacterWiki,
            characterImage = urlsCharacterImage,
            videoId = urlsVideoId,
            hitboxImageList = urlsHitboxImageList.toDomain(),
            moveImageList = urlsMoveImageList.toDomain(),
            wikiUrl = urlsWikiUrl,
        ),
        gbvsrProperties = if (moveId == null) {
            null
        } else {
            Move.GBVSRProperties(
                meter = meter,
                level = level,
                cooldown = cooldown,
                cls = cls,
                type = type,
            )
        },
    )
}

internal fun SelectAllMk.toDomain(): Move {
    return Move(
        id = id,
        charName = charName,
        name = name,
        input = input,
        damage = damage,
        startup = startup,
        onBlock = onBlock,
        onHit = onHit,
        onCH = onCH,
        active = active,
        cancel = cancel,
        recovery = recovery,
        guard = guard,
        invulnerability = invulnerability,
        notes = notes.toDomain(),
        aliases = aliases.toDomain(),
        urls = Move.Urls(
            characterWiki = urlsCharacterWiki,
            characterImage = urlsCharacterImage,
            videoId = urlsVideoId,
            hitboxImageList = urlsHitboxImageList.toDomain(),
            moveImageList = urlsMoveImageList.toDomain(),
            wikiUrl = urlsWikiUrl,
        ),
        mkProperties = if (moveId == null) {
            null
        } else {
            Move.MKProperties(
                moveType = moveType,
                cost = cost.orEmpty().toDomain(),
                chip = chip,
                flawlessBlockAdv = flawlessBlockAdv,
                hitCancelAdv = hitCancelAdv,
                blockCancelAdv = blockCancelAdv,
                punish = punish,
            )
        },
    )
}

internal fun SelectAllMb.toDomain(): Move {
    return Move(
        id = id,
        charName = charName,
        name = name,
        input = input,
        damage = damage,
        startup = startup,
        onBlock = onBlock,
        onHit = onHit,
        onCH = onCH,
        active = active,
        cancel = cancel,
        recovery = recovery,
        guard = guard,
        invulnerability = invulnerability,
        notes = notes.toDomain(),
        aliases = aliases.toDomain(),
        urls = Move.Urls(
            characterWiki = urlsCharacterWiki,
            characterImage = urlsCharacterImage,
            videoId = urlsVideoId,
            hitboxImageList = urlsHitboxImageList.toDomain(),
            moveImageList = urlsMoveImageList.toDomain(),
            wikiUrl = urlsWikiUrl,
        ),
        mbProperties = if (moveId == null) {
            null
        } else {
            Move.MBProperties(
                inputInfo = inputInfo,
                subtitle = subtitle,
                minDamage = minDamage,
                property = property_,
                cost = cost,
                attribute = attribute,
                landing = landing,
                overall = overall,
            )
        },
    )
}

internal fun SelectAllBb.toDomain(): Move {
    return Move(
        id = id,
        charName = charName,
        name = name,
        input = input,
        damage = damage,
        startup = startup,
        onBlock = onBlock,
        onHit = onHit,
        onCH = onCH,
        active = active,
        cancel = cancel,
        recovery = recovery,
        guard = guard,
        invulnerability = invulnerability,
        notes = notes.toDomain(),
        aliases = aliases.toDomain(),
        urls = Move.Urls(
            characterWiki = urlsCharacterWiki,
            characterImage = urlsCharacterImage,
            videoId = urlsVideoId,
            hitboxImageList = urlsHitboxImageList.toDomain(),
            moveImageList = urlsMoveImageList.toDomain(),
            wikiUrl = urlsWikiUrl,
        ),
        bbProperties = if (moveId == null) {
            null
        } else {
            Move.BBProperties(
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
                type = type,
            )
        },
    )
}

internal fun SelectAllUni2.toDomain(): Move {
    return Move(
        id = id,
        charName = charName,
        name = name,
        input = input,
        damage = damage,
        startup = startup,
        onBlock = onBlock,
        onHit = onHit,
        onCH = onCH,
        active = active,
        cancel = cancel,
        recovery = recovery,
        guard = guard,
        invulnerability = invulnerability,
        notes = notes.toDomain(),
        aliases = aliases.toDomain(),
        urls = Move.Urls(
            characterWiki = urlsCharacterWiki,
            characterImage = urlsCharacterImage,
            videoId = urlsVideoId,
            hitboxImageList = urlsHitboxImageList.toDomain(),
            moveImageList = urlsMoveImageList.toDomain(),
            wikiUrl = urlsWikiUrl,
        ),
        uni2Properties = if (moveId == null) {
            null
        } else {
            Move.Uni2Properties(
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
            )
        },
    )
}

internal fun SelectAllVsav.toDomain(): Move {
    return Move(
        id = id,
        charName = charName,
        name = name,
        input = input,
        damage = damage,
        startup = startup,
        onBlock = onBlock,
        onHit = onHit,
        onCH = onCH,
        active = active,
        cancel = cancel,
        recovery = recovery,
        guard = guard,
        invulnerability = invulnerability,
        notes = notes.toDomain(),
        aliases = aliases.toDomain(),
        urls = Move.Urls(
            characterWiki = urlsCharacterWiki,
            characterImage = urlsCharacterImage,
            videoId = urlsVideoId,
            hitboxImageList = urlsHitboxImageList.toDomain(),
            moveImageList = urlsMoveImageList.toDomain(),
            wikiUrl = urlsWikiUrl,
        ),
        vsavProperties = if (moveId == null) {
            null
        } else {
            Move.VSAVProperties(
                inputInfo = inputInfo,
                subtitle = subtitle,
                whiteDmg = whiteDmg,
                renda = renda,
                meter = meter,
                reaction = reaction,
                curseTime = curseTime,
            )
        },
    )
}

internal fun SelectAllAvl.toDomain(): Move {
    return Move(
        id = id,
        charName = charName,
        name = name,
        input = input,
        damage = damage,
        startup = startup,
        onBlock = onBlock,
        onHit = onHit,
        onCH = onCH,
        active = active,
        cancel = cancel,
        recovery = recovery,
        guard = guard,
        invulnerability = invulnerability,
        notes = notes.toDomain(),
        aliases = aliases.toDomain(),
        urls = Move.Urls(
            characterWiki = urlsCharacterWiki,
            characterImage = urlsCharacterImage,
            videoId = urlsVideoId,
            hitboxImageList = urlsHitboxImageList.toDomain(),
            moveImageList = urlsMoveImageList.toDomain(),
            wikiUrl = urlsWikiUrl,
        ),
        avlProperties = if (moveId == null) {
            null
        } else {
            Move.AVLProperties(
                chiDamage = chiDamage,
                flow = flow,
            )
        },
    )
}
