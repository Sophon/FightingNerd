package io.github.sophon.wikidustloop.data.db

import io.github.sophon.core.wiki.data.toDomain
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.wikidustloop.data.CharacterEntity
import io.github.sophon.wikidustloop.data.SelectBBForGame
import io.github.sophon.wikidustloop.data.SelectDBFZForGame
import io.github.sophon.wikidustloop.data.SelectGBVSRForGame
import io.github.sophon.wikidustloop.data.SelectGGSTForGame
import io.github.sophon.wikidustloop.data.SelectMTFSForGame
import io.github.sophon.wikidustloop.integration.model.BBProperties
import io.github.sophon.wikidustloop.integration.model.DBFZProperties
import io.github.sophon.wikidustloop.integration.model.GBVSRProperties
import io.github.sophon.wikidustloop.integration.model.GGSTProperties
import io.github.sophon.wikidustloop.integration.model.MTFSProperties

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
        gameProperties = GGSTProperties(
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
        gameProperties = BBProperties(
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
        gameProperties = MTFSProperties(
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
        gameProperties = DBFZProperties(
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
        gameProperties = GBVSRProperties(
            jump = GBVSRProperties.Jump(
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
            closeRange = GBVSRProperties.CloseRange(
                l = closeRangeL,
                m = closeRangeM,
                h = closeRangeH,
            ),
        ),
    )
    return character
}
