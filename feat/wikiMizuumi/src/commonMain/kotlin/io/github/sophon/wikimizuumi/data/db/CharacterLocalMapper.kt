package io.github.sophon.wikimizuumi.data.db

import io.github.sophon.core.wiki.data.toDomain
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.wikimizuumi.data.CharacterEntity
import io.github.sophon.wikimizuumi.data.SelectUni2ForGame
import io.github.sophon.wikimizuumi.integration.model.Uni2Properties

internal fun CharacterEntity.toDomain(): Character {
    val character = Character(
        id = id,
        displayName = displayName,
        remoteQueryId = remoteQueryId,
        wikiUrl = wikiUrl,
        aliasList = aliases.toDomain(),
        images = Character.Images(
            iconId = imagesIconId,
            iconUrl = imagesIconUrl,
            bannerUrl = imagesBannerUrl,
        ),
        hp = hp,
        umo = umo.toDomain(),
    )
    return character
}

internal fun SelectUni2ForGame.toDomain(): Character {
    val character = Character(
        id = id,
        displayName = displayName,
        remoteQueryId = remoteQueryId,
        wikiUrl = wikiUrl,
        aliasList = aliases.toDomain(),
        images = Character.Images(
            iconId = imagesIconId,
            iconUrl = imagesIconUrl,
            bannerUrl = imagesBannerUrl,
        ),
        hp = hp,
        umo = umo.toDomain(),
        gameProperties = Uni2Properties(
            smartSteer = smartSteer,
            fWalkSpeed = fWalkSpeed,
            fWalkSpeedNote = fWalkSpeedNote,
            bWalkSpeed = bWalkSpeed,
            bWalkSpeedNote = bWalkSpeedNote,
            jumpStartup = jumpStartup,
            jumpDuration = jumpDuration,
            jumpDurationNote = jumpDurationNote,
            dashStartup = dashStartup,
            iDashSpeed = iDashSpeed,
            iDashSpeedNote = iDashSpeedNote,
            dashAccel = dashAccel,
            dashAccelNote = dashAccelNote,
            maxDashSpeed = maxDashSpeed,
            bDashStartup = bDashStartup,
            bDashDuration = bDashDuration,
            bDashDurationNote = bDashDurationNote,
            bDashDistance = bDashDistance,
            bDashDistanceNote = bDashDistanceNote,
            bDashFullInvulStart = bDashFullInvulStart,
            bDashFullInvulEnd = bDashFullInvulEnd,
            bDashThrowInvulStart = bDashThrowInvulStart,
            bDashThrowInvulEnd = bDashThrowInvulEnd,
            throwWidth = throwWidth,
            throwRange = throwRange,
            trait = trait,
            vorpalTrait = vorpalTrait,
        ),
    )
    return character
}
