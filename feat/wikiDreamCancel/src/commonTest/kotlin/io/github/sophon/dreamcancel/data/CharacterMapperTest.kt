package io.github.sophon.dreamcancel.data

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.wiki.domain.model.Character
import kotlin.test.Test

class CharacterMapperTest {
    //region Alias Generation Tests
    @Test
    fun `toDomain should create empty aliases for single word name`() {
        // given
        val characterName = "King"
        val expectedCharacter = Character(
            id = "king",
            displayName = "King",
            queryName = "King",
            wikiUrl = "https://dreamcancel.com/wiki/The_King_of_Fighters_XV/King",
            aliasList = emptyList()
        )

        // when
        val result = characterName.toDomain(TAG_KOF)

        // then
        assertThat(result).isEqualTo(expectedCharacter)
    }

    @Test
    fun `toDomain should create aliases for two word name`() {
        // given
        val characterName = "Andy Bogard"
        val expectedCharacter = Character(
            id = "andy_bogard",
            displayName = "Andy Bogard",
            queryName = "Andy_Bogard",
            wikiUrl = "https://dreamcancel.com/wiki/The_King_of_Fighters_XV/Andy_Bogard",
            aliasList = listOf("andy", "bogard", "ab")
        )

        // when
        val result = characterName.toDomain(TAG_KOF)

        // then
        assertThat(result).isEqualTo(expectedCharacter)
    }

    @Test
    fun `toDomain should create aliases for three word name with short word`() {
        // given
        val characterName = "King of Dinosaurs"
        val expectedCharacter = Character(
            id = "king_of_dinosaurs",
            displayName = "King of Dinosaurs",
            queryName = "King_of_Dinosaurs",
            wikiUrl = "https://dreamcancel.com/wiki/The_King_of_Fighters_XV/King_of_Dinosaurs",
            aliasList = listOf("king", "dinosaurs", "of", "kod")
        )

        // when
        val result = characterName.toDomain(TAG_KOF)

        // then
        assertThat(result).isEqualTo(expectedCharacter)
    }

    @Test
    fun `toDomain should handle duplicate words in name`() {
        // given
        val characterName = "Sylvie Paula Paula"
        val expectedCharacter = Character(
            id = "sylvie_paula_paula",
            displayName = "Sylvie Paula Paula",
            queryName = "Sylvie_Paula_Paula",
            wikiUrl = "https://dreamcancel.com/wiki/The_King_of_Fighters_XV/Sylvie_Paula_Paula",
            aliasList = listOf("sylvie", "paula", "spp")
        )

        // when
        val result = characterName.toDomain(TAG_KOF)

        // then
        assertThat(result).isEqualTo(expectedCharacter)
    }

    @Test
    fun `toDomain should filter out single character words from aliases`() {
        // given
        val characterName = "K&#039;"
        val expectedCharacter = Character(
            id = "k",
            displayName = "K'",
            queryName = "K'",
            wikiUrl = "https://dreamcancel.com/wiki/The_King_of_Fighters_XV/K'",
            aliasList = listOf()
        )

        // when
        val result = characterName.toDomain(TAG_KOF)

        // then
        assertThat(result).isEqualTo(expectedCharacter)
    }

    @Test
    fun `toDomain should handle accented characters`() {
        // given
        val characterName = "Ángel"
        val expectedCharacter = Character(
            id = "angel",
            displayName = "Ángel",
            queryName = "Angel",
            wikiUrl = "https://dreamcancel.com/wiki/The_King_of_Fighters_XV/Angel",
            aliasList = emptyList()
        )

        // when
        val result = characterName.toDomain(TAG_KOF)

        // then
        assertThat(result).isEqualTo(expectedCharacter)
    }

    @Test
    fun `toDomain should handle period in name`() {
        // given
        val characterName = "B.Jenet"
        val expectedCharacter = Character(
            id = "b.jenet",
            displayName = "B.Jenet",
            queryName = "B.Jenet",
            wikiUrl = "https://dreamcancel.com/wiki/The_King_of_Fighters_XV/B.Jenet",
            aliasList = listOf("jenet", "bj")
        )

        // when
        val result = characterName.toDomain(TAG_KOF)

        // then
        assertThat(result).isEqualTo(expectedCharacter)
    }
    //endregion

    private companion object {
        const val TAG_KOF = "The_King_of_Fighters_XV"
    }
}