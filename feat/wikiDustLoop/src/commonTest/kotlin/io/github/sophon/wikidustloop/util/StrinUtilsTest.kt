package io.github.sophon.wikidustloop.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class StringUtilsTest {
    @Test
    fun `toClickable transforms link`() {
        // given
        val string = "[[GGST/Sol Badguy/Frame Data#Gun Flame (Feint)|Link to Special Cancel into Gun Flame (Feint) Table]]"
        val expected = "[Link to Special Cancel into Gun Flame (Feint) Table](https://www.dustloop.com/w/GGST/Sol_Badguy/Frame_Data#Gun_Flame_(Feint))"

        // when
        val result = string.toClickable()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `toClickable only transforms relevant part`() {
        // given
        val string = "Damage changes with each set of 8 active frames.\n" +
                "[[GGST/Sol Badguy/Frame Data#Gun Flame|Link to Gun Flame Frame Advantage Table]]"
        val expected = "Damage changes with each set of 8 active frames.\n" +
                "[Link to Gun Flame Frame Advantage Table](https://www.dustloop.com/w/GGST/Sol_Badguy/Frame_Data#Gun_Flame)"

        // when
        val result = string.toClickable()

        //then
        assertThat(result).isEqualTo(expected)
    }
}