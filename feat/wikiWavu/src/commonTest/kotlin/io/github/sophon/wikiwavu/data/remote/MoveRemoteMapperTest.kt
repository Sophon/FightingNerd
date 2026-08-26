package io.github.sophon.wikiwavu.data.remote

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wikiwavu.integration.model.T8Properties
import kotlin.test.Test

class MoveRemoteMapperTest {
    val character = Character(
        id = "",
        displayName = "",
        remoteQueryId = "",
        wikiUrl = "",
    )
    val ak = Character(
        id = "armor_king",
        displayName = "Armor King",
        remoteQueryId = "Armor_King",
        wikiUrl = "https://wavu.wiki/t/Armor_King_movelist",
    )

    //region formId
    @Test
    fun `formId handles multi word names`() {
        //given
        val move = MoveSource.ffn2
        val expectedId = "armor_king-ffn2"

        //when
        val result = move.toDomain(character, emptyMap())

        //then
        assertThat(result.id).isEqualTo(expectedId)
    }

    @Test
    fun `formId handles mixed case with special characters`() {
        //given
        val move = MoveSource.df2
        val expectedId = "jack-8-df2"

        //when
        val result = move.toDomain(character, emptyMap())

        //then
        assertThat(result.id).isEqualTo(expectedId)
    }
    //endregion

    //region formAliases
    @Test
    fun `toDomain handles null alias`() {
        //given
        val move = MoveSource.konvictKick
        val expectedAlias = emptyList<String>()

        //when
        val result = move.toDomain(character, emptyMap())

        //then
        assertThat(result.aliases).isEqualTo(expectedAlias)
    }

    @Test
    fun `toDomain handles multi-word aliases`() {
        // given
        val move = MoveSource.shiningWizard
        val expectedAlias = listOf("shining wizard")

        // when
        val result = move.toDomain(character, emptyMap())

        //then
        assertThat(result.aliases).isEqualTo(expectedAlias)
    }

    @Test
    fun `formAliases handles http with multiple aliases`() {
        //given
        val move = MoveSource.cancans
        val expectedAlias = listOf("can cans", "cancan",)

        //when
        val result = move.toDomain(character, emptyMap())

        //then
        assertThat(result.aliases).isEqualTo(expectedAlias)
    }

    @Test
    fun `formAliases handles cd`() {
        // given
        val move = MoveSource.whf
        val expectedAlias = listOf("whf", "cd.2", "cd2", "cddf2")

        // when
        val result = move.toDomain(character, emptyMap())

        //then
        assertThat(result.aliases).isEqualTo(expectedAlias)
    }

    @Test
    fun `formAliases handles just-frame cd with df`() {
        // given
        val move = MoveSource.ewhf
        val expectedAlias = listOf("ewhf", "electric", "ecd2", "cd#2", "fndf#2", "cddf#2")

        // when
        val result = move.toDomain(character, emptyMap())

        //then
        assertThat(result.aliases).isEqualTo(expectedAlias)
    }

    @Test
    fun `formAliases handles alt and alias`() {
        // given
        val move = MoveSource.wgk
        val expectedAlias = listOf(
            "cd.3",
            "cd3",
            "fndf3",
            "df3df3",
            "wgsdf3",
        )

        // when
        val result = move.toDomain(character, emptyMap())

        //then
        assertThat(result.aliases).isEqualTo(expectedAlias)
    }

