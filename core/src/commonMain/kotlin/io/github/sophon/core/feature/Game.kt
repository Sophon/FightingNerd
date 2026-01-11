package io.github.sophon.core.feature

enum class Game(
    val id: String,
    val iconUrl: String,
    val wikiUrl: String,
) {
    Tekken8(
        id = "Tekken_8",
        iconUrl = "https://i.imgur.com/Yl6j809.png",
        wikiUrl = "https://wavu.wiki/t/Main_Page",
    ),

    StreetFighter6(
        id = "Street_Fighter_6",
        iconUrl = "https://i.imgur.com/N9wYA5K.png",
        wikiUrl = "https://wiki.supercombo.gg/w/Street_Fighter_6",
    ),
    MK1(
        id = "Mortal_Kombat_1",
        iconUrl = "https://i.imgur.com/4OcVxqP.png",
        wikiUrl = "https://srk.shib.live/w/Mortal_Kombat_1"
    ),

    Xko(
        id = "XKO",
        iconUrl = "https://i.imgur.com/XtHOd6T.png",
        wikiUrl = "https://wiki.play2xko.com/en-us/",
    ),

    KoFXV(
        id = "The_King_of_Fighters_XV",
        iconUrl = "https://i.imgur.com/Zlin7xi.png",
        wikiUrl = "https://dreamcancel.com/wiki/The_King_of_Fighters_XV",
    ),
    COTW(
        id = "Fatal_Fury:_City_of_the_Wolves",
        iconUrl = "https://i.imgur.com/ucbtSgx.png",
        wikiUrl = "https://dreamcancel.com/wiki/Fatal_Fury:_City_of_the_Wolves",
    ),

    GGST(
        id = "GGST",
        iconUrl = "https://i.imgur.com/07yTLtj.png",
        wikiUrl = "https://www.dustloop.com/w/GGST",
    ),
    DBFZ(
        id = "DBFZ",
        iconUrl = "https://i.imgur.com/UuX6ZYv.png",
        wikiUrl = "https://www.dustloop.com/w/DBFZ",
    ),
    GBVSR(
        id = "GBVSR",
        iconUrl = "https://i.imgur.com/N6eeM4q.png",
        wikiUrl = "https://www.dustloop.com/w/GBVSR",
    ),
    BBCF(
        id = "BBCF",
        iconUrl = "https://i.imgur.com/RYWkC7x.png",
        wikiUrl = "https://www.dustloop.com/w/BBCF",
    ),

    MBTL(
        id = "MBTL",
        iconUrl = "https://i.imgur.com/E6O7DMi.png",
        wikiUrl = "https://mizuumi.wiki/w/Melty_Blood/MBTL"
    ),
    Uni2(
        id = "UNI2",
        iconUrl = "https://i.imgur.com/G5RoTij.png",
        wikiUrl = "https://mizuumi.wiki/w/Under_Night_In-Birth/UNI2"
    ),
    VSAV(
        id = "VSAV",
        iconUrl = "https://i.imgur.com/e3xYkHf.png",
        wikiUrl = "https://mizuumi.wiki/w/Vampire_Savior",
    );

    companion object {
        fun fromId(id: String): Game? {
            return entries.find { it.id == id }
        }
    }
}