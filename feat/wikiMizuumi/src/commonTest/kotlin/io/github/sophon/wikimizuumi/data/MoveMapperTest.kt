package io.github.sophon.wikimizuumi.data

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class MoveMapperTest {
    @Test
    fun `formPropertiesUrl creates markdown`() {
        // given
        val string = "[[Melty Blood/MBTL/Glossary#Launch (L)|L]]"
        val expected = "[L](https://mizuumi.wiki/w/Melty_Blood/MBTL/Glossary#Launch_(L))"

        // when
        val result = string.formPropertiesUrl()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `formPropertuesUrl creates markdown from multiple url`() {
        // given
        val string = "[[Melty Blood/MBTL/Glossary#Launch (L)|L]], [[Melty Blood/MBTL/Glossary#Ground Bounce (GB)|GB]]\n" +
                "(second hit)"
        val expected = "[L](https://mizuumi.wiki/w/Melty_Blood/MBTL/Glossary#Launch_(L)), " +
                "[GB](https://mizuumi.wiki/w/Melty_Blood/MBTL/Glossary#Ground_Bounce_(GB))\n" +
                "(second hit)"

        // when
        val result = string.formPropertiesUrl()

        //then
        assertThat(result).isEqualTo(expected)
    }
}