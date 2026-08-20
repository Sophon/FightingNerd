package io.github.sophon.core.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.util.findMatching
import kotlin.test.Test

class CharacterUtilsTest {
    private val jin = Character(
        id = "jin",
        displayName = "Jin",
        remoteQueryId = "",
        wikiUrl = "",
        aliasList = listOf("jim"),
    )
    private val hwoarang = Character(
        id = "hwoarang",
        displayName = "Hwoarang",
        remoteQueryId = "",
        wikiUrl = "",
        aliasList = listOf("hwo"),
    )
    private val raven = Character(
        id = "raven",
        displayName = "Raven",
        remoteQueryId = "",
        wikiUrl = "",
        aliasList = listOf("maven", "masterraven", "mrv"),
    )

    private val characterList = listOf(jin, hwoarang, raven)

    //region Match by id
    @Test
    fun `findMatching matches by id when query equals id exactly`() {
        // Given
        val query = "jin"
        val expected = jin

        // When
        val result = characterList.findMatching(query)

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `findMatching matches by id after normalizing query case and spaces`() {
        // Given
        val query = "H W O A R A N G"
        val expected = hwoarang

        // When
        val result = characterList.findMatching(query)

        // Then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region Match by aliasList
    @Test
    fun `findMatching matches by alias ignoring case and spaces`() {
        // Given
        val query = "MASTER RAVEN"
        val expected = raven

        // When
        val result = characterList.findMatching(query)

        // Then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region No match
    @Test
    fun `findMatching returns null when no field matches`() {
        // Given
        val query = "chunli"

        // When
        val result = characterList.findMatching(query)

        // Then
        assertThat(result).isNull()
    }
    //endregion
}
