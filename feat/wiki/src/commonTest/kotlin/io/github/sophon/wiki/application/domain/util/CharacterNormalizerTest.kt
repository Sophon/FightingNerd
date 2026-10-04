package io.github.sophon.wiki.application.domain.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.wiki.application.domain.model.Character
import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.wiki.Game
import kotlin.test.Test

class CharacterNormalizerTest {

    //region id
    @Test
    fun `id is lowercase with spaces as underscores`() {
        //given
        val character = CharacterSource.armorKing
        val expected = CharacterId(Game.Tekken8, "armor_king")

        //when
        val result = character.normalize()

        //then
        assertThat(result.id).isEqualTo(expected)
    }

    @Test
    fun `id keeps hyphens`() {
        //given
        val character = CharacterSource.jack8
        val expected = CharacterId(Game.Tekken8, "jack-8")

        //when
        val result = character.normalize()

        //then
        assertThat(result.id).isEqualTo(expected)
    }
    //endregion

    //region aliases
    @Test
    fun `display name becomes a lowercase alias`() {
        //given
        val character = CharacterSource.armorKing
        val expected = listOf("ak", "aking", "armorking", "armor king")

        //when
        val result = character.normalize()

        //then
        assertThat(result.aliasList).isEqualTo(expected)
    }

    @Test
    fun `aliases are lowercased without blanks or duplicates`() {
        //given
        val character = CharacterSource.devilJin
        val expected = listOf("dvj", "dj", "deviljin", "devil jin")

        //when
        val result = character.normalize()

        //then
        assertThat(result.aliasList).isEqualTo(expected)
    }

    @Test
    fun `display name is left for the UI`() {
        //given
        val character = CharacterSource.armorKing
        val expected = "Armor King"

        //when
        val result = character.normalize()

        //then
        assertThat(result.displayName).isEqualTo(expected)
    }
    //endregion
}

/**
 * Characters as the Wavu adapter maps them - the id is the raw query name, aliases as the wiki lists them.
 */
private object CharacterSource {
    val armorKing = wavuCharacter(name = "Armor King", aliasList = listOf("ak", "aking", "armorking"))
    val jack8 = wavuCharacter(name = "Jack-8", aliasList = listOf("jack", "j8", "jack8"))
    val devilJin = wavuCharacter(name = "Devil Jin", aliasList = listOf("DVJ", " dj ", "", "deviljin", "dvj"))
}

private fun wavuCharacter(
    name: String,
    aliasList: List<String>,
): Character {
    val character = Character(
        id = CharacterId(Game.Tekken8, name),
        displayName = name,
        remoteQueryId = name,
        wikiUrl = "https://wavu.wiki/t/${name.replace(" ", "_")}_movelist",
        aliasList = aliasList,
    )
    return character
}
