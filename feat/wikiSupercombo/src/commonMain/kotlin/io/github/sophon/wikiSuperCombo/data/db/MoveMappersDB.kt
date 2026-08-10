package io.github.sophon.wikiSuperCombo.data.db

import io.github.sophon.core.wiki.data.toDomain
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wikiSuperCombo.data.SelectAVLByCharacter
import io.github.sophon.wikiSuperCombo.data.SelectMK1ByCharacter
import io.github.sophon.wikiSuperCombo.data.SelectSF6ByCharacter

internal fun SelectSF6ByCharacter.toDomain(): Move {
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
        sf6Properties = Move.SF6Properties(
            type = type?.let { runCatching { Move.SF6Properties.Type.valueOf(it) }.getOrNull() },
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
        ),
    )
    return move
}

internal fun SelectMK1ByCharacter.toDomain(): Move {
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
        mkProperties = Move.MKProperties(
            moveType = moveType,
            cost = cost.orEmpty().toDomain(),
            chip = chip,
            flawlessBlockAdv = flawlessBlockAdv,
            hitCancelAdv = hitCancelAdv,
            blockCancelAdv = blockCancelAdv,
            punish = punish,
        ),
    )
    return move
}

internal fun SelectAVLByCharacter.toDomain(): Move {
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
        avlProperties = Move.AVLProperties(
            chiDamage = chiDamage,
            flow = flow,
        ),
    )
    return move
}
