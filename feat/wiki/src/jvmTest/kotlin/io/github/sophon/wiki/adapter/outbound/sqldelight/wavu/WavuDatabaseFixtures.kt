package io.github.sophon.wiki.adapter.outbound.sqldelight.wavu

import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.Move
import io.github.sophon.wiki.model.game.Tag2Properties
import io.github.sophon.wiki.model.wiki.Game

// normalized, like the service hands them to the adapter
// neighbouring flags alternate - a swapped column fails the round trip

internal val tag2Hwoarang = Character(
    id = CharacterId(Game.Tag2, "hwoarang"),
    displayName = "Hwoarang",
    remoteQueryId = "Hwoarang",
    wikiUrl = "https://wavu.wiki/t/Hwoarang_(Tag_2)",
    aliasList = listOf("hwoarang", "hwo"),
)

internal val tag2HwoarangRff1 = Move(
    remoteId = "Hwoarang-RFF.1",
    input = "rff.1",
    aliases = listOf("rff1"),
    damage = "13",
    startup = "i10",
    recovery = "RFF",
    onBlock = "+0",
    onHit = "+6",
    onCH = "+6",
    guard = "h",
    notes = listOf("Transition to RFF"),
    urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Hwoarang_movelist_(Tag_2)#Hwoarang-RFF.1"),
    gameProperties = Tag2Properties(
        isHoming = true,
        stance = "RFF",
        isHighCrush = false,
        isLowCrush = true,
        hasWallInteraction = false,
        hasFloorInteraction = true,
    ),
)
