package io.github.sophon.wiki.adapter.outbound.sqldelight

import io.github.sophon.wiki.application.domain.model.Character
import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.Move
import io.github.sophon.wiki.application.domain.model.gameProperties.T8Properties

// normalized, like the service hands them to the adapter

internal val jin = Character(
    id = CharacterId("jin"),
    displayName = "Jin",
    remoteQueryId = "Jin",
    wikiUrl = "https://wavu.wiki/t/Jin",
    aliasList = listOf("jin", "kazama"),
)

internal val asuka = Character(
    id = CharacterId("asuka"),
    displayName = "Asuka",
    remoteQueryId = "Asuka",
    wikiUrl = "https://wavu.wiki/t/Asuka",
    aliasList = listOf("asuka", "kazama"),
)

internal val armorKing = Character(
    id = CharacterId("armor_king"),
    displayName = "Armor King",
    remoteQueryId = "Armor King",
    wikiUrl = "https://wavu.wiki/t/Armor_King",
    aliasList = listOf("armor king", "ak"),
    images = Character.Images(
        iconUrl = "https://wavu.wiki/w/images/Armor_King_icon.png",
    ),
)

internal val solBadguy = Character(
    id = CharacterId("sol_badguy"),
    displayName = "Sol Badguy",
    remoteQueryId = "Sol Badguy",
    wikiUrl = "https://www.dustloop.com/w/GGST/Sol_Badguy",
    aliasList = listOf("sol badguy", "sol"),
)

internal val demonsPaw = Move(
    input = "ff2",
    remoteId = "Jin-f,F+2",
    name = "Demon's Paw",
    startup = "i14~15",
    onBlock = "-8",
    onHit = "+15a (+6)",
    urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Jin_movelist#Jin-f,F+2"),
    gameProperties = T8Properties(hasWallInteraction = true),
)

internal val midLeftPunch = Move(
    input = "df1",
    remoteId = "Jin-df+1",
    name = "Mid Left Punch",
    startup = "i13~14",
    onBlock = "-3",
    onHit = "+4",
    urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Jin_movelist#Jin-df+1"),
)

internal val spinningSidekick = Move(
    input = "b4",
    remoteId = "Jin-b+4",
    name = "Spinning Sidekick",
    startup = "i17~18",
    onBlock = "-7",
    onHit = "+15a",
    urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Jin_movelist#Jin-b+4"),
)

internal val windHookFist = Move(
    input = "cd.df2",
    remoteId = "Jin-CD.df+2",
    name = "Wind Hook Fist",
    startup = "i11~12",
    onBlock = "-10",
    onHit = "+76a (+60)",
    notes = listOf("Launches on hit"),
    aliases = listOf("whf", "cd.2"),
    urls = Move.Urls(
        wikiUrl = "https://wavu.wiki/t/Jin_movelist#Jin-CD.df+2",
        moveImageList = listOf("https://wavu.wiki/w/images/Jin_CD.df%2B2.png"),
    ),
    gameProperties = T8Properties(stance = "CD"),
)

internal val electricWindHookFist = Move(
    input = "cd.df#2",
    remoteId = "Jin-CD.df#2",
    name = "Electric Wind Hook Fist",
    aliases = listOf("ewhf", "whf"),
    urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Jin_movelist#Jin-CD.df#2"),
    gameProperties = T8Properties(stance = "CD"),
)

internal val solFarSlash = Move(
    input = "f.s",
    name = "f.S",
    startup = "10",
    onBlock = "-1",
    urls = Move.Urls(wikiUrl = "https://www.dustloop.com/w/GGST/Sol_Badguy#f.S"),
)