    @Test
    fun `formAliases handles cd in input`() {
        // given
        val move = MoveSource.whf
        val expected = listOf("whf", "cd.2", "cd2", "cddf2",)

        // when
        val result = move.toDomain(character, emptyMap())

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `formAliases handles ss in input`() {
        // given
        val move = MoveSource.ss4
        val expectedAlias = listOf("ss4")

        // when
        val result = move.toDomain(character, emptyMap())

        //then
        assertThat(result.aliases).isEqualTo(expectedAlias)
    }

    @Test
    fun `formAliases handles heat smash`() {
        // given
        val move = MoveSource.heatSmash
        val expectedAlias = listOf("hs", "heatsmash")

        // when
        val result = move.toDomain(character, emptyMap())

        //then
        assertThat(result.aliases).isEqualTo(expectedAlias)
    }

    @Test
    fun `toDomain with name OR creates correct aliases`() {
        // given
        val move = MoveSource.matterhorn
        val expectedAlias = listOf("matterhorn")

        // when
        val result = move.toDomain(character, emptyMap())

        //then
        assertThat(result.aliases).isEqualTo(expectedAlias)
    }

    @Test
    fun `formAliases with hFC`() {
        // given
        val move = MoveSource.yakouga
        val expectedAlias = listOf("hfcdb1+2", "fc1+2")

        // when
        val result = move.toDomain(character, emptyMap())

        //then
        assertThat(result.aliases).isEqualTo(expectedAlias)
    }
    //endregion

    //region isStance
    @Test
    fun `toDomain handles basic stance`() {
        //given
        val move = MoveSource.bad4
        val expectedStance = "bad"

        //when
        val result = move.toDomain(character, emptyMap())

        //then
        assertThat(result.t8Properties?.stance).isEqualTo(expectedStance)
    }

    @Test
    fun `toDomain ignores non stance`() {
        //given
        val move = MoveSource.matterhorn
        val expectedStance = null

        //when
        val result = move.toDomain(character, emptyMap())

        //then
        assertThat(result.t8Properties?.stance).isEqualTo(expectedStance)
    }

    @Test
    fun `toDomain ignores OTG`() {
        //given
        val move = MoveSource.stomp
        val expectedStance = null

        //when
        val result = move.toDomain(character, emptyMap())

        //then
        assertThat(result.t8Properties?.stance).isEqualTo(expectedStance)
    }

    @Test
    fun `toDomain handles backturn`() {
        //given
        val move = MoveSource.moonsault
        val expectedStance = "BT"

        //when
        val result = move.toDomain(character, emptyMap())

        //then
        assertThat(result.t8Properties?.stance).isEqualTo(expectedStance)
    }
    //endregion

    //region formUrl
    @Test
    fun `formUrl handles basic url`() {
        //given
        val move = MoveSource.matterhorn
        val expectedUrl = "https://wavu.wiki/t/Lili_movelist#Lili-d+3+4"
        val character = Character(
            id = "Lili",
            remoteQueryId = "Lili",
            displayName = "Lili",
            wikiUrl = "https://wavu.wiki/t/Lili_movelist#Lili-d+3+4",
        )

        //when
        val result = move.toDomain(character, emptyMap())

        //then
        assertThat(result.urls.wikiUrl).isEqualTo(expectedUrl)
    }
    //endregion

    //region formNotes
    @Test
    fun `formNotes handles links`() {
        //given
        val move = MoveSource.matterhorn
        val expectedNotes = listOf(
            "Tornado",
            "Evasive, can go under some mids and highs",
            "fs14~44",
        )

        //when
        val result = move.toDomain(character, emptyMap())

        //then
        assertThat(result.notes).isEqualTo(expectedNotes)
    }
    //endregion

    //region Parental
    @Test
    fun `toDomain handles parental string`() {
        // given
        val stomp = MoveSource.stomp
        val secondStomp = MoveSource.secondStomp
        val map = mapOf(
            stomp.id to stomp,
            secondStomp.id to secondStomp,
        )
        val expectedInput = "otg.d44"
        val expectedStartup = "i19~21 (i18~21)"
        val expectedDamage = "18, 8"
        val expectedGuard = "L, L"

        // when
        val result = secondStomp.toDomain(character, map)

        //then
        assertThat(result.input).isEqualTo(expectedInput)
        assertThat(result.startup).isEqualTo(expectedStartup)
        assertThat(result.damage).isEqualTo(expectedDamage)
        assertThat(result.guard).isEqualTo(expectedGuard)
    }

    @Test
    fun `formDataFromParent handles infinite loop cycle`() {
        // given
        val move1 = MoveDto(
            id = "1",
            input = "1",
            startup = "i11",
            parent = "1,1,2",
            target = "h",
            damage = "10",
        )
        val move2 = MoveDto(
            id = "1,1",
            input = ",1",
            startup = "i12",
            parent = "1",
            target = "h",
            damage = "11",
        )
        val move3 = MoveDto(
            id = "1,1,2",
            input = ",2",
            startup = "i13",
            parent = "1,1",
            target = "m",
            damage = "12",
        )
        val map = mapOf(
            move1.id to move1,
            move2.id to move2,
            move3.id to move3,
        )
        val expected = ParentalProperties(
            input = "112",
            startup = "i11 (i12, i13)",
            damage = "10, 11, 12",
            guard = "h, h, m",
        )

        // when
        val result = move3.formCompleteDataFromParent(map)

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region toDomain
    @Test
    fun `toDomain should map simple move`() {
        // given
        val moveDto = MoveDto(
            id = "Armor King-1",
            name = "Jab",
            input = "1",
            parent = null,
            target = "h",
            damage = "5",
            startup = "i10",
            recv = "r19",
            tot = "29",
            crush = null,
            block = "+1",
            hit = "+8",
            ch = null,
            notes = "Recovers 2f faster on hit or block (t27 r17)",
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        val responseDto = MoveListResponseDto(
            cargoQuery = listOf(
                MoveListResponseDto.Title(moveDto)
            )
        )
        val expectedMove = Move(
            characterId = "armor_king",
            id = "armor_king-1",
            name = "Jab",
            input = "1",
            damage = "5",
            startup = "i10",
            recovery = "r19",
            onBlock = "+1",
            onHit = "+8",
            onCH = null,
            guard = "h",
            notes = listOf(
                "Recovers 2f faster on hit or block (t27 r17)"
            ),
            aliases = emptyList(),
            urls = Move.Urls(videoUrl = null, wikiUrl = "https://wavu.wiki/t/Armor_King_movelist#Armor_King-1"),
            gameProperties = T8Properties(
                isHeat = false,
                isPowerCrush = false,
                isHoming = false,
                stance = null,
            )
        )

        // when
        val result = responseDto.toDomain(
            ak
        )

        // then
        assertThat(result).hasSize(1)
        assertThat(result[0]).isEqualTo(expectedMove)
    }

    @Test
    fun `toDomain should correctly map child move with parent data`() {
        // given
        val parentMove = MoveDto(
            id = "Armor King-f+2",
            name = null,
            input = "f+2",
            parent = null,
            target = "m",
            damage = "12",
            startup = "i15~16",
            recv = "r29",
            tot = "45",
            crush = null,
            block = "-11",
            hit = "+2",
            ch = null,
            notes = "<div\n  style=\"display: block; border-width: 0 0 0 0.5em; padding-left: 0.2em; border-style: solid;\"\n  class=\"movedata-icon border-teal tip\"\n>Elbow</div>",
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        val childMove = MoveDto(
            id = "Armor King-f+2,1",
            name = "Dark Elbow Hook",
            input = ",1",
            parent = "Armor King-f+2",
            target = ",h",
            damage = ",25",
            startup = ",i18~19",
            recv = "r33",
            tot = "69",
            crush = null,
            block = "-9",
            hit = "+16a",
            ch = null,
            notes = "<div class=\"plainlist\">\n* \n<div\n  style=\"display: block; border-width: 0 0 0 0.5em; padding-left: 0.2em; border-style: solid;\"\n  class=\"movedata-icon border-purple heat\"\n>Heat Engager\n</div>\n* \n<div\n  style=\"display: block; border-width: 0 0 0 0.5em; padding-left: 0.2em; border-style: solid;\"\n  class=\"movedata-icon border-purple heat\"\n>Heat Dash +5, +36a (+26)\n</div>\n* \n<div\n  style=\"display: block; border-width: 0 0 0 0.5em; padding-left: 0.2em; border-style: solid;\"\n  class=\"movedata-icon border-green balcony-break\"\n>Balcony Break</div>\n* Combo from 1st hit with 6F delay\n* Combo from 1st CH with 12F delay\n* Move can be delayed by 10F\n* Input can be delayed by 12F\n* Opponent recovers in FDFA\n</div>",
            alias = null,
            image = null,
            video = null,
            alt = null
        )
        val responseDto = MoveListResponseDto(
            cargoQuery = listOf(
                MoveListResponseDto.Title(parentMove),
                MoveListResponseDto.Title(childMove)
            )
        )
        val expectedMove = Move(
            characterId = "armor_king",
            id = "armor_king-f21",
            name = "Dark Elbow Hook",
            input = "f21",
            damage = "12, 25",
            startup = "i15~16 (i18~19)",
            recovery = "r33",
            onBlock = "-9",
            onHit = "+16a",
            onCH = null,
            guard = "m, h",
            notes = listOf(
                "Heat Engager",
                "Heat Dash +5, +36a (+26)",
                "Balcony Break",
                "Combo from 1st hit with 6F delay",
                "Combo from 1st CH with 12F delay",
                "Move can be delayed by 10F",
                "Input can be delayed by 12F",
                "Opponent recovers in FDFA"
            ),
            aliases = emptyList(),
            urls = Move.Urls(videoUrl = null, wikiUrl = "https://wavu.wiki/t/Armor_King_movelist#Armor_King-f+2,1"),
            gameProperties = T8Properties(
                isHeat = true,
                isPowerCrush = false,
                isHoming = false,
                stance = null,
                hasWallInteraction = true,
            )
        )

        // when
        val result = responseDto.toDomain(ak)

        // then
        assertThat(result).hasSize(2)
        assertThat(result[1]).isEqualTo(expectedMove)
    }

    @Test
    fun `toDomain should detect stance from input`() {
        // given
        val moveDto = MoveSource.shadowPress
        val responseDto = MoveListResponseDto(
            cargoQuery = listOf(
                MoveListResponseDto.Title(moveDto)
            )
        )
        val expectedMove = Move(
            characterId = "armor_king",
            id = "armor_king-bad.db1+2",
            name = "Shadow Press",
            input = "bad.db1+2",
            damage = "18,15",
            startup = "i14~17",
            recovery = "r43? FDFA",
            onBlock = "-18c",
            onHit = "+0d",
            onCH = null,
            isThrow = true,
            guard = "m,t",
            notes = listOf(
                "Transition into hit grab on grounded, airborne, and backturn hit",
                "AK is left FDFA on whiff/block",
                "Opponent is left FUFT on hit",
                "js14~34"
            ),
            aliases = listOf("baddb1+2"),
            urls = Move.Urls(
                videoId = "t8-p2-armor_king-bad.db+1+2.mp4",
                videoUrl = "https://wavu.wiki/t/Special:Redirect/file/File%3At8-p2-armor_king-bad.db%2B1%2B2.mp4",
                wikiUrl = "https://wavu.wiki/t/Armor_King_movelist#Armor_King-BAD.db+1+2",
            ),
            gameProperties = T8Properties(
                isHeat = false,
                isPowerCrush = false,
                isHoming = false,
                stance = "bad",
                isLowCrush = true,
                isHighCrush = false,
            )
        )

        // when
        val result = responseDto.toDomain(ak)

        // then
        assertThat(result).hasSize(1)
        assertThat(result[0]).isEqualTo(expectedMove)
    }

    @Test
    fun `toDomain should correctly parse while running move`() {
        // given
        val moveDto = MoveSource.akSW
        val responseDto = MoveListResponseDto(
            cargoQuery = listOf(
                MoveListResponseDto.Title(moveDto)
            )
        )
        val expectedMove = Move(
            characterId = "armor_king",
            id = "armor_king-wr2+4",
            name = "Brilliant Brawler Kick",
            input = "wr2+4",
            damage = "40 (45)",
            startup = "i10",
            isThrow = true,
            recovery = "FUFT",
            onBlock = "-5",
            onHit = "+10d",
            onCH = null,
            guard = "th(h)",
            notes = listOf(
                "Balcony Break",
                "Throw break 1+2",
                "Input n,f,F+2+4 within 6 frames after dash startup (f,n,f) to execute \"blue spark\" (+5 damage).",
                "i13 startup for Bluespark throw with buffered input",
                "Opponent left FUFT",
                "Armor King recovers FUFT",
                "becomes Homing in heat",
                "Partially restores remaining Heat Time"
            ),
            aliases = listOf("shining wizard"),
            urls = Move.Urls(
                videoUrl = null,
                wikiUrl = "https://wavu.wiki/t/Armor_King_movelist#Armor_King-f,f,F+2+4"
            ),
            gameProperties = T8Properties(
                isHeat = false,
                isPowerCrush = false,
                isHoming = true,
                stance = null,
                hasWallInteraction = true,
                hasFloorInteraction = false,
            )
        )

        // when
        val result = responseDto.toDomain(ak)

        // then
        assertThat(result).hasSize(1)
        assertThat(result[0]).isEqualTo(expectedMove)
    }

    @Test
    fun `toDomain formats input for heat and crouch dash`() {
        // given
        val move = MoveSource.heatMist
        val expectedInput = "h.cd.1+2"
        val expectedAlias = listOf("h.bad.f1+2", "h.cd1+2")

        // when
        val result = move.toDomain(character, emptyMap())

        //then
        assertThat(result.input).isEqualTo(expectedInput)
        assertThat(result.aliases).isEqualTo(expectedAlias)
    }

    @Test
    fun `toDomain detects throw`() {
        // given
        val move = MoveSource.shiningWizard
        val expectedIsThrow = true

        // when
        val result = move.toDomain(character, emptyMap())

        //then
        assertThat(result.isThrow).isEqualTo(expectedIsThrow)
    }
    //endregion

    //region Properties from aliases
    @Test
    fun `Heat from aliases`() {
        // given
        val move = MoveSource.tempestBlaster
        val expectedIsHeat = true

        // when
        val result = move.toDomain(character = character, movesById = emptyMap())

        //then
        assertThat(result.t8Properties?.isHeat).isEqualTo(expectedIsHeat)
    }

    @Test
    fun `Stance from aliases`() {
        // given
        val move = MoveSource.manjiBackfistShredder
        val expectedStance = "BT"

        // when
        val result = move.toDomain(character = character, movesById = emptyMap())

        //then
        assertThat(result.t8Properties?.stance).isEqualTo(expectedStance)
    }
    //endregion
}

private val Move.t8Properties: T8Properties?
    get() = gameProperties as? T8Properties

private object MoveSource {
    val shadowPress = MoveDto(
        id = "Armor King-BAD.db+1+2",
        name = "Shadow Press",
        input = "BAD.db+1+2",
        parent = null,
        target = "m,t",
        damage = "18,15",
        startup = "i14~17",
        recv = "r43? FDFA",
        tot = "60",
        crush = "js14~34",
        block = "-18c",
        hit = "+0d",
        ch = null,
        notes = "<div class=\"plainlist\">\n* Transition into hit grab on grounded, airborne, and backturn hit\n* AK is left FDFA on whiff/block\n* Opponent is left FUFT on hit</div>",
        alias = null,
        image = null,
        video = "File:t8-p2-armor_king-bad.db+1+2.mp4",
        alt = null
    )
    val matterhorn = MoveDto(
        id = "Lili-d+3+4",
        name = "Matterhorn Ascension",
        input = "d+3+4",
        parent = null,
        target = "m",
        damage = "23",
        startup = "i17~21",
        recv = "r41",
        tot = "62",
        crush = "fs14~44",
        block = "-21",
        hit = "+45a (+35)",
        ch = null,
        notes = "<div class=\"plainlist\">\n* \n<div\n  style=\"display: block; border-width: 0 0 0 0.5em; padding-left: 0.2em; border-style: solid;\"\n  class=\"movedata-icon border-orange tornado\"\n>Tornado</div>\n* Evasive, can go under some mids and highs\n</div>",
        alias = "Matterhorn",
        image = null,
        video = "File:t8-p2-lili-d+3+4.mp4",
        alt = null,
    )
    val shiningWizard = MoveDto(
        id = "King-f,f,F+2+4",
        name = "Tomahawk",
        input = "f,f,F+2+4",
        parent = null,
        target = "t",
        damage = "40(45)",
        startup = "i10",
        recv = "r29",
        tot = "39",
        crush = null,
        block = "-5",
        hit = "+1d",
        ch = null,
        notes = "<div class=\"plainlist\">\n* \n<div\n  style=\"display: block; border-width: 0 0 0 0.5em; padding-left: 0.2em; border-style: solid;\"\n  class=\"movedata-icon border-blue homing\"\n>Homing</div> during heat\n* 1+2 throw break\n* Input f,F+2+4 in 6 frames after f,f for bluespark.\n* i13 startup for Bluespark throw with buffered input\n* 45 damage on bluespark\n* 18F throw break window on bluespark\n* 7F throw break window on CH bluespark\n* Partially restores remaining Heat time.\n* Opponent recovers in FUFL.\n</div>",
        alias = "Shining Wizard",
        image = null,
        video = "File:t8-p2-king-f,f,f+2+4.mp4",
        alt = null,
    )
    val akSW = MoveDto(
        id = "Armor King-f,f,F+2+4",
        name = "Brilliant Brawler Kick",
        input = "f,f,F+2+4",
        parent = null,
        target = "th(h)",
        damage = "40 (45)",
        startup = "i10",
        recv = "FUFT",
        tot = null,
        crush = null,
        block = "-5",
        hit = "+10d",
        ch = null,
        notes = "<div class=\"plainlist\">\n* \n<div\n  style=\"display: block; border-width: 0 0 0 0.5em; padding-left: 0.2em; border-style: solid;\"\n  class=\"movedata-icon border-green balcony-break\"\n>Balcony Break</div>\n* Throw break 1+2\n* Input n,f,F+2+4 within 6 frames after dash startup (f,n,f) to execute \"blue spark\" (+5 damage).\n* i13 startup for Bluespark throw with buffered input\n* Opponent left FUFT\n* Armor King recovers FUFT\n* becomes Homing in heat\n* Partially restores remaining Heat Time\n </div>",
        alias = "Shining Wizard",
        image = null,
        video = null,
        alt = "wr2+4"
    )
    val konvictKick = MoveDto(
        id = "King-f,F+4",
        name = "Konvict Kick",
        input = "f,F+4",
        parent = null,
        target = "m,(t)",
        damage = "25,(14)",
        startup = "i15",
        recv = null,
        tot = null,
        crush = null,
        block = "-15",
        hit = "+14a(+4)",
        ch = "+1d",
        notes = "&lt;div class=&quot;plainlist&quot;&gt;\n* \n&lt;div\n  style=&quot;display: block; border-width: 0 0 0 0.5em; padding-left: 0.2em; border-style: solid;&quot;\n  class=&quot;movedata-icon border-orange tornado&quot;\n&gt;Tornado&lt;/div&gt;\n* \n&lt;div\n  style=&quot;display: block; border-width: 0 0 0 0.5em; padding-left: 0.2em; border-style: solid;&quot;\n  class=&quot;movedata-icon border-green balcony-break&quot;\n&gt;Balcony Break&lt;/div&gt;\n* Shifts to 14dmg throw on front grounded CH.\n&lt;/div&gt;",
        alias = null,
        image = null,
        video = "File:t8-p2-king-f,f,4.mp4",
        alt = null
    )
    val cancans = MoveDto(
        id = "Asuka-d+3+4",
        name = "Double Lift Kicks",
        input = "d+3+4",
        parent = null,
        target = "l,h",
        damage = "5,15",
        startup = "i14 i9~12",
        recv = "r31",
        tot = "73",
        crush = "&lt;div class=&quot;plainlist&quot;&gt;\n* js5~42\n* fs43~45&lt;/div&gt;",
        block = "-8",
        hit = "[[Asuka_combos#Staples|+30a (+20)]]",
        ch = null,
        notes = "&lt;div class=&quot;plainlist&quot;&gt;\n* Combo from 1st CH\n* -25 if 1st hit is blocked \n&lt;/div&gt;",
        alias = "&lt;div class=&quot;dotlist&quot;&gt;\n\n* Can Cans\n* Cancan\n\n&lt;/div&gt;",
        image = null,
        video = "File:t8-p2-asuka-d+3+4.mp4",
        alt = null
    )
    val whf = MoveDto(
        id = "Jin-CD.df+2",
        name = "Wind Hook Fist",
        input = "CD.df+2",
        parent = null,
        target = "h",
        damage = "20",
        startup = "i11~12",
        recv = "r28",
        tot = "40",
        crush = null,
        block = "-10",
        hit = "[[Jin_combos#Staples|+74a (+58)]]",
        ch = null,
        notes = "&lt;div class=&quot;plainlist&quot;&gt;\n* \n&lt;div\n  style=&quot;display: block; border-width: 0 0 0 0.5em; padding-left: 0.2em; border-style: solid;&quot;\n  class=&quot;movedata-icon border-green balcony-break&quot;\n&gt;Balcony Break&lt;/div&gt;\n* Turns into EWHF (CD.df#2) while in heat\n&lt;/div&gt;",
        alias = "&lt;div class=&quot;dotlist&quot;&gt;\n\n* WHF\n* f,n,d,df+2\n&lt;/div&gt;",
        image = null,
        video = "File:t8-p2-jin-cd.df+2.mp4",
        alt = null
    )
    val ewhf = MoveDto(
        id = "Jin-CD.df\${justFrame}2",
        name = "Electric Wind Hook Fist",
        input = "CD.df#2",
        parent = null,
        target = "h",
        damage = "25",
        startup = "i11~i12",
        recv = "r27",
        tot = "39",
        crush = null,
        block = "+5~+6",
        hit = "[[Jin_combos#Staples|+76a (+60)]]",
        ch = null,
        notes = "&lt;div class=&quot;plainlist&quot;&gt;\n* \n&lt;div\n  style=&quot;display: block; border-width: 0 0 0 0.5em; padding-left: 0.2em; border-style: solid;&quot;\n  class=&quot;movedata-icon border-green balcony-break&quot;\n&gt;Balcony Break&lt;/div&gt;\n* Recovers 3f faster on hit or block (t36 r24)\n&lt;/div&gt;",
        alias = "&lt;div class=&quot;dotlist&quot;&gt;\n\n* EWHF\n* Electric\n* ECD+2\n* f,n,d,df#2\n&lt;/div&gt;",
        image = null,
        video = "File:t8-p2-jin-cd.dfh2.mp4",
        alt = "f,n,df#2"
    )
    val wgk = MoveDto(
        id = "Reina-WGS.DF+3",
        name = "War God Kick",
        input = "WGS.df+3",
        parent = null,
        target = "M",
        damage = "17",
        startup = "i15~17",
        recv = "r38 FC",
        tot = null,
        crush = null,
        block = "-13",
        hit = "+13a (-4)",
        ch = "[[Reina_combos#Staples|+23a (+17)]]",
        notes = "&lt;div class=&quot;plainlist&quot;&gt;\n* Becomes 22dmg Electric War God Kick during Heat (partially uses remaining Heat Time)\n** 4 chip damage on block\n* 21dmg when performing f,n,d,DF+3/f,n,DF+3 without Heat\n&lt;/div&gt;",
        alias = null,
        image = null,
        video = "File:Reina-WGS.df+3.mp4",
        alt = "&lt;div class=&quot;dotlist&quot;&gt;\n\n* f,n,d,DF+3\n* f,n,DF+3\n* df+3,df+3\n&lt;/div&gt;"
    )
    val ss4 = MoveDto(
        id = "Claudio-SS.4",
        name = "Luxuria",
        input = "SS.4",
        parent = null,
        target = "L",
        damage = "20",
        startup = "i20~21",
        recv = "r30",
        tot = "51",
        crush = null,
        block = "-12",
        hit = "+6c",
        ch = null,
        notes = "&lt;div class=&quot;plainlist&quot;&gt;\n* Side step takes 9f, effective startup i29\n* Tracking and range notes:\n* Assuming i29 startup with 9f left sidestep\n* SSL4 range: 2.45\n* SSR4 range: 2.39\n* Pseudo homing low?\n&lt;/div&gt;",
        alias = null,
        image = null,
        video = "File:t8-p2-claudio-ss.4.mp4",
        alt = null
    )
    val heatSmash = MoveDto(
        id = "Bryan-H.2+3",
        name = "Notorious Monster",
        input = "H.2+3",
        parent = null,
        target = "m,m,t",
        damage = "20,20,35",
        startup = "i16",
        recv = "r34",
        tot = "70",
        crush = "js9~",
        block = "+9",
        hit = "-1d",
        ch = null,
        notes = "&lt;div class=&quot;plainlist&quot;&gt;\n* \n&lt;div\n  style=&quot;display: block; border-width: 0 0 0 0.5em; padding-left: 0.2em; border-style: solid;&quot;\n  class=&quot;movedata-icon border-purple heat&quot;\n&gt;Heat Smash&lt;/div&gt;\n* \n&lt;div\n  style=&quot;display: block; border-width: 0 0 0 0.5em; padding-left: 0.2em; border-style: solid;&quot;\n  class=&quot;movedata-icon border-blue reversal-break&quot;\n&gt;Reversal Break&lt;/div&gt;\n* \n&lt;div\n  style=&quot;display: block; border-width: 0 0 0 0.5em; padding-left: 0.2em; border-style: solid;&quot;\n  class=&quot;movedata-icon border-yellow spike&quot;\n&gt;Spike&lt;/div&gt;\n* Transition to attack throw on hit\n&lt;/div&gt;",
        alias = null,
        image = null,
        video = null,
        alt = null
    )
    val df2 = MoveDto(
        id = "Jack-8-df+2",
        name = "Programmed Uppercut",
        input = "df+2",
        parent = null,
        target = "m",
        damage = "13",
        startup = "i15",
        recv = null,
        tot = null,
        crush = null,
        block = "-14",
        hit = "+31a (+21)",
        ch = null,
        notes = null,
        alias = null,
        image = null,
        video = "File:t8-p2-jack-8-df+2.mp4",
        alt = null
    )
    val ffn2 = MoveDto(
        id = "Armor King-f,f,n,2",
        name = "Underhanded",
        input = "f,f,n,2",
        parent = null,
        target = "L",
        damage = "20",
        startup = "i19",
        recv = null,
        tot = null,
        crush = "cs8~37",
        block = "-13",
        hit = "+5",
        ch = "+10",
        notes = "&lt;div class=&quot;plainlist&quot;&gt;\n* Transition to BAD on hit with F (+8/+13)\n&lt;/div&gt;",
        alias = null,
        image = null,
        video = null,
        alt = null
    )
    val bad4 = MoveDto(
        id = "Armor King-BAD.4",
        name = "Bandido Snatch",
        input = "BAD.4",
        parent = null,
        target = "L",
        damage = "23",
        startup = "i17~18",
        recv = "r31",
        tot = "49",
        crush = null,
        block = "-13",
        hit = "+3c",
        ch = "+26a",
        notes = "&lt;div class=&quot;plainlist&quot;&gt;\n  &lt;/div&gt;",
        alias = null,
        image = null,
        video = "File:t8-p2-armor_king-bad.4.mp4",
        alt = null
    )
    val moonsault = MoveDto(
        id = "Armor King-BT.1+4",
        name = "Moonsault Drop",
        input = "BT.1+4",
        parent = null,
        target = "m!",
        damage = "20",
        startup = "i41~47",
        recv = null,
        tot = null,
        crush = "js",
        block = "!",
        hit = "+0d",
        ch = null,
        notes = "&lt;div class=&quot;plainlist&quot;&gt;\n* Armor King recovers FDFR on close hit, and FUFT on far hit or whiff\n&lt;/div&gt;",
        alias = null,
        image = null,
        video = null,
        alt = null
    )
    val stomp = MoveDto(
        id = "Armor King-OTG.d+4",
        name = null,
        input = "OTG.d+4",
        parent = null,
        target = "L",
        damage = "18",
        startup = "i19~21",
        recv = null,
        tot = null,
        crush = null,
        block = "-16",
        hit = "-5d (-13)",
        ch = null,
        notes = "&lt;div class=&quot;plainlist&quot;&gt;\n* -5 on hit when opponent is standing\n&lt;/div&gt;",
        alias = null,
        image = null,
        video = null,
        alt = null
    )
    val secondStomp = MoveDto(
        id = "Armor King-OTG.d+4,4",
        name = null,
        input = ",4",
        parent = "Armor King-OTG.d+4",
        target = ",L",
        damage = ",8",
        startup = "i18~21",
        recv = null,
        tot = null,
        crush = null,
        block = "-16",
        hit = "-8d",
        ch = null,
        notes = "<div class=\"plainlist\">\n* \n<div\n  style=\"display: block; border-width: 0 0 0 0.5em; padding-left: 0.2em; border-style: solid;\"\n  class=\"movedata-icon border-yellow floor-break\"\n>Floor Break</div>\n* Combos from 1st hit on grounded opponent\n* -5 on hit when opponent is standing\n</div>",
        alias = null,
        image = null,
        video = null,
        alt = null,
    )
    val heatMist = MoveDto(
        id = "Armor King-H.f,n,d,df+1+2",
        name = "Malice Mist: Villain",
        input = "H.f,n,d,df+1+2",
        parent = null,
        target = "h!",
        damage = "[10]",
        startup = "i25~28",
        recv = "r29",
        tot = "57",
        crush = null,
        block = null,
        hit = "+23g",
        ch = null,
        notes = "<div class=\"plainlist\">\n* On hit, gives a special stun that makes throws unbreakable for the stun duration\n* Consumes 600F of remaining Heat time\n* Only deals recoverable damage\n* Cannot K.O </div>",
        alias = null,
        image = null,
        video = null,
        alt = "H.BAD.f+1+2",
    )
    val yakouga = MoveDto(
        id = "Kunimitsu-hFC.1+2",
        name = "Yakouga",
        input = "hFC.1+2",
        parent = null,
        target = "l,sm",
        damage = "8,15",
        startup = "i18 i15",
        recv = "r31",
        tot = "70",
        crush = "cs1~24",
        block = "-12",
        hit = "+5",
        ch = null,
        notes = "<div class=\"plainlist\">\n* \n<div\n  style=\"display: block; border-width: 0 0 0 0.5em; padding-left: 0.2em; border-style: solid;\"\n  class=\"movedata-icon border-teal tip\"\n>Weapon</div>\n* Can hit grounded when off-axis\n* Recovers in BT on hit only\n</div>",
        alias = null,
        image = null,
        video = null,
        alt = "hFC.db+1+2",
    )
    val tempestBlaster = MoveDto(
        id = "Kazuya-DVK.f,n,d,df+3",
        name = "Tempest Blaster",
        input = "DVK.f,n,d,df+3",
        parent = null,
        target = "h,t",
        damage = "30,20",
        startup = "i18~27,i20~30",
        recv = null,
        tot = null,
        crush = "js9~",
        block = "-2~+8",
        hit = "+0d",
        ch = null,
        notes = "&lt;div class=&quot;plainlist&quot;&gt;\n* Consumes 180F of remaining Heat time\n* Extension available only on hit or block\n* Extension can be canceled with b\n* 16 chip damage on block\n&lt;/div&gt;",
        alias = "&lt;div class=&quot;dotlist&quot;&gt;\n\n* DVK.cd+3\n* H.f,n,d,df+3\n* H.cd+3\n\n&lt;/div&gt;",
        image = null,
        video = "File:t8-p2-kazuya-dvk.f,n,d,df+3.mp4",
        alt = null,
    ) //has Heat in alt inputs
    val manjiBackfistShredder = MoveDto(
        id = "Yoshimitsu-f+2,1",
        name = "Manji Backfist Shredder",
        input = ",1",
        parent = "Yoshimitsu-f+2",
        target = ",M",
        damage = ",21",
        startup = ",i26~27",
        recv = "r32",
        tot = "77",
        crush = null,
        block = "-12",
        hit = "+17a (+10)",
        ch = null,
        notes = "&lt;div class=&quot;plainlist&quot;&gt;\n* \n&lt;div\n  style=&quot;display: block; border-width: 0 0 0 0.5em; padding-left: 0.2em; border-style: solid;&quot;\n  class=&quot;movedata-icon border-orange tornado&quot;\n&gt;Tornado&lt;/div&gt;\n* \n&lt;div\n  style=&quot;display: block; border-width: 0 0 0 0.5em; padding-left: 0.2em; border-style: solid;&quot;\n  class=&quot;movedata-icon border-teal tip&quot;\n&gt;Weapon&lt;/div&gt;\n* Combo from 1st CH +66a (+50)&lt;/div&gt;",
        alias = "&lt;div class=&quot;dotlist&quot;&gt;\n\n* 1SS.f+2,1\n* f+2,1SS.1\n&lt;/div&gt;",
        image = null,
        video = "File:t8-p2-yoshimitsu-f+2,1.mp4",
        alt = "BT.2,1",
    ) //has Stance in alt inputs
}
