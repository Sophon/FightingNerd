package io.github.sophon.fightingnerd.screens.moveList.data

import io.github.sophon.core.wiki.domain.model.Move

internal fun Move.toEntity(): MoveEntity {
    return MoveEntity(
        charName = charName,
        id = id,
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

        notes = notes.joinToString(";"),
        aliases = aliases.joinToString(";"),

        urlsChracterWiki = urls.characterWiki,
        urlsVideoId = urls.videoId,
        urlsHitboxImage = urls.hitboxImage,

        t8isHeat = t8Properties?.isHeat,
        t8isPowerCrush = t8Properties?.isPowerCrush,
        t8isHoming = t8Properties?.isHoming,
        t8stance = t8Properties?.stance,
        t8isLowCrush = t8Properties?.isLowCrush,
        t8isHighCrush = t8Properties?.isHighCrush,

        sf6Type = sf6Properties?.type,
        sf6Images = sf6Properties?.images?.joinToString(","),
        sf6Chip = sf6Properties?.chip,
        sf6DmgScaling = sf6Properties?.dmgScaling,
        sf6Total = sf6Properties?.total,
        sf6HitConfirm = sf6Properties?.hitConfirm,
        sf6PunishAdv = sf6Properties?.punishAdv,
        sf6PerfParryAdv = sf6Properties?.perfParryAdv,
        sf6DRcOH = sf6Properties?.DRcOH,
        sf6DRcOB = sf6Properties?.DRcOB,
        sf6DROH = sf6Properties?.DROH,
        sf6DROB = sf6Properties?.DROB,
        sf6HitStun = sf6Properties?.hitStun,
        sf6BlockStun = sf6Properties?.blockStun,
        sf6HitStop = sf6Properties?.hitStop,
        sf6DriveDmgOnBlock = sf6Properties?.driveDmgOnBlock,
        sf6DriveDmgOnHit = sf6Properties?.driveDmgOnHit,
        sf6DriveGain = sf6Properties?.driveGain,
        sf6SuperGainOnHit = sf6Properties?.superGainOnHit,
        sf6SuperGainOnBlock = sf6Properties?.superGainOnBlock,
        sf6Armor = sf6Properties?.armor,
        sf6Airborne = sf6Properties?.airborne,
        sf6JugStart = sf6Properties?.jugStart,
        sf6JugIncrease = sf6Properties?.jugIncrease,
        sf6JugLimit = sf6Properties?.jugLimit,
        sf6ProjectileSpeed = sf6Properties?.projectileSpeed,
        sf6AttackRange = sf6Properties?.attackRange,
    )
}

internal fun MoveEntity.toDomain(): Move {
    return Move(
        charName = charName,
        id = id,
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

        notes = notes?.split(";")?.filter { it.isNotBlank() }.orEmpty(),
        aliases = aliases?.split(";")?.filter { it.isNotBlank() }.orEmpty(),

        urls = Move.Urls(
            characterWiki = urlsChracterWiki,
            videoId = urlsVideoId,
            hitboxImage = urlsHitboxImage,
        ),

        t8Properties = if (t8isHeat != null) {
            Move.T8Properties(
                isHeat = t8isHeat,
                isPowerCrush = t8isPowerCrush == true,
                isHoming = t8isHoming == true,
                stance = t8stance,
                isHighCrush = t8isHighCrush == true,
                isLowCrush = t8isLowCrush == true,
            )
        } else null,
        sf6Properties = if (sf6Type != null) {
            Move.SF6Properties(
                type = sf6Type,
                images = sf6Images?.split(",")?.map { it.trim() },
                chip = sf6Chip,
                dmgScaling = sf6DmgScaling,
                total = sf6Total,
                hitConfirm = sf6HitConfirm,
                punishAdv = sf6PunishAdv,
                perfParryAdv = sf6PerfParryAdv,
                DRcOH = sf6DRcOH,
                DRcOB = sf6DRcOB,
                DROH = sf6DROH,
                DROB = sf6DROB,
                hitStun = sf6HitStun,
                blockStun = sf6BlockStun,
                hitStop = sf6HitStop,
                driveDmgOnBlock = sf6DriveDmgOnBlock,
                driveDmgOnHit = sf6DriveDmgOnHit,
                driveGain = sf6DriveGain,
                superGainOnHit = sf6SuperGainOnHit,
                superGainOnBlock = sf6SuperGainOnBlock,
                armor = sf6Armor,
                airborne = sf6Airborne,
                jugStart = sf6JugStart,
                jugIncrease = sf6JugIncrease,
                jugLimit = sf6JugLimit,
                projectileSpeed = sf6ProjectileSpeed,
                attackRange = sf6AttackRange,
            )
        } else null
    )
}