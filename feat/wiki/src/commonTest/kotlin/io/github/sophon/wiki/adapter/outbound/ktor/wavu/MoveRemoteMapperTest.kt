package io.github.sophon.wiki.adapter.outbound.ktor.wavu

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wiki.application.domain.model.gameProperties.T8Properties
import kotlin.test.Test

class MoveRemoteMapperTest {
    val ak = Character(
        id = "armor_king",
        displayName = "Armor King",
        remoteQueryId = "Armor_King",
        wikiUrl = "https://wavu.wiki/t/Armor_King_movelist",
    )

    //region aliases
    @Test
    fun `no alias and no alt means no aliases`() {
        //given
        val move = MoveSource.konvictKick
        val expected = emptyList<String>()

        //when
        val result = move.toMove(ak)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `multi-word alias stays one entry`() {
        //given
        val move = MoveSource.shiningWizard
        val expected = listOf("Shining Wizard")

        //when
        val result = move.toMove(ak)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `word containing or is not split`() {
        //given
        val move = MoveSource.matterhorn
        val expected = listOf("Matterhorn")

        //when
        val result = move.toMove(ak)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `html alias list is split into entries`() {
        //given
        val move = MoveSource.cancans
        val expected = listOf("Can Cans", "Cancan")

        //when
        val result = move.toMove(ak)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `aliases keep the wiki notation`() {
        //given
        val move = MoveSource.whf
        val expected = listOf("WHF", "f,n,d,df+2")

        //when
        val result = move.toMove(ak)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `alt entries follow alias entries`() {
        //given
        val move = MoveSource.unsd4
        val expected = listOf("f,n,4", "WDS.4")

        //when
        val result = move.toMove(ak)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }
    //endregion

    //region wiki url
    @Test
    fun `wiki url points to the move on the character movelist`() {
        //given
        val move = MoveSource.matterhorn
        val expected = "https://wavu.wiki/t/Lili_movelist#Lili-d+3+4"
        val lili = Character(
            id = "Lili",
            remoteQueryId = "Lili",
            displayName = "Lili",
            wikiUrl = "https://wavu.wiki/t/Lili_movelist#Lili-d+3+4",
        )

        //when
        val result = move.toMove(lili)

        //then
        assertThat(result.urls.wikiUrl).isEqualTo(expected)
    }
    //endregion

    //region notes
    @Test
    fun `html notes become lines with crushes appended`() {
        //given
        val move = MoveSource.matterhorn
        val expected = listOf(
            "Tornado",
            "Evasive, can go under some mids and highs",
            "fs14~44",
        )

        //when
        val result = move.toMove(ak)

        //then
        assertThat(result.notes).isEqualTo(expected)
    }
    //endregion

    //region parent
    @Test
    fun `child move is completed from its parent`() {
        // given
        val moveList = listOf(MoveSource.stomp, MoveSource.secondStomp)
        val expectedInput = "OTG.d+4,4"
        val expectedStartup = "i19~21 (i18~21)"
        val expectedDamage = "18, 8"
        val expectedGuard = "L, L"

        // when
        val result = moveList.toMoveList(ak)[1]

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
        val expectedInput = "1,1,2"
        val expectedStartup = "i11 (i12, i13)"
        val expectedDamage = "10, 11, 12"
        val expectedGuard = "h, h, m"

        // when
        val result = moveList.toMoveList(ak)[2]

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
        val responseDto = MoveListResponseDto(
            cargoQuery = listOf(
                MoveListResponseDto.Title(moveDto)
            )
        )
        val expected = Move(
            characterId = "armor_king",
            id = "Armor King-1",
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
        val result = responseDto.toDomain(ak)

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
        val responseDto = MoveListResponseDto(
            cargoQuery = listOf(
                MoveListResponseDto.Title(parentMove),
                MoveListResponseDto.Title(childMove)
            )
        )
        val expected = Move(
            characterId = "armor_king",
            id = "Armor King-f+2,1",
            name = "Dark Elbow Hook",
            input = "f+2,1",
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
                hasWallInteraction = true,
            )
        )

        // when
        val result = responseDto.toDomain(ak)

        // then
        assertThat(result).hasSize(2)
        assertThat(result[1]).isEqualTo(expected)
    }

    @Test
    fun `maps move with video`() {
        // given
        val moveDto = MoveSource.shadowPress
        val responseDto = MoveListResponseDto(
            cargoQuery = listOf(
                MoveListResponseDto.Title(moveDto)
            )
        )
        val expected = Move(
            characterId = "armor_king",
            id = "Armor King-BAD.db+1+2",
            name = "Shadow Press",
            input = "BAD.db+1+2",
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
            aliases = emptyList(),
            urls = Move.Urls(
                videoId = "t8-p2-armor_king-bad.db+1+2.mp4",
                videoUrl = "https://wavu.wiki/t/Special:Redirect/file/File%3At8-p2-armor_king-bad.db%2B1%2B2.mp4",
                wikiUrl = "https://wavu.wiki/t/Armor_King_movelist#Armor_King-BAD.db+1+2",
            ),
            gameProperties = T8Properties(
                isLowCrush = true,
            )
        )

        // when
        val result = responseDto.toDomain(ak)

        // then
        assertThat(result).hasSize(1)
        assertThat(result[0]).isEqualTo(expected)
    }

    @Test
    fun `maps running throw`() {
        // given
        val moveDto = MoveSource.akSW
        val responseDto = MoveListResponseDto(
            cargoQuery = listOf(
                MoveListResponseDto.Title(moveDto)
            )
        )
        val expected = Move(
            characterId = "armor_king",
            id = "Armor King-f,f,F+2+4",
            name = "Brilliant Brawler Kick",
            input = "f,f,F+2+4",
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
            aliases = listOf("Shining Wizard", "wr2+4"),
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
        val result = responseDto.toDomain(ak)

        // then
        assertThat(result).hasSize(1)
        assertThat(result[0]).isEqualTo(expected)
    }

    @Test
    fun `detects throw`() {
        // given
        val move = MoveSource.shiningWizard
        val expected = true

        // when
        val result = move.toMove(ak)

        //then
        assertThat(result.isThrow).isEqualTo(expected)
    }
    //endregion
}

private fun List<MoveDto>.toMoveList(character: Character): List<Move> {
    val responseDto = MoveListResponseDto(cargoQuery = map { MoveListResponseDto.Title(it) })
    val moveList = responseDto.toDomain(character)
    return moveList
}

private fun MoveDto.toMove(character: Character): Move {
    val move = listOf(this).toMoveList(character).single()
    return move
}

private object MoveSource {
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
}
