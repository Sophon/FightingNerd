package io.github.sophon.wiki.adapter.outbound.sqldelight.mizuumi

import io.github.sophon.wiki.application.domain.model.Character
import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.Move
import io.github.sophon.wiki.application.domain.model.gameProperties.MBTLMoveProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.Uni2CharProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.Uni2MoveProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.VSAVMoveProperties

// normalized, like the service hands them to the adapter
// every property has a distinct value - a swapped column fails the round trip

internal val arcueid = Character(
    id = CharacterId("arcueid_brunestud"),
    displayName = "Arcueid Brunestud",
    remoteQueryId = "Arcueid Brunestud",
    wikiUrl = "https://mizuumi.wiki/w/Melty_Blood/MBTL/Arcueid_Brunestud",
    aliasList = listOf("arcueid brunestud", "arc"),
)

internal val arcueidStandingA = Move(
    input = "5a",
    name = "5A",
    startup = "5",
    onBlock = "+1",
    urls = Move.Urls(wikiUrl = "https://mizuumi.wiki/w/Melty_Blood/MBTL/Arcueid_Brunestud#5A"),
    gameProperties = MBTLMoveProperties(
        inputInfo = "Chains into itself",
        subtitle = "Standing Light",
        minDamage = "140",
        mizuumiProperty = "Chain",
        cost = "-",
        attribute = "Strike",
        landing = "3",
        overall = "22",
    ),
)

internal val hyde = Character(
    id = CharacterId("hyde"),
    displayName = "Hyde",
    remoteQueryId = "Hyde",
    wikiUrl = "https://mizuumi.wiki/w/Under_Night_In-Birth/UNI2/Hyde",
    aliasList = listOf("hyde"),
    gameProperties = Uni2CharProperties(
        smartSteer = "5A > 5B > 5C > 236A",
        fWalkSpeed = "3.6",
        fWalkSpeedNote = "Average",
        bWalkSpeed = "2.8",
        bWalkSpeedNote = "Slow",
        jumpStartup = "4",
        jumpDuration = "38",
        jumpDurationNote = "Low arc",
        dashStartup = "5",
        iDashSpeed = "12.0",
        iDashSpeedNote = "Fast start",
        dashAccel = "0.4",
        dashAccelNote = "Steady",
        maxDashSpeed = "15.5",
        bDashStartup = "1",
        bDashDuration = "20",
        bDashDurationNote = "Airborne 3-14",
        bDashDistance = "1.8",
        bDashDistanceNote = "Short",
        bDashFullInvulStart = "2",
        bDashFullInvulEnd = "8",
        bDashThrowInvulStart = "3",
        bDashThrowInvulEnd = "14",
        throwWidth = "0.9",
        throwRange = "1.1",
        trait = "Balanced",
        vorpalTrait = "+1 GRD",
    ),
)

internal val hydeStandingA = Move(
    input = "5a",
    name = "5A",
    startup = "6",
    onBlock = "-1",
    urls = Move.Urls(wikiUrl = "https://mizuumi.wiki/w/Under_Night_In-Birth/UNI2/Hyde#5A"),
    gameProperties = Uni2MoveProperties(
        inputInfo = "Chains into 5B",
        subtitle = "Standing Light",
        minDamage = "160",
        cancelWindow = "8-10",
        mizuumiProperty = "Chain",
        cost = "-",
        attribute = "Strike",
        landing = "None",
        overall = "21",
        assaultAdv = "+4",
        blockstun = "12",
        groundHit = "15",
        airHit = "17",
        groundCH = "19",
        airCH = "23",
        hitstop = "9",
        CHstop = "13",
        proration = "80%",
        comboP1 = "100",
        comboP2 = "85",
    ),
)

internal val morrigan = Character(
    id = CharacterId("morrigan"),
    displayName = "Morrigan",
    remoteQueryId = "Morrigan",
    wikiUrl = "https://mizuumi.wiki/w/Vampire_Savior/Morrigan",
    aliasList = listOf("morrigan"),
)

internal val morriganStandingJab = Move(
    input = "5lp",
    name = "5LP",
    startup = "4",
    onBlock = "+2",
    urls = Move.Urls(wikiUrl = "https://mizuumi.wiki/w/Vampire_Savior/Morrigan#5LP"),
    gameProperties = VSAVMoveProperties(
        inputInfo = "Chains into itself",
        subtitle = "Standing Jab",
        whiteDmg = "2",
        renda = "Yes",
        meter = "1",
        reaction = "Small",
        curseTime = "-",
    ),
)
