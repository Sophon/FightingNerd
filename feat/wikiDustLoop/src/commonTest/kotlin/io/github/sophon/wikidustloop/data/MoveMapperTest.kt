package io.github.sophon.wikidustloop.data

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.feature.Game
import kotlin.test.Test

class MoveMapperTest {
    val gb = Game.GBVSR.id
    val gg = Game.GGST.id
    val bb = Game.BBCF.id

    //region Aliases
    @Test
    fun `formAliases handles regular input`() {
        //given
        val input = "c.m"
        val expected = listOf("cm")

        //when
        val result = formAliases(gb, input, "")

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `formAliases handles form input`() {
        //given
        val input1 = "f.l[k]"
        val input2 = "c.m[ex]"
        val expected1 = listOf("k.f.l", "fl[k]")
        val expected2 = listOf("ex.c.m", "cm[ex]")

        //when
        val result1 = formAliases(gb, input1, "")
        val result2 = formAliases(gb, input2, "")

        //then
        assertThat(result1).isEqualTo(expected1)
        assertThat(result2).isEqualTo(expected2)
    }

    @Test
    fun `formAliases handles empty form input`() {
        //given
        val input = "f.l[]"
        val expected = listOf("fl[]")

        //when
        val result = formAliases(gb, input, "")

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `formAliases handles slash inputs`() {
        // given
        val input = "236S/H/D"
        val expected = listOf("236s", "236h", "236d")

        // when
        val result = formAliases(gg, input, "")

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `formAliases handles Nago inputs`() {
        // given
        val input1 = "2h level br"
        val input2 = "2H Level 1"
        val input3 = "2s level 3"
        val expected = listOf("2hb", "2h", "2s3")

        // when
        val result = listOf(input1, input2, input3).flatMap { it.formNagoriyukiAliases() }

        //then
        assertThat(result).isEqualTo(expected)
    }
    
    @Test
    fun `formAliases handles OR input`() {
        // given
        val input = "j.6d or j.4d"
        val expected = listOf(
            "j.6d",
            "j.4d",
            "j6d or j4d",
            "j6d",
            "j4d",
        )
        
        // when
        val result = formAliases(gg, input, "")

        //then
        assertThat(result).isEqualTo(expected)
    }
    
    @Test
    fun `formAliases handles gb variant inputs`() {
        // given
        val input = "22m~l/m"
        val expected = listOf("22m~l", "22m~m")
        
        // when
        val result = formAliases(gb, input, "")

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `formAliases handles release notation`() {
        // given
        val input = "214]p["
        val expected = listOf("214p")

        // when
        val result = formAliases(gg, input, "")

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    @Test
    fun `formWikiUrl handles spaces in inputs`() {
        // given
        val string = "5D Bat"
        val expected = "https://www.dustloop.com/w/BBCF/Platinum_the_Trinity#Magical_Bat"
        val dto = MoveDto(
            chara = "Platinum the Trinity",
            name = "Magical Bat",
            input = "5D Bat",
            damage = "900",
            guard = "Mid",
            startup = "11",
            active = "4",
            recovery = "26",
            onBlock = "-16",
            onODR = "-14",
            attribute = "B",
            invuln = "1~11 All",
            cancel = "(S)R",
            p1 = "60",
            p2 = "75",
            starter = "Very Short",
            level = "2",
            blockstun = "13",
            groundHit = "Launch",
            airHit = "30 + WStick 25",
            groundCH = "Launch",
            airCH = "42 + WBounce 40 + WStick 25",
            blockstop = "22",
            hitstop = "+0",
            CHstop = "+1",
            cancelTiming = null,
            images = "BBCS_Platinum_homerun5D.png",
            caption = "Also good in combos",
            hitboxes = "BBCF Platinum homerun5D hitbox 1.png;BBCF Platinum homerun5D hitbox 2.png",
            hitboxCaption = "Ground, frame 11\\Ground, frames 12-14",
            type = "drive",
            notes = "Counter Hit state for entire move; Reversal"
        )

        // when
        val result = formMoveWikiUrl(bb, dto)

        //then
        assertThat(result).isEqualTo(expected)
    }
}