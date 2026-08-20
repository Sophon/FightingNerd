package io.github.sophon.core.util

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.util.filterMatching
import io.github.sophon.core.wiki.util.findMatching
import kotlin.test.Test

class MoveUtilsTest {
    //region SETUP
    private val df1 = Move(
        characterId = "",
        id = "df1",
        input = "df+1",
        urls = Move.Urls(wikiUrl = ""),
    )
    private val ws12 = Move(
        characterId = "",
        id = "ws12",
        input = "WS1+2",
        urls = Move.Urls(wikiUrl = ""),
    )
    private val cd2 = Move(
        characterId = "",
        id = "cd2",
        input = "f,n,d,df+2",
        name = "Wind God Fist",
        aliases = listOf("wgf"),
        urls = Move.Urls(wikiUrl = ""),
    )
    private val electric = Move(
        characterId = "",
        id = "cd#2",
        input = "f,n,d,df#2",
        name = "Electric Wind God Fist",
        aliases = listOf("ewgf", "electric"),
        urls = Move.Urls(wikiUrl = ""),
    )
    private val jab = Move(
        characterId = "",
        id = "1",
        input = "1",
        urls = Move.Urls(wikiUrl = ""),
    )
    private val moveList = listOf(df1, ws12, cd2, electric, jab)
    //endregion

    //region findMatching by id
    @Test
    fun `findMatching matches by id when query equals id exactly`() {
        // Given
        val query = "df1"
        val expected = df1

        // When
        val result = moveList.findMatching(query)

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `findMatching matches by id after normalizing query case and spaces`() {
        // Given
        val query = "D F 1"
        val expected = df1

        // When
        val result = moveList.findMatching(query)

        // Then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region findMatching by input
    @Test
    fun `findMatching matches by input ignoring case`() {
        // Given
        val query = "ws1+2"
        val expected = ws12

        // When
        val result = moveList.findMatching(query)

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `findMatching matches by input ignoring spaces`() {
        // Given
        val query = "f, n, d, df+2"
        val expected = cd2

        // When
        val result = moveList.findMatching(query)

        // Then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region findMatching by aliases
    @Test
    fun `findMatching matches by alias ignoring case and spaces`() {
        // Given
        val query = "EWGF"
        val expected = electric

        // When
        val result = moveList.findMatching(query)

        // Then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region findMatching by name
    @Test
    fun `findMatching matches by name ignoring case and spaces`() {
        // Given
        val query = "WIND GOD FIST"
        val expected = cd2

        // When
        val result = moveList.findMatching(query)

        // Then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region findMatching no match
    @Test
    fun `findMatching returns null when no field matches`() {
        // Given
        val query = "shoryuken"

        // When
        val result = moveList.findMatching(query)

        // Then
        assertThat(result).isNull()
    }
    //endregion

    //region filterMatching
    @Test
    fun `filterMatching returns entire list when query is null`() {
        // Given
        val query: String? = null
        val expected = moveList

        // When
        val result = moveList.filterMatching(query)

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `filterMatching matches by id case-insensitively`() {
        // Given
        val query = "DF1"
        val expected = listOf(df1)

        // When
        val result = moveList.filterMatching(query)

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `filterMatching matches by input prefix case-insensitively`() {
        // Given
        val query = "WS1"
        val expected = listOf(ws12)

        // When
        val result = moveList.filterMatching(query)

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `filterMatching matches by alias substring case-insensitively`() {
        // Given
        val query = "LEC"
        val expected = listOf(electric)

        // When
        val result = moveList.filterMatching(query)

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `filterMatching matches by name substring case-insensitively`() {
        // Given
        val query = "wind"
        val expected = listOf(cd2, electric)

        // When
        val result = moveList.filterMatching(query)

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `filterMatching returns multiple moves when query matches different fields`() {
        // Given
        val query = "d"
        val expected = listOf(df1, cd2, electric)

        // When
        val result = moveList.filterMatching(query)

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `filterMatching returns empty list when nothing matches`() {
        // Given
        val query = "shoryuken"

        // When
        val result = moveList.filterMatching(query)

        // Then
        assertThat(result).isEmpty()
    }
    //endregion
}
