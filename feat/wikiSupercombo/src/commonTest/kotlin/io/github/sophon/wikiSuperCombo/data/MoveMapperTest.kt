package io.github.sophon.wikiSuperCombo.data

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class MoveMapperTest {
    val gameId = "Street_Fighter_6"
    
    //region formMoveWikiUrl
    @Test
    fun `formUrl handles basic url`() {
        //given
        val charName = "A.K.I."
        val input = "2MP"
        val name = null
        val expected = "https://wiki.supercombo.gg/w/Street_Fighter_6/A.K.I.#2MP"
        
        //when
        val result = formMoveWikiUrl(gameId, charName, input, name)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `formUrl handles move with name`() {
        //given
        val charName = "Chun-Li"
        val input = "214P~MK"
        val name = "Senpu Kick"
        val expected = "https://wiki.supercombo.gg/w/Street_Fighter_6/Chun-Li#Senpu_Kick_(214P~MK)"

        //when
        val result = formMoveWikiUrl(gameId, charName, input, name)

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion
}