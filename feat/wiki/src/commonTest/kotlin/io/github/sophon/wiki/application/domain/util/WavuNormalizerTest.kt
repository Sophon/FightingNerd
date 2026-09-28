package io.github.sophon.wiki.application.domain.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wiki.application.domain.model.gameProperties.T8Properties
import kotlin.test.Test

class WavuNormalizerTest {

    //region id
    @Test
    fun `id is lowercased with words joined by underscore`() {
        //given
        val move = WavuMoveSource.ffn2
        val expected = "armor_king-ffn2"

        //when
        val result = move.normalizeT8()

        //then
        assertThat(result.id).isEqualTo(expected)
    }

    @Test
    fun `id notation is cleaned`() {
        //given
        val move = WavuMoveSource.df2
        val expected = "jack-8-df2"

        //when
        val result = move.normalizeT8()

        //then
        assertThat(result.id).isEqualTo(expected)
    }
    //endregion

    //region aliases
    @Test
    fun `input alone produces no aliases`() {
        //given
        val move = WavuMoveSource.konvictKick
        val expected = emptyList<String>()

        //when
        val result = move.normalizeT8()

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `multi-word alias keeps its space`() {
        //given
        val move = WavuMoveSource.shiningWizard
        val expected = listOf("shining wizard")

        //when
        val result = move.normalizeT8()

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `aliases are lowercased`() {
        //given
        val move = WavuMoveSource.cancans
        val expected = listOf("can cans", "cancan")

        //when
        val result = move.normalizeT8()

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `crouch dash gets cd variants`() {
        //given
        val move = WavuMoveSource.whf
        val expected = listOf("whf", "cd.2", "cd2", "cddf2")

        //when
        val result = move.normalizeT8()

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `just-frame crouch dash gets cd variants`() {
        //given
        val move = WavuMoveSource.ewhf
        val expected = listOf("ewhf", "electric", "ecd2", "cd#2", "fndf#2", "cddf#2")

        //when
        val result = move.normalizeT8()

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `motion aliases are cleaned`() {
        //given
        val move = WavuMoveSource.wgk
        val expected = listOf(
            "cd.3",
            "cd3",
            "fndf3",
            "df3df3",
            "wgsdf3",
        )

        //when
        val result = move.normalizeT8()

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `sidestep gets a dotless variant`() {
        //given
        val move = WavuMoveSource.ss4
        val expected = listOf("ss4")

        //when
        val result = move.normalizeT8()

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `heat smash gets its nicknames`() {
        //given
        val move = WavuMoveSource.heatSmash
        val expected = listOf("hs", "heatsmash")

        //when
        val result = move.normalizeT8()

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `hfc gets a fc variant`() {
        //given
        val move = WavuMoveSource.yakouga
        val expected = listOf("hfcdb1+2", "fcdb1+2", "fc1+2")

        //when
        val result = move.normalizeT8()

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `three-letter stances get dotless variants`() {
        //given
        val move = WavuMoveSource.unsd4
        val expected = listOf("fn4", "wds.4", "wds4", "unsd4")

        //when
        val result = move.normalizeT8()

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }
    //endregion

    //region stance
    @Test
    fun `stance is detected from input`() {
        //given
        val move = WavuMoveSource.bad4
        val expected = "BAD"

        //when
        val result = move.normalizeT8()

        //then
        assertThat(result.t8Properties?.stance).isEqualTo(expected)
    }

    @Test
    fun `input without stance has none`() {
        //given
        val move = WavuMoveSource.matterhorn

        //when
        val result = move.normalizeT8()

        //then
        assertThat(result.t8Properties?.stance).isNull()
    }

    @Test
    fun `otg is not a stance`() {
        //given
        val move = WavuMoveSource.stomp

        //when
        val result = move.normalizeT8()

        //then
        assertThat(result.t8Properties?.stance).isNull()
    }

    @Test
    fun `backturn is a stance`() {
        //given
        val move = WavuMoveSource.moonsault
        val expected = "BT"

        //when
        val result = move.normalizeT8()

        //then
        assertThat(result.t8Properties?.stance).isEqualTo(expected)
    }

    @Test
    fun `stance is detected from aliases`() {
        //given
        val move = WavuMoveSource.manjiBackfistShredder
        val expected = "BT"

        //when
        val result = move.normalizeT8()

        //then
        assertThat(result.t8Properties?.stance).isEqualTo(expected)
    }
    //endregion

    //region heat
    @Test
    fun `heat is detected from aliases`() {
        //given
        val move = WavuMoveSource.tempestBlaster
        val expected = true

        //when
        val result = move.normalizeT8()

        //then
        assertThat(result.t8Properties?.isHeat).isEqualTo(expected)
    }
    //endregion

    //region input
    @Test
    fun `heat crouch dash input is cleaned`() {
        //given
        val move = WavuMoveSource.heatMist
        val expectedInput = "h.cd.1+2"
        val expectedAliases = listOf("h.bad.f1+2", "h.cd1+2")

        //when
        val result = move.normalizeT8()

        //then
        assertThat(result.input).isEqualTo(expectedInput)
        assertThat(result.aliases).isEqualTo(expectedAliases)
    }

    @Test
    fun `input assembled from parents is cleaned`() {
        //given
        val move = WavuMoveSource.secondStomp
        val expected = "otg.d44"

        //when
        val result = move.normalizeT8()

        //then
        assertThat(result.input).isEqualTo(expected)
    }
    //endregion

    //region full move
    @Test
    fun `simple move only gets its id normalized`() {
        //given
        val move = WavuMoveSource.jab
        val expected = move.copy(id = "armor_king-1")

        //when
        val result = move.normalizeT8()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `heat engager note makes a heat move`() {
        //given
        val move = WavuMoveSource.darkElbowHook
        val expected = move.copy(
            id = "armor_king-f21",
            input = "f21",
            gameProperties = T8Properties(
                isHeat = true,
                hasWallInteraction = true,
            ),
        )

        //when
        val result = move.normalizeT8()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `stance move gets stance and dotless alias`() {
        //given
        val move = WavuMoveSource.shadowPress
        val expected = move.copy(
            id = "armor_king-bad.db1+2",
            input = "bad.db1+2",
            aliases = listOf("baddb1+2"),
            gameProperties = T8Properties(
                stance = "BAD",
                isLowCrush = true,
            ),
        )

        //when
        val result = move.normalizeT8()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `running input becomes wr and drops the matching alias`() {
        //given
        val move = WavuMoveSource.akSW
        val expected = move.copy(
            id = "armor_king-wr2+4",
            input = "wr2+4",
            aliases = listOf("shining wizard"),
        )

        //when
        val result = move.normalizeT8()

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion
}

private val Move.t8Properties: T8Properties?
    get() = gameProperties as? T8Properties

/**
 * Moves as the Wavu adapter maps them - raw notation, heat and stance not derived yet.
 */
private object WavuMoveSource {
    val ffn2 = Move(
        characterId = "armor-king",
        id = "Armor King-f,f,n,2",
        name = "Underhanded",
        input = "f,f,n,2",
        notes = listOf("Transition to BAD on hit with F (+8/+13)", "cs8~37"),
        urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Armor_King_movelist#Armor_King-f,f,n,2"),
        gameProperties = T8Properties(isHighCrush = true),
    )
    val df2 = Move(
        characterId = "jack-8",
        id = "Jack-8-df+2",
        name = "Programmed Uppercut",
        input = "df+2",
        urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Jack-8_movelist#Jack-8-df+2"),
        gameProperties = T8Properties(),
    )
    val konvictKick = Move(
        characterId = "king",
        id = "King-f,F+4",
        name = "Konvict Kick",
        input = "f,F+4",
        urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/King_movelist#King-f,F+4"),
        gameProperties = T8Properties(hasWallInteraction = true),
    )
    val shiningWizard = Move(
        characterId = "king",
        id = "King-f,f,F+2+4",
        name = "Tomahawk",
        input = "f,f,F+2+4",
        aliases = listOf("Shining Wizard"),
        isThrow = true,
        urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/King_movelist#King-f,f,F+2+4"),
        gameProperties = T8Properties(isHoming = true),
    )
    val cancans = Move(
        characterId = "asuka",
        id = "Asuka-d+3+4",
        name = "Double Lift Kicks",
        input = "d+3+4",
        aliases = listOf("Can Cans", "Cancan"),
        urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Asuka_movelist#Asuka-d+3+4"),
        gameProperties = T8Properties(isLowCrush = true),
    )
    val whf = Move(
        characterId = "jin",
        id = "Jin-CD.df+2",
        name = "Wind Hook Fist",
        input = "CD.df+2",
        aliases = listOf("WHF", "f,n,d,df+2"),
        urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Jin_movelist#Jin-CD.df+2"),
        gameProperties = T8Properties(hasWallInteraction = true),
    )
    val ewhf = Move(
        characterId = "jin",
        id = "Jin-CD.df#2",
        name = "Electric Wind Hook Fist",
        input = "CD.df#2",
        aliases = listOf("EWHF", "Electric", "ECD+2", "f,n,d,df#2", "f,n,df#2"),
        urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Jin_movelist#Jin-CD.df#2"),
        gameProperties = T8Properties(hasWallInteraction = true),
    )
    val wgk = Move(
        characterId = "reina",
        id = "Reina-WGS.DF+3",
        name = "War God Kick",
        input = "WGS.df+3",
        aliases = listOf("f,n,d,DF+3", "f,n,DF+3", "df+3,df+3"),
        urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Reina_movelist#Reina-WGS.DF+3"),
        gameProperties = T8Properties(),
    )
    val ss4 = Move(
        characterId = "claudio",
        id = "Claudio-SS.4",
        name = "Luxuria",
        input = "SS.4",
        urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Claudio_movelist#Claudio-SS.4"),
        gameProperties = T8Properties(),
    )
    val heatSmash = Move(
        characterId = "bryan",
        id = "Bryan-H.2+3",
        name = "Notorious Monster",
        input = "H.2+3",
        notes = listOf("Heat Smash", "Reversal Break", "Spike", "Transition to attack throw on hit", "js9~"),
        isThrow = true,
        urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Bryan_movelist#Bryan-H.2+3"),
        gameProperties = T8Properties(isLowCrush = true),
    )
    val matterhorn = Move(
        characterId = "lili",
        id = "Lili-d+3+4",
        name = "Matterhorn Ascension",
        input = "d+3+4",
        aliases = listOf("Matterhorn"),
        notes = listOf("Tornado", "Evasive, can go under some mids and highs", "fs14~44"),
        urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Lili_movelist#Lili-d+3+4"),
        gameProperties = T8Properties(),
    )
    val yakouga = Move(
        characterId = "kunimitsu",
        id = "Kunimitsu-hFC.1+2",
        name = "Yakouga",
        input = "hFC.1+2",
        aliases = listOf("hFC.db+1+2"),
        urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Kunimitsu_movelist#Kunimitsu-hFC.1+2"),
        gameProperties = T8Properties(isHighCrush = true),
    )
    val unsd4 = Move(
        characterId = "reina",
        id = "Reina-UNS.d+4",
        name = "Santei Gedan-Geri",
        input = "UNS.d+4",
        aliases = listOf("f,n,4", "WDS.4"),
        urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Reina_movelist#Reina-UNS.d+4"),
        gameProperties = T8Properties(isHighCrush = true),
    )
    val bad4 = Move(
        characterId = "armor-king",
        id = "Armor King-BAD.4",
        name = "Bandido Snatch",
        input = "BAD.4",
        urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Armor_King_movelist#Armor_King-BAD.4"),
        gameProperties = T8Properties(),
    )
    val stomp = Move(
        characterId = "armor-king",
        id = "Armor King-OTG.d+4",
        input = "OTG.d+4",
        urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Armor_King_movelist#Armor_King-OTG.d+4"),
        gameProperties = T8Properties(),
    )
    val secondStomp = Move(
        characterId = "armor-king",
        id = "Armor King-OTG.d+4,4",
        input = "OTG.d+4,4",
        urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Armor_King_movelist#Armor_King-OTG.d+4,4"),
        gameProperties = T8Properties(hasFloorInteraction = true),
    )
    val moonsault = Move(
        characterId = "armor-king",
        id = "Armor King-BT.1+4",
        name = "Moonsault Drop",
        input = "BT.1+4",
        urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Armor_King_movelist#Armor_King-BT.1+4"),
        gameProperties = T8Properties(isLowCrush = true),
    )
    val manjiBackfistShredder = Move(
        characterId = "yoshimitsu",
        id = "Yoshimitsu-f+2,1",
        name = "Manji Backfist Shredder",
        input = "f+2,1",
        aliases = listOf("1SS.f+2,1", "f+2,1SS.1", "BT.2,1"),
        urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Yoshimitsu_movelist#Yoshimitsu-f+2,1"),
        gameProperties = T8Properties(),
    ) //has Stance in alt inputs
    val tempestBlaster = Move(
        characterId = "kazuya",
        id = "Kazuya-DVK.f,n,d,df+3",
        name = "Tempest Blaster",
        input = "DVK.f,n,d,df+3",
        aliases = listOf("DVK.cd+3", "H.f,n,d,df+3", "H.cd+3"),
        notes = listOf(
            "Consumes 180F of remaining Heat time",
            "Extension available only on hit or block",
            "Extension can be canceled with b",
            "16 chip damage on block",
            "js9~",
        ),
        urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Kazuya_movelist#Kazuya-DVK.f,n,d,df+3"),
        gameProperties = T8Properties(isLowCrush = true),
    ) //has Heat in alt inputs
    val heatMist = Move(
        characterId = "armor-king",
        id = "Armor King-H.f,n,d,df+1+2",
        name = "Malice Mist: Villain",
        input = "H.f,n,d,df+1+2",
        aliases = listOf("H.BAD.f+1+2"),
        urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Armor_King_movelist#Armor_King-H.f,n,d,df+1+2"),
        gameProperties = T8Properties(),
    )
    val jab = Move(
        characterId = "armor-king",
        id = "Armor King-1",
        name = "Jab",
        input = "1",
        damage = "5",
        startup = "i10",
        recovery = "r19",
        onBlock = "+1",
        onHit = "+8",
        guard = "h",
        notes = listOf("Recovers 2f faster on hit or block (t27 r17)"),
        urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Armor_King_movelist#Armor_King-1"),
        gameProperties = T8Properties(),
    )
    val darkElbowHook = Move(
        characterId = "armor-king",
        id = "Armor King-f+2,1",
        name = "Dark Elbow Hook",
        input = "f+2,1",
        damage = "12, 25",
        startup = "i15~16 (i18~19)",
        recovery = "r33",
        onBlock = "-9",
        onHit = "+16a",
        guard = "m, h",
        notes = listOf(
            "Heat Engager",
            "Heat Dash +5, +36a (+26)",
            "Balcony Break",
            "Combo from 1st hit with 6F delay",
            "Combo from 1st CH with 12F delay",
            "Move can be delayed by 10F",
            "Input can be delayed by 12F",
            "Opponent recovers in FDFA",
        ),
        urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Armor_King_movelist#Armor_King-f+2,1"),
        gameProperties = T8Properties(hasWallInteraction = true),
    )
    val shadowPress = Move(
        characterId = "armor-king",
        id = "Armor King-BAD.db+1+2",
        name = "Shadow Press",
        input = "BAD.db+1+2",
        isThrow = true,
        guard = "m,t",
        notes = listOf(
            "Transition into hit grab on grounded, airborne, and backturn hit",
            "AK is left FDFA on whiff/block",
            "Opponent is left FUFT on hit",
            "js14~34",
        ),
        urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Armor_King_movelist#Armor_King-BAD.db+1+2"),
        gameProperties = T8Properties(isLowCrush = true),
    )
    val akSW = Move(
        characterId = "armor-king",
        id = "Armor King-f,f,F+2+4",
        name = "Brilliant Brawler Kick",
        input = "f,f,F+2+4",
        aliases = listOf("Shining Wizard", "wr2+4"),
        isThrow = true,
        guard = "th(h)",
        notes = listOf(
            "Balcony Break",
            "Throw break 1+2",
            "becomes Homing in heat",
            "Partially restores remaining Heat Time",
        ),
        urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Armor_King_movelist#Armor_King-f,f,F+2+4"),
        gameProperties = T8Properties(isHoming = true, hasWallInteraction = true),
    )
}
