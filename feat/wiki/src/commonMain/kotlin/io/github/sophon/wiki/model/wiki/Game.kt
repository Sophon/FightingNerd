package io.github.sophon.wiki.model.wiki

enum class Game(
    val id: String,
    val displayName: String,
    val iconUrl: String,
    val wikiUrl: String,
    val wiki: Wiki,
    val separateCharMoveDownload: Boolean = true,
) {
    Tekken8(
        id = "Tekken_8",
        displayName = "Tekken 8",
        iconUrl = "https://i.imgur.com/Yl6j809.png",
        wikiUrl = "https://wavu.wiki/t/Main_Page",
        wiki = Wiki.Wavu,
    ),

    StreetFighter6(
        id = "Street_Fighter_6",
        displayName = "Street Fighter 6",
        iconUrl = "https://i.imgur.com/N9wYA5K.png",
        wikiUrl = "https://wiki.supercombo.gg/w/Street_Fighter_6",
        wiki = Wiki.SuperCombo,
    ),
    MK1(
        id = "Mortal_Kombat_1",
        displayName = "Mortal Kombat 1",
        iconUrl = "https://i.imgur.com/4OcVxqP.png",
        wikiUrl = "https://srk.shib.live/w/Mortal_Kombat_1",
        wiki = Wiki.SuperCombo,
    ),
    AVL(
        id = "Avatar_Legends",
        displayName = "Avatar Legends: The Fighting Game",
        iconUrl = "https://i.imgur.com/aRyOZfI.png",
        wikiUrl = "https://wiki.supercombo.gg/w/Avatar_Legends",
        wiki = Wiki.SuperCombo,
    ),

    Xko(
        id = "XKO",
        displayName = "2XKO",
        iconUrl = "https://i.imgur.com/XtHOd6T.png",
        wikiUrl = "https://wiki.play2xko.com/en-us/",
        wiki = Wiki.Xko,
        separateCharMoveDownload = false,
    ),

    KoFXV(
        id = "The_King_of_Fighters_XV",
        displayName = "The King of Fighters XV",
        iconUrl = "https://i.imgur.com/Zlin7xi.png",
        wikiUrl = "https://dreamcancel.com/wiki/The_King_of_Fighters_XV",
        wiki = Wiki.DreamCancel,
        separateCharMoveDownload = false,
    ),
    COTW(
        id = "Fatal_Fury:_City_of_the_Wolves",
        displayName = "Fatal Fury: City of the Wolves",
        iconUrl = "https://i.imgur.com/ucbtSgx.png",
        wikiUrl = "https://dreamcancel.com/wiki/Fatal_Fury:_City_of_the_Wolves",
        wiki = Wiki.DreamCancel,
        separateCharMoveDownload = false,
    ),

    GGST(
        id = "GGST",
        displayName = "Guilty Gear -Strive-",
        iconUrl = "https://i.imgur.com/07yTLtj.png",
        wikiUrl = "https://www.dustloop.com/w/GGST",
        wiki = Wiki.DustLoop,
    ),
    DBFZ(
        id = "DBFZ",
        displayName = "Dragon Ball FighterZ",
        iconUrl = "https://i.imgur.com/UuX6ZYv.png",
        wikiUrl = "https://www.dustloop.com/w/DBFZ",
        wiki = Wiki.DustLoop,
    ),
    GBVSR(
        id = "GBVSR",
        displayName = "Granblue Fantasy Versus: Rising",
        iconUrl = "https://i.imgur.com/N6eeM4q.png",
        wikiUrl = "https://www.dustloop.com/w/GBVSR",
        wiki = Wiki.DustLoop,
    ),
    BBCF(
        id = "BBCF",
        displayName = "BlazBlue: Central Fiction",
        iconUrl = "https://i.imgur.com/RYWkC7x.png",
        wikiUrl = "https://www.dustloop.com/w/BBCF",
        wiki = Wiki.DustLoop,
    ),
    MTFS(
        id = "MTFS",
        displayName = "Marvel Tokon: Fighting Souls",
        iconUrl = "https://i.imgur.com/Hps0M3O.png",
        wikiUrl = "https://www.dustloop.com/w/MTFS",
        wiki = Wiki.DustLoop,
    ),

    MBTL(
        id = "MBTL",
        displayName = "Melty Blood: Type Lumina",
        iconUrl = "https://i.imgur.com/E6O7DMi.png",
        wikiUrl = "https://mizuumi.wiki/w/Melty_Blood/MBTL",
        wiki = Wiki.Mizuumi,
        separateCharMoveDownload = false,
    ),
    Uni2(
        id = "UNI2",
        displayName = "Under Night In-Birth II Sys:Celes",
        iconUrl = "https://i.imgur.com/G5RoTij.png",
        wikiUrl = "https://mizuumi.wiki/w/Under_Night_In-Birth/UNI2",
        wiki = Wiki.Mizuumi,
    ),
    VSAV(
        id = "VSAV",
        displayName = "Vampire Savior",
        iconUrl = "https://i.imgur.com/e3xYkHf.png",
        wikiUrl = "https://mizuumi.wiki/w/Vampire_Savior",
        wiki = Wiki.Mizuumi,
        separateCharMoveDownload = false,
    ),
    ROA2(
        id = "ROA2",
        displayName = "Rivals of Aether 2",
        iconUrl = "https://i.imgur.com/DzgFNiQ.png",
        wikiUrl = "https://dragdown.wiki/wiki/RoA2",
        wiki = Wiki.DragDown,
    );

    val shortDisplayName: String
        get() {
            return displayName.substringBefore(":")
        }

    companion object {
        fun fromId(id: String): Game? {
            return entries.find { it.id.equals(id, ignoreCase = true) }
        }

        fun gamesFor(wiki: Wiki): List<Game> {
            return entries.filter { it.wiki == wiki }
        }
    }
}