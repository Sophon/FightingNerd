package io.github.sophon.wiki.model.wiki

//TODO: refactor to a different model
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
    Tag2(
        id = "Tag_2",
        displayName = "Tekken Tag Tournament 2",
        iconUrl = "https://wavu.wiki/w/images/thumb/7/7f/Tekken_Tag_Tournament_2.png/500px-Tekken_Tag_Tournament_2.png",
        wikiUrl = "https://wavu.wiki/t/Tekken_Tag_Tournament_2_(Main_Page)",
        wiki = Wiki.Wavu,
    ),
    Tekken5(
        id = "Tekken_5",
        displayName = "Tekken 5",
        iconUrl = "https://wavu.wiki/w/images/thumb/8/84/Tekken_5_Dark_Resurrection.png/615px-Tekken_5_Dark_Resurrection.png",
        wikiUrl = "https://wavu.wiki/t/Tekken_5:_Dark_Resurrection_(Main_Page)",
        wiki = Wiki.Wavu,
    ),

    StreetFighter6(
        id = "Street_Fighter_6",
        displayName = "Street Fighter 6",
        iconUrl = "https://wiki.supercombo.gg/images/thumb/4/42/SF6_Logo.png/300px-SF6_Logo.png",
        wikiUrl = "https://wiki.supercombo.gg/w/Street_Fighter_6",
        wiki = Wiki.SuperCombo,
    ),
    MK1(
        id = "Mortal_Kombat_1",
        displayName = "Mortal Kombat 1",
        iconUrl = "https://wiki.supercombo.gg/images/thumb/7/73/MK1_Logo.png/300px-MK1_Logo.png",
        wikiUrl = "https://srk.shib.live/w/Mortal_Kombat_1",
        wiki = Wiki.SuperCombo,
    ),
    AVL(
        id = "Avatar_Legends",
        displayName = "Avatar Legends: The Fighting Game",
        iconUrl = "https://wiki.supercombo.gg/images/thumb/5/5e/Avatar_Legends_Logo.png/1599px-Avatar_Legends_Logo.png",
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
        iconUrl = "https://dreamcancel.com/w/images/thumb/7/7e/KOF_XV_Logo.png/400px-KOF_XV_Logo.png",
        wikiUrl = "https://dreamcancel.com/wiki/The_King_of_Fighters_XV",
        wiki = Wiki.DreamCancel,
        separateCharMoveDownload = false,
    ),
    COTW(
        id = "Fatal_Fury:_City_of_the_Wolves",
        displayName = "Fatal Fury: City of the Wolves",
        iconUrl = "https://dreamcancel.com/w/images/thumb/b/b2/FF_CotW_logo.png/400px-FF_CotW_logo.png",
        wikiUrl = "https://dreamcancel.com/wiki/Fatal_Fury:_City_of_the_Wolves",
        wiki = Wiki.DreamCancel,
        separateCharMoveDownload = false,
    ),

    GGST(
        id = "GGST",
        displayName = "Guilty Gear -Strive-",
        iconUrl = "https://www.dustloop.com/wiki/images/5/55/GGST_Logo.png",
        wikiUrl = "https://www.dustloop.com/w/GGST",
        wiki = Wiki.DustLoop,
    ),
    DBFZ(
        id = "DBFZ",
        displayName = "Dragon Ball FighterZ",
        iconUrl = "https://www.dustloop.com/wiki/images/0/06/DBFZ_Logo.png",
        wikiUrl = "https://www.dustloop.com/w/DBFZ",
        wiki = Wiki.DustLoop,
    ),
    GBVSR(
        id = "GBVSR",
        displayName = "Granblue Fantasy Versus: Rising",
        iconUrl = "https://www.dustloop.com/wiki/images/5/52/GBVSR_Logo.png",
        wikiUrl = "https://www.dustloop.com/w/GBVSR",
        wiki = Wiki.DustLoop,
    ),
    BBCF(
        id = "BBCF",
        displayName = "BlazBlue: Central Fiction",
        iconUrl = "https://www.dustloop.com/wiki/images/5/5e/BBCF_Logo.png",
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
        iconUrl = "https://mizuumi.wiki/images/1/18/MBTL_Logo.png",
        wikiUrl = "https://mizuumi.wiki/w/Melty_Blood/MBTL",
        wiki = Wiki.Mizuumi,
        separateCharMoveDownload = false,
    ),
    Uni2(
        id = "UNI2",
        displayName = "Under Night In-Birth II Sys:Celes",
        iconUrl = "https://mizuumi.wiki/images/thumb/f/fe/UNI2_Logo.png/150px-UNI2_Logo.png",
        wikiUrl = "https://mizuumi.wiki/w/Under_Night_In-Birth/UNI2",
        wiki = Wiki.Mizuumi,
    ),
    VSAV(
        id = "VSAV",
        displayName = "Vampire Savior",
        iconUrl = "https://mizuumi.wiki/images/thumb/a/a1/VSAV_Logo.png/150px-VSAV_Logo.png",
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