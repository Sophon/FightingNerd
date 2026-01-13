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
        val string = "c.m"
        val expected = listOf<String>()

        //when
        val result = string.formAliases(gb)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `formAliases handles form input`() {
        //given
        val string1 = "f.l[k]"
        val string2 = "c.m[ex]"
        val expected1 = listOf("k.f.l")
        val expected2 = listOf("ex.c.m")

        //when
        val result1 = string1.formAliases(gb)
        val result2 = string2.formAliases(gb)

        //then
        assertThat(result1).isEqualTo(expected1)
        assertThat(result2).isEqualTo(expected2)
    }

    @Test
    fun `formAliases handles empty form input`() {
        //given
        val string = "f.l[]"
        val expected = listOf<String>()

        //when
        val result = string.formAliases(gb)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `formAliases handles slash inputs`() {
        // given
        val string = "236S/H/D"
        val expected = listOf("236s", "236h", "236d")

        // when
        val result = string.formAliases(gg)

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    @Test
    fun `formWikiUrl handles spaces in inputs`() {
        // given
        val string = "5D Bat"
        val expected = "https://www.dustloop.com/w/BBCF/Platinum_the_Trinity#5D_Bat"
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