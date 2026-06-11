package io.github.sophon.fightingnerd.core.data.db

import character.SelectAllBb
import character.SelectAllDbfz
import character.SelectAllGbvsr
import character.SelectAllGgst
import character.SelectAllMk1
import character.SelectAllSf6
import character.SelectAllUni2
import io.github.sophon.core.wiki.model.Character

internal fun character.Character.toDomain(): Character {
    return Character(
        id = id,
        displayName = displayName,
        remoteQueryId = queryName,
        wikiUrl = wikiUrl,
        aliasList = aliasList.toDomain(),
        images = toImages(imageIconUrl, imageBannerUrl),
        hp = hp,
        umo = umo.toDomain(),
    )
}

internal fun SelectAllSf6.toDomain(): Character {
    return Character(
        id = id,
        displayName = displayName,
        remoteQueryId = queryName,
        wikiUrl = wikiUrl,
        aliasList = aliasList.toDomain(),
        images = toImages(imageIconUrl, imageBannerUrl),
        hp = hp,
        umo = umo.toDomain(),
        sf6Properties = if (characterId == null) {
            null
        } else {
            Character.SF6Properties(
                fwdWalkSpd = fwdWalkSpd,
                bwdWalkSpd = bwdWalkSpd,
                fwdDashSpd = fwdDashSpd,
                bwdDashSpd = bwdDashSpd,
                fwdDashDist = fwdDashDist,
                bwdDashDist = bwdDashDist,
                dRushMin = dRushMin,
                dRushBlock = dRushBlock,
                dRushMax = dRushMax,
                throwRange = throwRange,
                throwHurtbox = throwHurtbox,
                jumpSpd = jumpSpd,
                jumpApex = jumpApex,
                fwdJumpDist = fwdJumpDist,
                bwdJumpDist = bwdJumpDist,
            )
        },
    )
}

internal fun SelectAllGgst.toDomain(): Character {
    return Character(
        id = id,
        displayName = displayName,
        remoteQueryId = queryName,
        wikiUrl = wikiUrl,
        aliasList = aliasList.toDomain(),
        images = toImages(imageIconUrl, imageBannerUrl),
        hp = hp,
        umo = umo.toDomain(),
        ggstProperties = if (characterId == null) {
            null
        } else {
            Character.GGSTProperties(
                defense = defense,
                guts = guts,
                guardBalance = guardBalance,
                prejump = prejump,
                bwdDash = bwdDash,
                bwdDashDuration = bwdDashDuration,
                bwdDashInvulnerability = bwdDashInvulnerability,
                bwdDashAirborne = bwdDashAirborne,
                bwdDashDist = bwdDashDist,
                fwdDash = fwdDash,
                jumpDuration = jumpDuration,
                highJumpDuration = highJumpDuration,
                jumpHeight = jumpHeight,
                highJumpHeight = highJumpHeight,
                earliestIAD = earliestIAD,
                adDuration = adDuration,
                abdDuration = abdDuration,
                adDist = adDist,
                abdDist = abdDist,
                movementTension = movementTension,
                jumpTension = jumpTension,
                airDashTension = airDashTension,
                walkSpd = walkSpd,
                bwdWalkSpd = bwdWalkSpd,
                dashInitialSpd = dashInitialSpd,
                dashAcceleration = dashAcceleration,
                dashFriction = dashFriction,
                jumpGravity = jumpGravity,
                highJumpGravity = highJumpGravity,
                boostAttack = boostAttack,
                boostDefense = boostDefense,
            )
        },
    )
}

internal fun SelectAllDbfz.toDomain(): Character {
    return Character(
        id = id,
        displayName = displayName,
        remoteQueryId = queryName,
        wikiUrl = wikiUrl,
        aliasList = aliasList.toDomain(),
        images = toImages(imageIconUrl, imageBannerUrl),
        hp = hp,
        umo = umo.toDomain(),
        dbfzProperties = if (characterId == null) {
            null
        } else {
            Character.DBFZProperties(
                kiMod = kiMod,
            )
        },
    )
}

internal fun SelectAllGbvsr.toDomain(): Character {
    return Character(
        id = id,
        displayName = displayName,
        remoteQueryId = queryName,
        wikiUrl = wikiUrl,
        aliasList = aliasList.toDomain(),
        images = toImages(imageIconUrl, imageBannerUrl),
        hp = hp,
        umo = umo.toDomain(),
        gbvsrProperties = if (characterId == null) {
            null
        } else {
            Character.GBVSRProperties(
                jump = Character.GBVSRProperties.Jump(
                    pre = jumpPre,
                    forwardDistance = jumpForwardDistance,
                    superForwardDistance = jumpSuperForwardDistance,
                    backDistance = jumpBackDistance,
                    superBackDistance = jumpSuperBackDistance,
                    gravity = jumpGravity,
                    superGravity = jumpSuperGravity,
                    superHeight = jumpSuperHeight,
                ),
                backdash = backdash,
                walkSpeed = walkSpeed,
                walkSpeedBack = walkSpeedBack,
                dashInitial = dashInitial,
                dashAcceleration = dashAcceleration,
                closeRange = Character.GBVSRProperties.CloseRange(
                    l = closeRangeL,
                    m = closeRangeM,
                    h = closeRangeH,
                ),
            )
        },
    )
}

internal fun SelectAllMk1.toDomain(): Character {
    return Character(
        id = id,
        displayName = displayName,
        remoteQueryId = queryName,
        wikiUrl = wikiUrl,
        aliasList = aliasList.toDomain(),
        images = toImages(imageIconUrl, imageBannerUrl),
        hp = hp,
        umo = umo.toDomain(),
        mkProperties = if (characterId == null) {
            null
        } else {
            Character.MK1Properties(
                hpMod = hpMod,
                throwDmg = throwDmg,
            )
        },
    )
}

internal fun SelectAllBb.toDomain(): Character {
    return Character(
        id = id,
        displayName = displayName,
        remoteQueryId = queryName,
        wikiUrl = wikiUrl,
        aliasList = aliasList.toDomain(),
        images = toImages(imageIconUrl, imageBannerUrl),
        hp = hp,
        umo = umo.toDomain(),
        bbProperties = if (characterId == null) {
            null
        } else {
            Character.BBProperties(
                preJump = preJump,
                backDash = backDash,
                forwardDash = forwardDash,
            )
        },
    )
}

internal fun SelectAllUni2.toDomain(): Character {
    return Character(
        id = id,
        displayName = displayName,
        remoteQueryId = queryName,
        wikiUrl = wikiUrl,
        aliasList = aliasList.toDomain(),
        images = toImages(imageIconUrl, imageBannerUrl),
        hp = hp,
        umo = umo.toDomain(),
        uni2Properties = if (characterId == null) {
            null
        } else {
            Character.Uni2Properties(
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
            )
        },
    )
}

internal fun toImages(icon: String?, banner: String?): Character.Images? {
    if (icon == null && banner == null) {
        return null
    }
    return Character.Images(iconUrl = icon, bannerUrl = banner)
}
