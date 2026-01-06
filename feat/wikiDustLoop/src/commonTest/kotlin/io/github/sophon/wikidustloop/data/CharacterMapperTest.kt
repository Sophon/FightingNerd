package io.github.sophon.wikidustloop.data

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.feature.Game
import kotlin.test.Test

class CharacterMapperTest {
    val gg = Game.GGST.id
    val bb = Game.BBCF.id
    
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
    fun `formWikiUrl handles apostrophe`() {
        // given
        val char = "Susano&#039;o"
        val expected = "https://www.dustloop.com/w/BBCF/Susano%27o"

        // when
        val result = char.formWikiUrl(Game.BBCF.id)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `formWikiUrl handles dots`() {
        // given
        val char = "Valkenhayn R. Hellsing"
        val expected = "https://www.dustloop.com/w/BBCF/Valkenhayn_R._Hellsing"

        // when
        val result = char.formWikiUrl(Game.BBCF.id)

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
        val result = char.createAliases(gg)

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
        val result = char.createAliases(gg)

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
        val result1 = char1.createAliases(gg)
        val result2 = char2.createAliases(gg)

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
        val result = char.createAliases(gg)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `createAliases removes numbers`() {
        //given
        val char = "Zato-1"
        val expected = listOf("zato")

        //when
        val result = char.createAliases(gg)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `create aliases for BB handles dots`() {
        // given
        val char = "Celica A. Mercury"
        val expected = listOf(
            "celica",
            "ce",
        )

        // when
        val result = char.createAliases(bb)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `create aliases for BB handles hyphens`() {
        // given
        val char = "Lambda-11"
        val expected = listOf(
            "lambda",
            "rm",
        )

        // when
        val result = char.createAliases(bb)
        
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

    //region formNotes
    @Test
    fun `formNotes handles html`() {
        //given
        val string = "Retracts lower hurtbox 1-2F, slightly retracts further 3-18F;\nStaggers on grounded counter hit.; " +
                "Although the counterhit slowdown and stagger recovery overlap, the slowdown ends before the opponent can begin blocking," +
                " so the 25F stagger can be treated as if it were 31F.; Total stagger duration: 31F (1-23F hitstun, 24-31F can block only);" +
                " Hitstun duration is increased by 4F for red (normal) recovery, and 8F for blue (slow) and no recovery. (27F and 31F respectively);" +
                " Total duration is increased by 5F  for red (normal) recovery, and 10F for blue (slow) and no recovery. (36F and 41F respectively);" +
                " Opponents can be thrown while staggered, allowing comboing into throws. During the &#039;can only block&#039; stage at the end" +
                " of stagger, the opponent cannot jump to escape throws, meaning there is more time to combo into throws than strikes.; " +
                "Stagger does not gain 1 additional hitstun on crouching hit, unlike other attacks.;"
        val expected = listOf(
            "Retracts lower hurtbox 1-2F, slightly retracts further 3-18F",
            "Staggers on grounded counter hit.",
            "Although the counterhit slowdown and stagger recovery overlap, the slowdown ends before the opponent can begin blocking," +
                    " so the 25F stagger can be treated as if it were 31F.",
            "Total stagger duration: 31F (1-23F hitstun, 24-31F can block only)",
            "Hitstun duration is increased by 4F for red (normal) recovery, and 8F for blue (slow) and no recovery. (27F and 31F respectively)",
            "Total duration is increased by 5F  for red (normal) recovery, and 10F for blue (slow) and no recovery. (36F and 41F respectively)",
            "Opponents can be thrown while staggered, allowing comboing into throws. During the 'can only block' stage at the " +
                    "end of stagger, the opponent cannot jump to escape throws, meaning there is more time to combo into throws than strikes.",
            "Stagger does not gain 1 additional hitstun on crouching hit, unlike other attacks.",
        )

        //when
        val result = string.formNotes()

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion
}