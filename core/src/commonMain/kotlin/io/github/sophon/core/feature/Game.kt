package io.github.sophon.core.feature

enum class Game(
    val id: String,
    val iconUrl: String,
    val wikiUrl: String,
) {
    Tekken8(
        id = "Tekken_8",
        iconUrl = "TODO",
        wikiUrl = "https://wavu.wiki/t/Main_Page",
    ),
    StreetFighter6(
        id = "Street_Fighter_6",
        iconUrl = "TODO",
        wikiUrl = "https://wiki.supercombo.gg/w/Street_Fighter_6",
    ),
    Xko(
        id = "XKO",
        iconUrl = "TODO",
        wikiUrl = "https://wiki.play2xko.com/en-us/",
    ),
    KoFXV(
        id = "The_King_of_Fighters_XV",
        iconUrl = "TODO",
        wikiUrl = "https://dreamcancel.com/wiki/The_King_of_Fighters_XV",
    ),
    COTW(
        id = "Fatal_Fury:_City_of_the_Wolves",
        iconUrl = "TODO",
        wikiUrl = "https://dreamcancel.com/wiki/Fatal_Fury:_City_of_the_Wolves",
    ),
}