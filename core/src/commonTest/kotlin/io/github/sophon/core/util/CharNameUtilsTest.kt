package io.github.sophon.core.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class CharNameUtilsTest {
    //region Alias handling
    @Test
    fun `createAliases handles names with spaces`() {
        //given
        val characterName = "B. Jenet"
        val expected = listOf(
            "bj",
            "jenet",
        )

        //when
        val result = characterName.createAliases()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `createAliases handles single word names`() {
        //given
        val characterName = "King"
        val expected = listOf<String>()

        //when
        val result = characterName.createAliases()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `createAliases handles two word names`() {
        // given
        val characterName = "Andy Bogard"
        val expected = listOf(
            "ab",
            "andy",
            "bogard",
        )

        //when
        val result = characterName.createAliases()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `createAliases handles three word names`() {
        //given
        val characterName = "King of Dinosaurs"
        val expected = listOf(
            "kod",
            "king",
            "dinosaurs",
        )

        //when
        val result = characterName.createAliases()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `createAliases handles duplicate words`() {
        // given
        val characterName = "Sylvie Paula Paula"
        val expected = listOf(
            "spp",
            "sylvie",
            "paula",
        )

        //when
        val result = characterName.createAliases()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `createAliases handles three words`() {
        //given
        val characterName = "King of Dinosaurs"
        val expected = listOf(
            "kod",
            "king",
            "dinosaurs",
        )

        //when
        val result = characterName.createAliases()

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion
}