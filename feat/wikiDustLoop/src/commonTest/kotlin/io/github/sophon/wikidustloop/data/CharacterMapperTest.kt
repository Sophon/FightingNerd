package io.github.sophon.wikidustloop.data

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.feature.Game
import kotlin.test.Test

class CharacterMapperTest {
    @Test
    fun `filterJunkChars filters out names with parentheses`() {
        //given
        val chars = listOf(
            CargoQueryItem(
                title = CharacterDto(
                    name = "Sol Badguy",
                ),
            ),
            CargoQueryItem(
                title = CharacterDto(
                    name = "Giovanna (100% Tension)",
                ),
            ),
            CargoQueryItem(
                title = CharacterDto(
                    name = "Perry Perynthesis (",
                ),
            ),
            CargoQueryItem(
                title = CharacterDto(
                    name = "Jack-O (Backward Dash)",
                ),
            ),
        )
        val expected = listOf(
            CargoQueryItem(
                title = CharacterDto(
                    name = "Sol Badguy",
                ),
            ),
            CargoQueryItem(
                title = CharacterDto(
                    name = "Perry Perynthesis (",
                ),
            ),
        )

        //when
        val result = chars.filterOutJunkCharacters()

        //then
        assertThat(result).isEqualTo(expected)
    }

    //region ID
    @Test
    fun `formId handles standard name`() {
        //given
        val char = "Giovanna"
        val expected = "giovanna"

        //when
        val result = char.formCharacterId()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `formId handles name with space`() {
        //given
        val char = "Sol Badguy"
        val expected = "sol_badguy"

        //when
        val result = char.formCharacterId()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `formId handles name with dots`() {
        //given
        val char = "A.B.A"
        val expected = "aba"

        //when
        val result = char.formCharacterId()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `formId removes special symbols`() {
        //given
        val char1 = "Jack-O'"
        val char2 = "Bedman?"
        val expected1 = "jacko"
        val expected2 = "bedman"

        //when
        val result1 = char1.formCharacterId()
        val result2 = char2.formCharacterId()

        //then
        assertThat(result1).isEqualTo(expected1)
        assertThat(result2).isEqualTo(expected2)
    }
    //endregion

    //region query
    @Test
    fun `formQuery keeps spaces`() {
        //given
        val char = "Sol Badguy"
        val expected = "Sol Badguy"

        //when
        val result = char.formCharacterQueryName()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `formQuery handles name with special symbols`() {
        //given
        val char1 = "Jack-O'"
        val char2 = "Bedman?"
        val expected1 = "Jack-O"
        val expected2 = "Bedman"

        //when
        val result1 = char1.formCharacterQueryName()
        val result2 = char2.formCharacterQueryName()

        //then
        assertThat(result1).isEqualTo(expected1)
        assertThat(result2).isEqualTo(expected2)
    }
    //endregion

    //region wiki url
    @Test
    fun `formWikiUrl handles standard name`() {
        //given
        val char = "Slayer"
        val expected = "https://www.dustloop.com/w/GGST/Slayer"

        //when
        val result = char.formWikiUrl(Game.GGST.id)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `formWikiUrl handles spaces`() {
        //given
        val char = "Sol Badguy"
        val expected = "https://www.dustloop.com/w/GGST/Sol_Badguy"

        //when
        val result = char.formWikiUrl(Game.GGST.id)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `formWikiUrl handles special symbols`() {
        //given
        val char = "A.B.A"
        val expected = "https://www.dustloop.com/w/GGST/A.B.A"

        //when
        val result = char.formWikiUrl(Game.GGST.id)

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion
    
    //region aliases
    @Test
    fun `createAliases handles one word name`() {
        //given
        val char = "Nagoriyuki"
        val expected = listOf<String>()
        
        //when
        val result = char.createAliases()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `createAliases handles two word names`() {
        //given
        val char = "Ky Kiske"
        val expected = listOf(
            "kk",
            "ky",
            "kiske",
        )

        //when
        val result = char.createAliases()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `createAliases handles special chars`() {
        //given
        val char1 = "Jack-O"
        val expected1 = listOf<String>()
        val char2 = "A.B.A"
        val expected2 = listOf<String>()
        
        //when
        val result1 = char1.createAliases()
        val result2 = char2.createAliases()

        //then
        assertThat(result1).isEqualTo(expected1)
        assertThat(result2).isEqualTo(expected2)
    }

    @Test
    fun `createAliases only forms initials from multi char word`() {
        //given
        val char = "Asuka R"
        val expected = listOf(
            "ar",
            "asuka",
        )

        //when
        val result = char.createAliases()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `createAliases removes numbers`() {
        //given
        val char = "Zato-1"
        val expected = listOf(
            "zato",
        )

        //when
        val result = char.createAliases()

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region toClickable
    @Test
    fun `toClickable handles no link`() {
        //given
        val string = "Step-Dash (15F)"
        val expected = listOf("Step-Dash (15F)")

        //when
        val result = string.toClickable()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `toClickable handles link`() {
        //given
        val string = "[[GGST/Baiken#Kabari|[H] Kabari follow-up]]"
        val expected = listOf(
            "[[H] Kabari follow-up](https://www.dustloop.com/w/GGST/Baiken#Kabari)",
        )

        //when
        val result = string.toClickable()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `toClickable handles multiple links`() {
        //given
        val string = "Step-Dash (15F), [[GGST/Johnny#Mist Finer Stance|Mist Finer Dash]], [[GGST/Johnny#Vault|Vault]]"
        val expected = listOf(
            "Step-Dash (15F)",
            "[Mist Finer Dash](https://www.dustloop.com/w/GGST/Johnny#Mist_Finer_Stance)",
            "[Vault](https://www.dustloop.com/w/GGST/Johnny#Vault)",
        )

        //when
        val result = string.toClickable()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `toClickable ignores blank or null`() {
        //given
        val string1: String? = null
        val string2 = ""
        val expected1 = listOf<String>()
        val expected2 = listOf<String>()

        //when
        val result1 = string1.toClickable()
        val result2 = string2.toClickable()

        //then
        assertThat(result1).isEqualTo(expected1)
        assertThat(result2).isEqualTo(expected2)
    }
    //endregion
}