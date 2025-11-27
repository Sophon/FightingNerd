package io.github.sophon.wikiwavu.data

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import io.github.sophon.core.wiki.domain.model.Move
import kotlin.test.Test

class MoveMapperTest {
    //region formId
    @Test
    fun `formId handles multi word names`() {
        //given
        val name = "Armor King"
        val expected = "armor_king"

        //when
        val result = name.formId()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `formId handles mixed case with special characters`() {
        //given
        val name = "Jack-8"
        val expected = "jack-8"

        //when
        val result = name.formId()

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region parseAlias
    @Test
    fun `parseAlias handles null`() {
        //given
        val alias: String? = null
        val expected: List<String> = emptyList()

        //when
        val result = alias.parseAliases()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `parseAlias handles multi-word alias`() {
        //given
        val alias = "Shining Wizard"
        val expected = listOf("shining wizard")

        //when
        val result = alias.parseAliases()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `parseAlias handles http with multiple aliases`() {
        //given
        val alias = "&lt;div class=&quot;dotlist&quot;&gt;\n" +
                "\n" +
                "* Can Cans\n" +
                "* Cancan\n" +
                "\n" +
                "&lt;/div&gt;"
        val expected = listOf(
            "can cans",
            "cancan",
        )

        //when
        val result = alias.parseAliases()

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
            charName = "Armor King",
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
            urls = Move.Urls(videoId = null),
            t8Properties = Move.T8Properties(
                isHeat = false,
                isPowerCrush = false,
                isHoming = false,
                stance = ""
            )
        )

        // when
        val result = responseDto.toDomain("Armor King")

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
            charName = "Armor King",
            id = "armor_king-f21",
            name = "Dark Elbow Hook",
            input = "f21",
            damage = "12,25",
            startup = "i15~16",
            recovery = "r33",
            onBlock = "-9",
            onHit = "+16a",
            onCH = null,
            guard = "m,h",
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
            urls = Move.Urls(videoId = null),
            t8Properties = Move.T8Properties(
                isHeat = true,
                isPowerCrush = false,
                isHoming = false,
                stance = ""
            )
        )

        // when
        val result = responseDto.toDomain("Armor King")

        // then
        assertThat(result).hasSize(2)
        assertThat(result[1]).isEqualTo(expectedMove)
    }

    @Test
    fun `toDomain should detect stance from input`() {
        // given
        val moveDto = MoveDto(
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
        val responseDto = MoveListResponseDto(
            cargoQuery = listOf(
                MoveListResponseDto.Title(moveDto)
            )
        )
        val expectedMove = Move(
            charName = "Armor King",
            id = "armor_king-bad.db1+2",
            name = "Shadow Press",
            input = "baddb1+2",
            damage = "18,15",
            startup = "i14~17",
            recovery = "r43? FDFA",
            onBlock = "-18c",
            onHit = "+0d",
            onCH = null,
            guard = "m,t",
            notes = listOf(
                "Transition into hit grab on grounded, airborne, and backturn hit",
                "AK is left FDFA on whiff/block",
                "Opponent is left FUFT on hit",
                "js14~34"
            ),
            aliases = emptyList(),
            urls = Move.Urls(videoId = "File:t8-p2-armor_king-bad.db+1+2.mp4"),
            t8Properties = Move.T8Properties(
                isHeat = false,
                isPowerCrush = false,
                isHoming = false,
                stance = "bad"
            )
        )

        // when
        val result = responseDto.toDomain("Armor King")

        // then
        assertThat(result).hasSize(1)
        assertThat(result[0]).isEqualTo(expectedMove)
    }

    @Test
    fun `toDomain should correctly parse while running move`() {
        // given
        val moveDto = MoveDto(
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
        val responseDto = MoveListResponseDto(
            cargoQuery = listOf(
                MoveListResponseDto.Title(moveDto)
            )
        )
        val expectedMove = Move(
            charName = "Armor King",
            id = "armor_king-wr2+4",
            name = "Brilliant Brawler Kick",
            input = "wr2+4",
            damage = "40 (45)",
            startup = "i10",
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
            urls = Move.Urls(videoId = null),
            t8Properties = Move.T8Properties(
                isHeat = false,
                isPowerCrush = false,
                isHoming = true,
                stance = ""
            )
        )

        // when
        val result = responseDto.toDomain("Armor King")

        // then
        assertThat(result).hasSize(1)
        assertThat(result[0]).isEqualTo(expectedMove)
    }
    //endregion
}
