package io.github.sophon.wiki.adapter.outbound.sqldelight.dustLoop

import io.github.sophon.wiki.application.domain.model.Character
import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.Move
import io.github.sophon.wiki.application.domain.model.gameProperties.BBCharProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.BBMoveProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.DBFZCharProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.DBFZMoveProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.GBVSRCharProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.GBVSRMoveProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.GGCharProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.GGMoveProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.MTFSCharProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.MTFSMoveProperties

// normalized, like the service hands them to the adapter
// every property has a distinct value - a swapped column fails the round trip

internal val kyKiske = Character(
    id = CharacterId("ky_kiske"),
    displayName = "Ky Kiske",
    remoteQueryId = "Ky Kiske",
    wikiUrl = "https://www.dustloop.com/w/GGST/Ky_Kiske",
    aliasList = listOf("ky kiske", "ky"),
    gameProperties = GGCharProperties(
        defense = "1.00",
        guts = "2",
        guardBalance = "0",
        prejump = "4",
        bwdDash = "23",
        bwdDashDuration = "19",
        bwdDashInvulnerability = "1-7",
        bwdDashAirborne = "5-15",
        bwdDashDist = "1.65",
        fwdDash = "Run",
        jumpDuration = "45",
        highJumpDuration = "53",
        jumpHeight = "2.62",
        highJumpHeight = "3.34",
        earliestIAD = "10",
        adDuration = "20",
        abdDuration = "12",
        adDist = "2.35",
        abdDist = "1.10",
        movementTension = "14",
        jumpTension = "2.5",
        airDashTension = "6",
        walkSpd = "6.60",
        bwdWalkSpd = "4.60",
        dashInitialSpd = "8.25",
        dashAcceleration = "0.35",
        dashFriction = "1.20",
        jumpGravity = "0.98",
        highJumpGravity = "0.88",
        boostAttack = "1.03",
        boostDefense = "0.97",
    ),
)

internal val kyFarSlash = Move(
    input = "f.s",
    name = "f.S",
    startup = "10",
    onBlock = "-1",
    urls = Move.Urls(wikiUrl = "https://www.dustloop.com/w/GGST/Ky_Kiske#f.S"),
    gameProperties = GGMoveProperties(
        riscGain = "4",
        riscLoss = "20",
        wallDamage = "350",
        inputTension = "5",
        chipRatio = "0",
        otgType = "OTG",
        prorate = "90%",
        level = "3",
    ),
)

internal val android18 = Character(
    id = CharacterId("android_18"),
    displayName = "Android 18",
    remoteQueryId = "Android 18",
    wikiUrl = "https://www.dustloop.com/w/DBFZ/Android_18",
    aliasList = listOf("android 18", "18"),
    gameProperties = DBFZCharProperties(kiMod = "1.00"),
)

internal val android18StandingLight = Move(
    input = "5l",
    name = "5L",
    startup = "7",
    onBlock = "-2",
    urls = Move.Urls(wikiUrl = "https://www.dustloop.com/w/DBFZ/Android_18#5L"),
    gameProperties = DBFZMoveProperties(
        attribute = "Mid",
        smash = "No",
        kiGain = "300",
        prorate = "80%",
        blockStun = "11",
        groundHit = "15",
        airHit = "18",
        level = "1",
    ),
)

internal val gran = Character(
    id = CharacterId("gran"),
    displayName = "Gran",
    remoteQueryId = "Gran",
    wikiUrl = "https://www.dustloop.com/w/GBVSR/Gran",
    aliasList = listOf("gran"),
    gameProperties = GBVSRCharProperties(
        jump = GBVSRCharProperties.Jump(
            pre = "4",
            forwardDistance = "196",
            superForwardDistance = "260",
            backDistance = "176",
            superBackDistance = "232",
            gravity = "0.85",
            superGravity = "0.75",
            superHeight = "310",
        ),
        backdash = "21",
        walkSpeed = "4.4",
        walkSpeedBack = "3.5",
        dashInitial = "10.0",
        dashAcceleration = "0.55",
        closeRange = GBVSRCharProperties.CloseRange(
            l = "76",
            m = "95",
            h = "120",
        ),
    ),
)

internal val granCloseMedium = Move(
    input = "c.m",
    name = "c.M",
    startup = "7",
    onBlock = "-3",
    urls = Move.Urls(wikiUrl = "https://www.dustloop.com/w/GBVSR/Gran#c.M"),
    gameProperties = GBVSRMoveProperties(
        meter = "5%",
        level = "2",
        cooldown = "-",
        cls = "Normal",
    ),
)

internal val ragna = Character(
    id = CharacterId("ragna_the_bloodedge"),
    displayName = "Ragna the Bloodedge",
    remoteQueryId = "Ragna the Bloodedge",
    wikiUrl = "https://www.dustloop.com/w/BBCF/Ragna_the_Bloodedge",
    aliasList = listOf("ragna the bloodedge", "ragna"),
    gameProperties = BBCharProperties(
        preJump = "4",
        backDash = "5-9 Invuln",
        forwardDash = "Run",
    ),
)

internal val ragnaStandingB = Move(
    input = "5b",
    name = "5B",
    startup = "8",
    onBlock = "-3",
    urls = Move.Urls(wikiUrl = "https://www.dustloop.com/w/BBCF/Ragna_the_Bloodedge#5B"),
    gameProperties = BBMoveProperties(
        onODR = "No",
        attribute = "B",
        p1 = "100",
        p2 = "80",
        starter = "Yes",
        level = "2",
        blockstun = "13",
        groundHit = "15",
        airHit = "16",
        groundCH = "20",
        airCH = "21",
        blockstop = "11",
        hitstop = "10",
        chStop = "12",
        cancelTiming = "Rev, Jump",
    ),
)

internal val arizona = Character(
    id = CharacterId("arizona"),
    displayName = "Arizona",
    remoteQueryId = "Arizona",
    wikiUrl = "https://www.dustloop.com/w/MTFS/Arizona",
    aliasList = listOf("arizona"),
    gameProperties = MTFSCharProperties(
        prejump = "3",
        backdash = "20",
        team = "Prey",
    ),
)

internal val arizonaStandingA = Move(
    input = "5a",
    name = "5A",
    startup = "5",
    onBlock = "+1",
    urls = Move.Urls(wikiUrl = "https://www.dustloop.com/w/MTFS/Arizona#5A"),
    gameProperties = MTFSMoveProperties(
        simpleInput = "A",
        level = "1",
        prorate = "85%",
        meterGain = "8",
        untechAmount = "14",
        hitboxCaption = "Hooves out front",
    ),
)
