package io.github.sophon.wiki.adapter.outbound.sqldelight.dustLoop

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.wiki.adapter.outbound.sqldelight.TestWikiDatabase
import io.github.sophon.wiki.model.game.GGCharProperties
import io.github.sophon.wiki.model.wiki.Game
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class DustLoopSqlDelightGamePropertiesTest {
    @Test
    fun `GGST character properties are loaded back`() = runTest {
        // given
        val database = TestWikiDatabase()
        val expected = listOf(kyKiske)

        // when
        database.save(kyKiske)

        // then
        val characterList = database.characterAdapter.subscribe(Game.GGST).first()
        assertThat(characterList).isEqualTo(expected)
    }

    @Test
    fun `GGST move properties are loaded back`() = runTest {
        // given
        val database = TestWikiDatabase()
        val expected = listOf(kyFarSlash)

        // when
        database.save(kyKiske, expected)

        // then
        val moveList = database.moveAdapter.subscribe(kyKiske.id).first()
        assertThat(moveList).isEqualTo(expected)
    }

    @Test
    fun `DBFZ character properties are loaded back`() = runTest {
        // given
        val database = TestWikiDatabase()
        val expected = listOf(android18)

        // when
        database.save(android18)

        // then
        val characterList = database.characterAdapter.subscribe(Game.DBFZ).first()
        assertThat(characterList).isEqualTo(expected)
    }

    @Test
    fun `DBFZ move properties are loaded back`() = runTest {
        // given
        val database = TestWikiDatabase()
        val expected = listOf(android18StandingLight)

        // when
        database.save(android18, expected)

        // then
        val moveList = database.moveAdapter.subscribe(android18.id).first()
        assertThat(moveList).isEqualTo(expected)
    }

    @Test
    fun `GBVSR character properties are loaded back`() = runTest {
        // given
        val database = TestWikiDatabase()
        val expected = listOf(gran)

        // when
        database.save(gran)

        // then
        val characterList = database.characterAdapter.subscribe(Game.GBVSR).first()
        assertThat(characterList).isEqualTo(expected)
    }

    @Test
    fun `GBVSR move properties are loaded back`() = runTest {
        // given
        val database = TestWikiDatabase()
        val expected = listOf(granCloseMedium)

        // when
        database.save(gran, expected)

        // then
        val moveList = database.moveAdapter.subscribe(gran.id).first()
        assertThat(moveList).isEqualTo(expected)
    }

    @Test
    fun `BBCF character properties are loaded back`() = runTest {
        // given
        val database = TestWikiDatabase()
        val expected = listOf(ragna)

        // when
        database.save(ragna)

        // then
        val characterList = database.characterAdapter.subscribe(Game.BBCF).first()
        assertThat(characterList).isEqualTo(expected)
    }

    @Test
    fun `BBCF move properties are loaded back`() = runTest {
        // given
        val database = TestWikiDatabase()
        val expected = listOf(ragnaStandingB)

        // when
        database.save(ragna, expected)

        // then
        val moveList = database.moveAdapter.subscribe(ragna.id).first()
        assertThat(moveList).isEqualTo(expected)
    }

    @Test
    fun `MTFS character properties are loaded back`() = runTest {
        // given
        val database = TestWikiDatabase()
        val expected = listOf(arizona)

        // when
        database.save(arizona)

        // then
        val characterList = database.characterAdapter.subscribe(Game.MTFS).first()
        assertThat(characterList).isEqualTo(expected)
    }

    @Test
    fun `MTFS move properties are loaded back`() = runTest {
        // given
        val database = TestWikiDatabase()
        val expected = listOf(arizonaStandingA)

        // when
        database.save(arizona, expected)

        // then
        val moveList = database.moveAdapter.subscribe(arizona.id).first()
        assertThat(moveList).isEqualTo(expected)
    }

    @Test
    fun `saving a character again updates its game properties`() = runTest {
        // given
        val database = TestWikiDatabase()
        val patchedKyKiske = kyKiske.copy(
            gameProperties = (kyKiske.gameProperties as GGCharProperties).copy(defense = "1.06", guts = "3"),
        )
        val expected = listOf(patchedKyKiske)

        // when
        database.save(kyKiske)
        database.save(patchedKyKiske)

        // then
        val characterList = database.characterAdapter.subscribe(Game.GGST).first()
        assertThat(characterList).isEqualTo(expected)
    }

    @Test
    fun `deleting a character deletes its character and move game properties`() = runTest {
        // given
        val database = TestWikiDatabase()
        database.save(kyKiske, listOf(kyFarSlash))
        val expected = 0L

        // when
        database.characterAdapter.delete(Game.GGST)

        // then
        assertThat(database.countRows("ggst_character")).isEqualTo(expected)
        assertThat(database.countRows("ggst_move")).isEqualTo(expected)
    }
}
