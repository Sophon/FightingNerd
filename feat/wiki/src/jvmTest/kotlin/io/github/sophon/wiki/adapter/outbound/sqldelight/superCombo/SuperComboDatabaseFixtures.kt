package io.github.sophon.wiki.adapter.outbound.sqldelight.superCombo

import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.Move
import io.github.sophon.wiki.model.game.AVLMoveProperties
import io.github.sophon.wiki.model.game.MKCharProperties
import io.github.sophon.wiki.model.game.MKMoveProperties
import io.github.sophon.wiki.model.game.SF6MoveProperties
import io.github.sophon.wiki.model.game.SFCharProperties
import io.github.sophon.wiki.model.wiki.Game

// normalized, like the service hands them to the adapter
// every property has a distinct value - a swapped column fails the round trip

internal val ryu = Character(
    id = CharacterId(Game.StreetFighter6, "ryu"),
    displayName = "Ryu",
    remoteQueryId = "Ryu",
    wikiUrl = "https://wiki.supercombo.gg/w/Street_Fighter_6/Ryu",
    aliasList = listOf("ryu"),
    gameProperties = SFCharProperties(
        fwdWalkSpd = "0.032",
        bwdWalkSpd = "0.025",
        fwdDashSpd = "19",
        bwdDashSpd = "23",
        fwdDashDist = "1.245",
        bwdDashDist = "0.98",
        dRushMin = "1.3",
        dRushBlock = "2.2",
        dRushMax = "3.24",
        throwRange = "0.8",
        throwHurtbox = "0.4",
        jumpSpd = "4+38+3",
        jumpApex = "2.1",
        fwdJumpDist = "1.72",
        bwdJumpDist = "1.52",
    ),
)

internal val ryuStandingMediumPunch = Move(
    input = "5mp",
    name = "5MP",
    startup = "6",
    onBlock = "+1",
    onHit = "+7",
    urls = Move.Urls(wikiUrl = "https://wiki.supercombo.gg/w/Street_Fighter_6/Ryu#5MP"),
    gameProperties = SF6MoveProperties(
        images = listOf("SF6_Ryu_5MP.png", "SF6_Ryu_5MP_2.png"),
        chip = "0",
        dmgScaling = "20% Starter scaling",
        total = "23",
        hitConfirm = "15",
        punishAdv = "+8",
        perfParryAdv = "-12",
        DRcOH = "+10",
        DRcOB = "+5",
        DROH = "+11",
        DROB = "+7",
        hitStun = "21",
        blockStun = "17",
        hitStop = "12",
        driveDmgOnBlock = "1500",
        driveDmgOnHit = "2000",
        driveGain = "1000",
        superGainOnHit = "600",
        superGainOnBlock = "300",
        armor = "-",
        airborne = "No",
        jugStart = "1",
        jugIncrease = "3",
        jugLimit = "4",
        projectileSpeed = "None",
        attackRange = "1.38",
    ),
)

internal val liuKang = Character(
    id = CharacterId(Game.MK1, "liu_kang"),
    displayName = "Liu Kang",
    remoteQueryId = "Liu Kang",
    wikiUrl = "https://wiki.supercombo.gg/w/Mortal_Kombat_1/Liu_Kang",
    aliasList = listOf("liu kang", "liu"),
    gameProperties = MKCharProperties(
        hpMod = "1.0",
        throwDmg = "120",
    ),
)

internal val liuKangBackOne = Move(
    input = "b1",
    name = "B1",
    startup = "8",
    onBlock = "-3",
    urls = Move.Urls(wikiUrl = "https://wiki.supercombo.gg/w/Mortal_Kombat_1/Liu_Kang/Data#B1"),
    gameProperties = MKMoveProperties(
        cost = listOf("1 Bar", "Meter"),
        chip = "3",
        flawlessBlockAdv = "-15",
        hitCancelAdv = "+18",
        blockCancelAdv = "-2",
        punish = "Yes",
    ),
)

internal val aang = Character(
    id = CharacterId(Game.AVL, "aang"),
    displayName = "Aang",
    remoteQueryId = "Aang",
    wikiUrl = "https://wiki.supercombo.gg/w/Avatar_Legends/Aang",
    aliasList = listOf("aang"),
)

internal val aangStandingLight = Move(
    input = "5l",
    name = "5L",
    startup = "5",
    onBlock = "0",
    urls = Move.Urls(wikiUrl = "https://wiki.supercombo.gg/w"),
    gameProperties = AVLMoveProperties(
        chiDamage = "40",
        flow = "+5",
    ),
)
