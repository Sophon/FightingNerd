package io.github.sophon.wiki.adapter.outbound.ktor.wavu

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.Move
import io.github.sophon.wiki.model.game.T5Properties
import io.github.sophon.wiki.model.game.T8Properties
import io.github.sophon.wiki.model.game.Tag2Properties
import io.github.sophon.wiki.model.wiki.Game
import kotlin.test.Test

class WavuMoveRemoteMapperTest {
    //region aliases
    @Test
    fun `no alias and no alt means no aliases`() {
        //given
        val move = WavuMoveSource.konvictKick
        val expected = emptyList<String>()

        //when
        val result = move.toMove(WavuCharacterSource.ak)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `multi-word alias stays one entry`() {
        //given
        val move = WavuMoveSource.shiningWizard
        val expected = listOf("shining wizard", "fff2+4")

        //when
        val result = move.toMove(WavuCharacterSource.ak)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `word containing or is not split`() {
        //given
        val move = WavuMoveSource.matterhorn
        val expected = listOf("matterhorn")

        //when
        val result = move.toMove(WavuCharacterSource.genericChar)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `html alias list is split into entries`() {
        //given
        val move = WavuMoveSource.cancans
        val expected = listOf("can cans", "cancan")

        //when
        val result = move.toMove(WavuCharacterSource.ak)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `aliases are normalized`() {
        //given
        val move = WavuMoveSource.whf
        val expected = listOf("whf", "cd.2", "cd2", "cddf2")

        //when
        val result = move.toMove(WavuCharacterSource.ak)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `alt entries follow alias entries`() {
        //given
        val move = WavuMoveSource.unsd4
        val expected = listOf("fn4", "wds.4", "wds4", "unsd4")

        //when
        val result = move.toMove(WavuCharacterSource.ak)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `denormalized input is kept in the alias`() {
        //given
        val move = WavuMoveSource.shiningWizard
        val expected = listOf(
            "shining wizard",
            "fff2+4",
        )

        //when
        val result = move.toMove(WavuCharacterSource.ak)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `just-frame crouch dash gets cd variants`() {
        //given
        val move = WavuMoveSource.ewhf
        val expected = listOf("ewhf", "electric", "ecd2", "cd#2", "fndf#2", "cddf#2")

        //when
        val result = move.toMove(WavuCharacterSource.ak)

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
        val result = move.toMove(WavuCharacterSource.ak)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `sidestep has correct dot for input or alias`() {
        //given
        val move = WavuMoveSource.ss4
        val expectedAlias = listOf("ss.4")
        val expectedInput = "ss4"

        //when
        val result = move.toMove(WavuCharacterSource.ak)

        //then
        assertThat(result.aliases).isEqualTo(expectedAlias)
        assertThat(result.input).isEqualTo(expectedInput)
    }

    @Test
    fun `heat smash gets its nicknames`() {
        //given
        val move = WavuMoveSource.heatSmash
        val expected = listOf("hs", "heatsmash")

        //when
        val result = move.toMove(WavuCharacterSource.ak)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `hfc gets a fc variant`() {
        //given
        val move = WavuMoveSource.yakouga
        val expected = listOf("hfcdb1+2", "fcdb1+2", "fc1+2", "hfc.1+2")

        //when
        val result = move.toMove(WavuCharacterSource.ak)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `heat crouch dash gets a dotless variant`() {
        //given
        val move = WavuMoveSource.heatMist
        val expected = listOf("h.bad.f1+2", "h.cd1+2", "h.fnddf1+2")

        //when
        val result = move.toMove(WavuCharacterSource.ak)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `backturn is properly formatted`() {
        //given
        val move = WavuMoveSource.blindKick
        val expectedInput = "bt.3"
        val expectedAliases = listOf("bt3")

        //when
        val result = move.toMove(WavuCharacterSource.ak)

        //then
        assertThat(result.input).isEqualTo(expectedInput)
        assertThat(result.aliases).isEqualTo(expectedAliases)
    }
    //endregion

    //region case
    @Test
    fun `held up direction input keeps its case`() {
        //given
        val move = WavuMoveSource.sommersault
        val expected = "UF4"

        //when
        val result = move.toMove(WavuCharacterSource.ak)

        //then
        assertThat(result.input).isEqualTo(expected)
    }

    @Test
    fun `tapped up direction input stays lowercase`() {
        //given
        val move = WavuMoveSource.hopkick
        val expected = "u4"

        //when
        val result = move.toMove(WavuCharacterSource.ak)

        //then
        assertThat(result.input).isEqualTo(expected)
    }
    //endregion

    //region stance
    @Test
    fun `stance is detected from input`() {
        //given
        val move = WavuMoveSource.bad4
        val expected = "BAD"

        //when
        val result = move.toMove(WavuCharacterSource.ak)

        //then
        assertThat(result.t8Properties?.stance).isEqualTo(expected)
    }

    @Test
    fun `input without stance has empty stance field`() {
        //given
        val move = WavuMoveSource.matterhorn

        //when
        val result = move.toMove(WavuCharacterSource.ak)

        //then
        assertThat(result.t8Properties?.stance).isNull()
    }

    @Test
    fun `otg is not a stance`() {
        //given
        val move = WavuMoveSource.stomp

        //when
        val result = move.toMove(WavuCharacterSource.ak)

        //then
        assertThat(result.t8Properties?.stance).isNull()
    }

    @Test
    fun `backturn is a stance`() {
        //given
        val move = WavuMoveSource.moonsault
        val expected = "BT"

        //when
        val result = move.toMove(WavuCharacterSource.ak)

        //then
        assertThat(result.t8Properties?.stance).isEqualTo(expected)
    }

    @Test
    fun `stance is detected from aliases`() {
        //given
        val move = WavuMoveSource.manjiBackfistShredder
        val expected = "BT"

        //when
        val result = move.toMove(WavuCharacterSource.ak)

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
        val result = move.toMove(WavuCharacterSource.ak)

        //then
        assertThat(result.t8Properties?.isHeat).isEqualTo(expected)
    }
    //endregion

    //region wiki url
    @Test
    fun `wiki url points to the move on the character movelist`() {
        //given
        val move = WavuMoveSource.matterhorn
        val expected = "https://wavu.wiki/t/Lili_movelist#Lili-d+3+4"

        //when
        val result = move.toMove(WavuCharacterSource.lili)

        //then
        assertThat(result.urls.wikiUrl).isEqualTo(expected)
    }
    //endregion

    //region notes
    @Test
    fun `html notes become lines with crushes appended`() {
        //given
        val move = WavuMoveSource.matterhorn
        val expected = listOf(
            "Tornado",
            "Evasive, can go under some mids and highs",
            "fs14~44",
        )

        //when
        val result = move.toMove(WavuCharacterSource.ak)

        //then
        assertThat(result.notes).isEqualTo(expected)
    }
    //endregion

    //region parent
    @Test
    fun `child move is completed from its parent`() {
        // given
        val moveList = listOf(WavuMoveSource.stomp, WavuMoveSource.secondStomp)
        val expectedInput = "otg.d44"
        val expectedStartup = "i19~21 (i18~21)"
        val expectedDamage = "18, 8"
        val expectedGuard = "L, L"

        // when
        val result = moveList.toMoveList(WavuCharacterSource.ak)[1]

        //then
        assertThat(result.input).isEqualTo(expectedInput)
        assertThat(result.startup).isEqualTo(expectedStartup)
        assertThat(result.damage).isEqualTo(expectedDamage)
        assertThat(result.guard).isEqualTo(expectedGuard)
    }

    @Test
    fun `parent cycle stops the traversal`() {
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
        val moveList = listOf(move1, move2, move3)
        val expectedInput = "112"
        val expectedStartup = "i11 (i12, i13)"
        val expectedDamage = "10, 11, 12"
        val expectedGuard = "h, h, m"

        // when
        val result = moveList.toMoveList(WavuCharacterSource.ak)[2]

        //then
        assertThat(result.input).isEqualTo(expectedInput)
        assertThat(result.startup).isEqualTo(expectedStartup)
        assertThat(result.damage).isEqualTo(expectedDamage)
        assertThat(result.guard).isEqualTo(expectedGuard)
    }
    //endregion

    //region full move
    @Test
    fun `maps simple move`() {
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
        val responseDto = WavuMoveListResponseDto(
            cargoQuery = listOf(
                WavuMoveListResponseDto.Title(moveDto)
            )
        )
        val expected = Move(
            remoteId = "Armor King-1",
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
            gameProperties = T8Properties(),
        )

        // when
        val result = responseDto.toDomain(WavuCharacterSource.ak)

        // then
        assertThat(result).hasSize(1)
        assertThat(result[0]).isEqualTo(expected)
    }

    @Test
    fun `maps child move with parent data`() {
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
        val responseDto = WavuMoveListResponseDto(
            cargoQuery = listOf(
                WavuMoveListResponseDto.Title(parentMove),
                WavuMoveListResponseDto.Title(childMove)
            )
        )
        val expected = Move(
            remoteId = "Armor King-f+2,1",
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
                hasWallInteraction = true,
            )
        )

        // when
        val result = responseDto.toDomain(WavuCharacterSource.ak)

        // then
        assertThat(result).hasSize(2)
        assertThat(result[1]).isEqualTo(expected)
    }

    @Test
    fun `maps move with video`() {
        // given
        val moveDto = WavuMoveSource.shadowPress
        val responseDto = WavuMoveListResponseDto(
            cargoQuery = listOf(
                WavuMoveListResponseDto.Title(moveDto)
            )
        )
        val expected = Move(
            remoteId = "Armor King-BAD.db+1+2",
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
                stance = "BAD",
                isLowCrush = true,
            )
        )

        // when
        val result = responseDto.toDomain(WavuCharacterSource.ak)

        // then
        assertThat(result).hasSize(1)
        assertThat(result[0]).isEqualTo(expected)
    }

    @Test
    fun `maps running throw`() {
        // given
        val moveDto = WavuMoveSource.akSW
        val responseDto = WavuMoveListResponseDto(
            cargoQuery = listOf(
                WavuMoveListResponseDto.Title(moveDto)
            )
        )
        val expected = Move(
            remoteId = "Armor King-f,f,F+2+4",
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
            aliases = listOf("shining wizard", "fff2+4"),
            urls = Move.Urls(
                videoUrl = null,
                wikiUrl = "https://wavu.wiki/t/Armor_King_movelist#Armor_King-f,f,F+2+4"
            ),
            gameProperties = T8Properties(
                isHoming = true,
                hasWallInteraction = true,
            )
        )

        // when
        val result = responseDto.toDomain(WavuCharacterSource.ak)

        // then
        assertThat(result).hasSize(1)
        assertThat(result[0]).isEqualTo(expected)
    }

    @Test
    fun `detects throw`() {
        // given
        val move = WavuMoveSource.shiningWizard
        val expected = true

        // when
        val result = move.toMove(WavuCharacterSource.ak)

        //then
        assertThat(result.isThrow).isEqualTo(expected)
    }
    //endregion

    //region tag 2
    @Test
    fun `tag 2 move gets tag 2 properties`() {
        // given
        val move = WavuMoveSource.tag2NejiriUraken
        val expected = Tag2Properties(isHighCrush = true)

        // when
        val result = move.toMove(WavuCharacterSource.tag2Kazuya)

        //then
        assertThat(result.gameProperties).isEqualTo(expected)
    }

    @Test
    fun `tag 2 wiki url points to the tag 2 movelist`() {
        // given
        val move = WavuMoveSource.tag2NejiriUraken
        val expected = "https://wavu.wiki/t/Kazuya_movelist_(Tag_2)#Kazuya-d+1+2"

        // when
        val result = move.toMove(WavuCharacterSource.tag2Kazuya)

        //then
        assertThat(result.urls.wikiUrl).isEqualTo(expected)
    }
    //endregion

    //region tekken 5
    @Test
    fun `tekken 5 move gets tekken 5 properties`() {
        // given
        val move = WavuMoveSource.t5Jack5HighCrush
        val expected = T5Properties(isHighCrush = true)

        // when
        val result = move.toMove(WavuCharacterSource.t5Jack5)

        //then
        assertThat(result.gameProperties).isEqualTo(expected)
    }

    @Test
    fun `tekken 5 wiki url points to the tekken 5 movelist`() {
        // given
        val move = WavuMoveSource.t5Jack5HighCrush
        val expected = "https://wavu.wiki/t/Jack-5_movelist_(Tekken_5)#Jack-5-d+3"

        // when
        val result = move.toMove(WavuCharacterSource.t5Jack5)

        //then
        assertThat(result.urls.wikiUrl).isEqualTo(expected)
    }
    //endregion
}

private val Move.t8Properties: T8Properties?
    get() = gameProperties as? T8Properties

private fun List<MoveDto>.toMoveList(character: Character): List<Move> {
    val responseDto = WavuMoveListResponseDto(cargoQuery = map { WavuMoveListResponseDto.Title(it) })
    val moveList = responseDto.toDomain(character)
    return moveList
}

private fun MoveDto.toMove(character: Character): Move {
    val move = listOf(this).toMoveList(character).single()
    return move
}

private object WavuCharacterSource {
    val ak = Character(
        id = CharacterId(Game.Tekken8, "Armor_King"),
        displayName = "Armor King",
        remoteQueryId = "Armor_King",
        wikiUrl = "https://wavu.wiki/t/Armor_King_movelist",
    )
    val lili = Character(
        id = CharacterId(Game.Tekken8, "Lili"),
        displayName = "Lili",
        remoteQueryId = "Lili",
        wikiUrl = "https://wavu.wiki/t/Lili_movelist#Lili-d+3+4",
    )
    val tag2Kazuya = Character(
        id = CharacterId(Game.Tag2, "Kazuya"),
        displayName = "Kazuya",
        remoteQueryId = "Kazuya",
        wikiUrl = "https://wavu.wiki/t/Kazuya_(Tag_2)",
    )
    val t5Jack5 = Character(
        id = CharacterId(Game.Tekken5, "Jack-5"),
        displayName = "Jack-5",
        remoteQueryId = "Jack-5",
        wikiUrl = "https://wavu.wiki/t/Jack-5_(Tekken_5)",
    )
    val genericChar = Character(
        id = CharacterId(Game.Tekken8, "Generic"),
        displayName = "Generic",
        remoteQueryId = "remote",
        wikiUrl = "",
    )
}

private object WavuMoveSource {
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
    val unsd4 = MoveDto(
        id = "Reina-UNS.d+4",
        name = "Santei Gedan-Geri",
        input = "UNS.d+4",
        parent = null,
        target = "L",
        damage = "14",
        startup = "i20",
        recv = "r33",
        tot = "53",
        crush = "cs6~35",
        block = "-12",
        hit = "+3 SEN",
        ch = "[[Reina_combos#Mini-combos|+13 SEN]]",
        notes = "&lt;div class=&quot;plainlist&quot;&gt;\n* Transition to SEN on hit only\n* Transition to standing (+0/[[Reina_combos#Mini-combos|+10]]) on hit with B\n* WDS.4: Unbufferable. i22 effective startup\n&lt;/div&gt;",
        alias = "f,n,4",
        image = null,
        video = null,
        alt = "WDS.4",
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
    val tag2NejiriUraken = MoveDto(
        id = "Kazuya-d+1+2",
        name = "Nejiri Uraken",
        input = "d+1+2",
        parent = null,
        target = "l",
        damage = "24",
        startup = "i25",
        recv = "FC",
        tot = null,
        crush = "cs",
        block = "-16",
        hit = "+2c",
        ch = "KND",
        notes = "Can be cancelled with DB",
        alias = null,
        image = null,
        video = null,
        alt = null,
    )
    val t5Jack5HighCrush = MoveDto(
        id = "Jack-5-d+3",
        name = null,
        input = "d+3",
        parent = null,
        target = "l",
        damage = "12",
        startup = "i20",
        recv = "FC",
        tot = null,
        crush = "cs",
        block = "-15",
        hit = "-1",
        ch = null,
        notes = null,
        alias = null,
        image = null,
        video = null,
        alt = null,
    )
    val hopkick = MoveDto(
        id = "Law-u+4",
        name = "Rising Toe Kick",
        input = "u+4",
        parent = null,
        target = "m",
        damage = "12",
        startup = "i15",
        recv = null,
        tot = null,
        crush = "js",
        block = "-13",
        hit = "+19a (+9)",
        ch = "+33a (+23)",
        notes = null,
        alias = "neutral hopkick",
        image = null,
        video = null,
        alt = null,
    )
    val sommersault = MoveDto(
        id = "Law-UF+4",
        name = "Somersault Kick",
        input = "UF+4",
        parent = null,
        target = "m",
        damage = "23",
        startup = "i18",
        recv = "FC",
        tot = null,
        crush = "js9~",
        block = "-17",
        hit = "+28a (+18)",
        ch = null,
        notes = "&lt;div class=&quot;plainlist&quot;&gt;\n* Alternate input\n** UB+4, U+4, FC.ub+4, FC.u+4, FC.uf+4\n* 18 dmg when performed with UB+4 or FC.ub+4&lt;/div&gt;",
        alias = null,
        image = null,
        video = "File:t8-p2-law-ufx+4.mp4",
        alt = "&lt;div class=&quot;dotlist&quot;&gt;\n\n* UB+4\n* U+4\n* FC.ub+4\n* FC.u+4\n\n&lt;/div&gt;",
    )
    val ewhf = MoveDto(
        id = "Jin-CD.df#2",
        name = "Electric Wind Hook Fist",
        input = "CD.df#2",
        alias = "&lt;div class=&quot;dotlist&quot;&gt;\n\n* EWHF\n* Electric\n* ECD+2\n* f,n,d,df#2\n* f,n,df#2\n&lt;/div&gt;",
    )
    val wgk = MoveDto(
        id = "Reina-WGS.DF+3",
        name = "War God Kick",
        input = "WGS.df+3",
        alias = "&lt;div class=&quot;dotlist&quot;&gt;\n\n* f,n,d,DF+3\n* f,n,DF+3\n* df+3,df+3\n&lt;/div&gt;",
    )
    val ss4 = MoveDto(
        id = "Claudio-SS.4",
        name = "Luxuria",
        input = "SS.4",
    )
    val heatSmash = MoveDto(
        id = "Bryan-H.2+3",
        name = "Notorious Monster",
        input = "H.2+3",
        crush = "js9~",
        notes = "<div class=\"plainlist\">\n* Heat Smash\n* Reversal Break\n* Spike\n* Transition to attack throw on hit\n</div>",
    )
    val yakouga = MoveDto(
        id = "Kunimitsu-hFC.1+2",
        name = "Yakouga",
        input = "hFC.1+2",
        alias = "hFC.db+1+2",
    )
    val heatMist = MoveDto(
        id = "Armor King-H.f,n,d,df+1+2",
        name = "Malice Mist: Villain",
        input = "H.f,n,d,df+1+2",
        alias = "H.BAD.f+1+2",
    )
    val bad4 = MoveDto(
        id = "Armor King-BAD.4",
        name = "Bandido Snatch",
        input = "BAD.4",
    )
    val moonsault = MoveDto(
        id = "Armor King-BT.1+4",
        name = "Moonsault Drop",
        input = "BT.1+4",
    )
    val blindKick = MoveDto(
        id = "Armor King-BT.3",
        name = "Blind Kick",
        input = "BT.3",
        parent = null,
        target = "M",
        damage = "20",
        startup = "i13~14",
        recv = "r37",
        tot = "51",
        crush = null,
        block = "-14",
        hit = "+49a",
        ch = null,
        notes = "&lt;div class=&quot;plainlist&quot;&gt;\n* \n&lt;div\n  style=&quot;display: block; border-width: 0 0 0 0.5em; padding-left: 0.2em; border-style: solid;&quot;\n  class=&quot;movedata-icon border-orange tornado&quot;\n&gt;Tornado&lt;/div&gt;\n* Unparryable by traditional kick parries (bug?)\n&lt;/div&gt;",
        alias = null,
        image = null,
        video = null,
        alt = null,
    )
    val manjiBackfistShredder = MoveDto(
        id = "Yoshimitsu-f+2,1",
        name = "Manji Backfist Shredder",
        input = "f+2,1",
        alias = "&lt;div class=&quot;dotlist&quot;&gt;\n\n* 1SS.f+2,1\n* f+2,1SS.1\n* BT.2,1\n&lt;/div&gt;",
    ) //has Stance in alt inputs
    val tempestBlaster = MoveDto(
        id = "Kazuya-DVK.f,n,d,df+3",
        name = "Tempest Blaster",
        input = "DVK.f,n,d,df+3",
        alias = "&lt;div class=&quot;dotlist&quot;&gt;\n\n* DVK.cd+3\n* H.f,n,d,df+3\n* H.cd+3\n&lt;/div&gt;",
    ) //has Heat in alt inputs
}
