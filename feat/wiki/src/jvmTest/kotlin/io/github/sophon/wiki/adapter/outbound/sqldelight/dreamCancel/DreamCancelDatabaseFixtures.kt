package io.github.sophon.wiki.adapter.outbound.sqldelight.dreamCancel

import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.Move
import io.github.sophon.wiki.model.game.COTWMoveProperties
import io.github.sophon.wiki.model.game.KOF15MoveProperties
import io.github.sophon.wiki.model.wiki.Game

// normalized, like the service hands them to the adapter

internal val kyo = Character(
    id = CharacterId(Game.KoFXV, "kyo_kusanagi"),
    displayName = "Kyo Kusanagi",
    remoteQueryId = "Kyo Kusanagi",
    wikiUrl = "https://dreamcancel.com/wiki/The_King_of_Fighters_XV/Kyo_Kusanagi",
    aliasList = listOf("kyo kusanagi", "kyo"),
)

internal val kyoCloseC = Move(
    input = "cl.c",
    name = "Close C",
    startup = "4",
    onBlock = "+1",
    urls = Move.Urls(wikiUrl = "https://dreamcancel.com/wiki/The_King_of_Fighters_XV/Kyo_Kusanagi"),
    gameProperties = KOF15MoveProperties(stun = "50"),
)

internal val terry = Character(
    id = CharacterId(Game.COTW, "terry_bogard"),
    displayName = "Terry Bogard",
    remoteQueryId = "Terry Bogard",
    wikiUrl = "https://dreamcancel.com/wiki/Fatal_Fury:_City_of_the_Wolves/Terry_Bogard",
    aliasList = listOf("terry bogard", "terry"),
)

internal val terryStandingC = Move(
    input = "5c",
    name = "Standing C",
    startup = "9",
    onBlock = "-4",
    urls = Move.Urls(wikiUrl = "https://dreamcancel.com/wiki/Fatal_Fury:_City_of_the_Wolves/Terry_Bogard"),
    gameProperties = COTWMoveProperties(revDamage = "20"),
)
