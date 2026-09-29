package io.github.sophon.wiki.adapter.outbound.sqldelight.dragDown

import io.github.sophon.wiki.application.domain.model.Character
import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.Move
import io.github.sophon.wiki.application.domain.model.gameProperties.Roa2CharProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.Roa2MoveProperties

// normalized, like the service hands them to the adapter
// every property has a distinct value - a swapped column fails the round trip

internal val clairen = Character(
    id = CharacterId("clairen"),
    displayName = "Clairen",
    remoteQueryId = "Clairen",
    wikiUrl = "https://dragdown.wiki/wiki/RoA2/Clairen",
    aliasList = listOf("clairen"),
    gameProperties = Roa2CharProperties(
        dacusSpeedMultiplier = "1.25",
        weight = "95",
        frictionGround = "0.5",
        frictionAir = "0.03",
        dashFrames = "14",
        dashSpeed = "7.5",
        dashAcceleration = "0.35",
        runSpeedMax = "6.5",
        runTurnAcceleration = "1.2",
        runTurnFrames = "12",
        walkAccelerationMax = "0.2",
        walkSpeedMax = "3.25",
        gravity = "0.55",
        hitstunGravity = "0.51",
        fallSpeedMax = "10",
        fastFallSpeed = "14.5",
        airAcceleration = "0.3",
        airSpeedHorizontalMax = "4.25",
        jumpSpeedHorizontalMax = "5",
        fullHopSpeed = "11",
        shortHopSpeed = "6",
        doubleJumpSpeed = "10.5",
        doubleJumpMaxHorizontalSpeed = "4.5",
        airDodgeSpeed = "9",
        airDodgeFriction = "0.75",
        rollSpeed = "8.5",
        shieldSizeMultiplier = "1.0",
        ledgeStandSpeed = "2",
        ledgeRollSpeed = "7",
        ledgeJumpMaxHorizontalAirSpeed = "3.5",
        getupRollSpeed = "8",
        techRollSpeed = "9.5",
        wallJumpSpeedY = "10.25",
        wallJumpSpeedX = "5.5",
    ),
)

internal val clairenJab = Move(
    input = "jab",
    name = "Jab",
    startup = "5",
    onBlock = "-12",
    urls = Move.Urls(wikiUrl = "https://dragdown.wiki/wiki/RoA2/Clairen"),
    gameProperties = Roa2MoveProperties(
        mode = "Default",
        caption = listOf("Plasma-tipped jab"),
        hitboxCaption = listOf("Sweetspot", "Sourspot"),
        startupNotes = "Hitbox appears frame 5",
        totalActiveNotes = "2 hits",
        endlagNotes = "Can jab cancel",
        cancelNotes = listOf("Jab 2 on hit"),
        landingLag = "4",
        landingLagNotes = "Grounded - none",
        iasa = "18",
        iasaNotes = "On whiff",
        totalDuration = "22",
        totalDurationNotes = "Jab 1 only",
        ledgeGrabFrame = "-",
        ledgeGrabFrameNotes = "Grounded",
        hitID = listOf("1", "2"),
        hitMoveID = listOf("jab1", "jab2"),
        hitName = listOf("Jab 1", "Jab 2"),
        hitActive = listOf("5-6", "9-10"),
        customShieldSafety = listOf("-12", "-9"),
        uniqueField = listOf("Tipper"),
        articleID = listOf("plasma_field"),
        notes = "Fast out of shield option",
        advNotes = "Tipper is safer",
    ),
)
