package io.github.sophon.dreamcancel.data

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class CharacterMapperTest {
    //region Alias handling
    @Test
    fun `createAliases handles names with spaces`() {
        //given
        val characterName = "B. Jenet"
        val expected = listOf(
            "jenet",
            "bj",
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
            "andy",
            "bogard",
            "ab",
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
            "king",
            "dinosaurs",
            "kod",
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
            "sylvie",
            "paula",
            "spp",
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
            "king",
            "dinosaurs",
            "kod",
        )

        //when
        val result = characterName.createAliases()

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region query name
    @Test
    fun `createQueryName handles names with spaces`() {
        val characterName = "B. Jenet"
        val expected = "B._Jenet"

        //when
        val result = characterName.createQueryName()

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion
}