package io.github.sophon.wikiwavu.data

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.core.wiki.usecase.DownloadMoveListUseCase
import io.github.sophon.wikiwavu.domain.cleanMoveInput
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

    //region formAliases
    @Test
    fun `formAliases handles null`() {
        //given
        val alias: String? = null
        val expected: List<String> = emptyList()

        //when
        val result = "".formAliases(alias = alias, alt = null)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `formAliases handles multi-word alias`() {
        //given
        val alias = "Shining Wizard"
        val expected = listOf("shining wizard")

        //when
        val result = "".formAliases(alias, null)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `formAliases handles http with multiple aliases`() {
        //given
        val alias = "&lt;div class=&quot;dotlist&quot;&gt;\n\n* Can Cans\n* Cancan\n\n&lt;/div&gt;"
        val expected = listOf(
            "can cans",
            "cancan",
        )

        //when
        val result = "".formAliases(alias, null)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `formAliases handles cd`() {
        // given
        val string = "cd.df2"
        val expected = listOf("cd2", "cd.2")

        // when
        val result = string.formAliases(null, null)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `formAliases handles just-frame cd with df`() {
        // given
        val string = "cd.df#2"
        val expected = listOf("cd#2")

        // when
        val result = string.formAliases(null, null)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `formAliases handles alt and alias`() {
        // given
        val input = "WGS.df+3".cleanMoveInput()
        val alt = "&lt;div class=&quot;dotlist&quot;&gt;\\n\\n* f,n,d,DF+3\\n* f,n,DF+3\\n* df+3,df+3\\n&lt;/div&gt;"
        val expected = listOf(
            "cd.3",
            "cd3",
            "fndf3",
            "df3df3",
            "wgsdf3",
        )

        // when
        val result = input.formAliases(null, alt)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `formAliases handles cd in input`() {
        // given
        val input = "f,n,d,DF+4,4".cleanMoveInput()
        val expected = listOf("cd44")

        // when
        val result = input.formAliases(null, null)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `formAliases handles ss in input`() {
        // given
        val input = "SS.2".cleanMoveInput()
        val expected = listOf("ss2")

        // when
        val result = input.formAliases(null, null)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `formAliases handles heat smash`() {
        // given
        val input = "h.2+3"
        val expected = listOf("hs", "heatsmash")

        // when
        val result = input.formAliases("", "")

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region isStance
    @Test
    fun `getStance handles basic stance`() {
        //given
        val string = "BAD.1+2"
        val expected = "BAD"

        //when
        val result = string.getStance()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `getStance ignores non stance`() {
        //given
        val string = "FCdf4"
        val expected = null

        //when
        val result = string.getStance()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `getStance ignores OTG`() {
        //given
        val string = "otg3"
        val expected = null

        //when
        val result = string.getStance()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `getStances handles backturn`() {
        //given
        val string = "BTws3"
        val expected = "BT"

        //when
        val result = string.getStance()

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region formUrl
    @Test
    fun `formUrl handles basic url`() {
        //given
        val charName = "Asuka"
        val id = "Asuka-3,1"
        val expected = "https://wavu.wiki/t/Asuka_movelist#Asuka-3,1"

        //when
        val result = formMoveWikiUrl(charName, id)

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region formNotes
    @Test
    fun `formNotes handles links`() {
        //given
        val string = "&lt;div class=&quot;plainlist&quot;&gt;\n* \n&lt;div\n  style=&quot;display: block; border-width: 0 0 0 0.5em;" +
                " padding-left: 0.2em; border-style: solid;&quot;\n  class=&quot;movedata-icon border-blue homing&quot;" +
                "\n&gt;Homing&lt;/div&gt;" +
                "\n* Deals chip damage on block" +
                "\n* Transition to SEN (+0/[[Reina_combos#Mini-combos|+13]]/[[Reina_combos#Mini-combos|+18c]]) with input F" +
                "\n* Transition to UNS (+0/+12/+18c) with u_d" +
                "\n* Cannot block up to i14 on empty transition on block\n&lt;/div&gt;"
        val expected = listOf(
            "Homing",
            "Deals chip damage on block",
            "Transition to SEN (+0/[+13](https://wavu.wiki/t/Reina_combos#Mini-combos)/[+18c](https://wavu.wiki/t/Reina_combos#Mini-combos)) with input F",
            "Transition to UNS (+0/+12/+18c) with u_d",
            "Cannot block up to i14 on empty transition on block"
        )

        //when
        val result = string.formNotes()

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region Parental
    @Test
    fun `formDataFromParent handles a single move`() {
        // given
        val move = MoveDto(
            id = "1",
            input = "1",
            startup = "i10",
            damage = "1",
            target = "h",
        )
        val map = mapOf(
            move.id to move,
        )
        val expected = ParentalProperties(
            input = "1",
            startup = "i10",
            damage = "1",
            guard = "h",
        )

        // when
        val result = move.formCompleteDataFromParent(map)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `formDataFromParent handles simple string`() {
        // given
        val move1 = MoveDto(
            id = "1",
            input = "1",
            startup = "i10",
            damage = "10",
            target = "h",
        )
        val move2 = MoveDto(
            id = "1,1",
            input = ",1",
            startup = "i11",
            parent = "1",
            damage = "11",
            target = "m"
        )
        val map = mapOf(
            move1.id to move1,
            move2.id to move2,
        )
        val expected = ParentalProperties(
            input = "11",
            startup = "i10 (i11)",
            damage = "10, 11",
            guard = "h, m",
        )

        // when
        val result = move2.formCompleteDataFromParent(map)

        //then
        assertThat(result).isEqualTo(expected)
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
}
