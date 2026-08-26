package io.github.sophon.wikidragdown.data.db

import io.github.sophon.core.wiki.data.toDomain
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.wikidragdown.data.SelectROA2ForGame
import io.github.sophon.wikidragdown.integration.model.Roa2Properties

internal fun SelectROA2ForGame.toDomain(): Character {
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
        gameProperties = Roa2Properties(
            dacusSpeedMultiplier = dacusSpeedMultiplier,
            weight = weight,
            frictionGround = frictionGround,
            frictionAir = frictionAir,
            dashFrames = dashFrames,
            dashSpeed = dashSpeed,
            dashAcceleration = dashAcceleration,
            runSpeedMax = runSpeedMax,
            runTurnAcceleration = runTurnAcceleration,
            runTurnFrames = runTurnFrames,
            walkAccelerationMax = walkAccelerationMax,
            walkSpeedMax = walkSpeedMax,
            gravity = gravity,
            hitstunGravity = hitstunGravity,
            fallSpeedMax = fallSpeedMax,
            fastFallSpeed = fastFallSpeed,
            airAcceleration = airAcceleration,
            airSpeedHorizontalMax = airSpeedHorizontalMax,
            jumpSpeedHorizontalMax = jumpSpeedHorizontalMax,
            fullHopSpeed = fullHopSpeed,
            shortHopSpeed = shortHopSpeed,
            doubleJumpSpeed = doubleJumpSpeed,
            doubleJumpMaxHorizontalSpeed = doubleJumpMaxHorizontalSpeed,
            airDodgeSpeed = airDodgeSpeed,
            airDodgeFriction = airDodgeFriction,
            rollSpeed = rollSpeed,
            shieldSizeMultiplier = shieldSizeMultiplier,
            ledgeStandSpeed = ledgeStandSpeed,
            ledgeRollSpeed = ledgeRollSpeed,
            ledgeJumpMaxHorizontalAirSpeed = ledgeJumpMaxHorizontalAirSpeed,
            getupRollSpeed = getupRollSpeed,
            techRollSpeed = techRollSpeed,
            wallJumpSpeedY = wallJumpSpeedY,
            wallJumpSpeedX = wallJumpSpeedX,
        ),
    )
    return character
}
