package io.github.sophon.cornerman.screens.moveList.data

import io.github.sophon.core.wiki.domain.model.Move

internal fun Move.toEntity(): MoveEntity {
    return MoveEntity(
        charName = charName,
        id = id,
        input = input,
        name = name,
        damage = damage,
        startup = startup,
        recovery = recovery,
        onBlock = onBlock,
        onHit = onHit,
        onCH = onCH,
        notes = notes.joinToString(";"),
        aliases = aliases.joinToString(";"),
        videoId = videoId,

        t8level = t8Properties?.level,
        t8isHeat = t8Properties?.isHeat,
        t8isPowerCrush = t8Properties?.isPowerCrush,
        t8isHoming = t8Properties?.isHoming,
        t8stance = t8Properties?.stance,
        t8isLowCrush = t8Properties?.isLowCrush,
        t8isHighCrush = t8Properties?.isHighCrush,
    )
}

internal fun MoveEntity.toDomain(): Move {
    return Move(
        charName = charName,
        id = id,
        input = input,
        name = name,
        damage = damage,
        startup = startup,
        recovery = recovery,
        onBlock = onBlock,
        onHit = onHit,
        onCH = onCH,
        notes = notes?.split(";").orEmpty(),
        aliases = aliases?.split(";").orEmpty(),
        videoId = videoId,
        t8Properties = Move.T8Properties(
            isHeat = t8isHeat == true,
            isPowerCrush = t8isPowerCrush == true,
            isHoming = t8isHoming == true,
            stance = t8stance,
        )
    )
}