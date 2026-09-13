package io.github.sophon.wikidustloop.data.db

import io.github.sophon.core.wiki.data.toDomain
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.wikidustloop.data.CharacterEntity
import io.github.sophon.wikidustloop.data.SelectBBForGame
import io.github.sophon.wikidustloop.data.SelectDBFZForGame
import io.github.sophon.wikidustloop.data.SelectGBVSRForGame
import io.github.sophon.wikidustloop.data.SelectGGSTForGame
import io.github.sophon.wikidustloop.data.SelectMTFSForGame
import io.github.sophon.wikidustloop.integration.model.BBCharProperties
import io.github.sophon.wikidustloop.integration.model.DBFZCharProperties
import io.github.sophon.wikidustloop.integration.model.GBVSRCharProperties
import io.github.sophon.wikidustloop.integration.model.GGCharProperties
import io.github.sophon.wikidustloop.integration.model.MTFSCharProperties

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

internal fun SelectGGSTForGame.toDomain(): Character {
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
        gameProperties = GGCharProperties(
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
        ),
    )
    return character
}

internal fun SelectBBForGame.toDomain(): Character {
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
        gameProperties = BBCharProperties(
            preJump = preJump,
            backDash = backDash,
            forwardDash = forwardDash,
        ),
    )
    return character
}

internal fun SelectMTFSForGame.toDomain(): Character {
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
        gameProperties = MTFSCharProperties(
            prejump = prejump,
            backdash = backdash,
            team = team,
        ),
    )
    return character
}

internal fun SelectDBFZForGame.toDomain(): Character {
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
        gameProperties = DBFZCharProperties(
            kiMod = kiMod,
        ),
    )
    return character
}

internal fun SelectGBVSRForGame.toDomain(): Character {
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
        gameProperties = GBVSRCharProperties(
            jump = GBVSRCharProperties.Jump(
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
            closeRange = GBVSRCharProperties.CloseRange(
                l = closeRangeL,
                m = closeRangeM,
                h = closeRangeH,
            ),
        ),
    )
    return character
}
