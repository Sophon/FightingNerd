package io.github.sophon.wiki.adapter.outbound.sqldelight.mizuumi

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.wiki.adapter.outbound.sqldelight.TestWikiDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class MizuumiSqlDelightGamePropertiesTest {
    @Test
    fun `MBTL move properties are loaded back`() = runTest {
        // given
        val database = TestWikiDatabase()
        val expected = listOf(arcueidStandingA)

        // when
        database.save(Game.MBTL, arcueid, expected)

        // then
        val moveList = database.moveAdapter.subscribe(Game.MBTL, arcueid.id).first()
        assertThat(moveList).isEqualTo(expected)
    }

    @Test
    fun `UNI2 character properties are loaded back`() = runTest {
        // given
        val database = TestWikiDatabase()
        val expected = listOf(hyde)

        // when
        database.save(Game.Uni2, hyde)

        // then
        val characterList = database.characterAdapter.subscribe(Game.Uni2).first()
        assertThat(characterList).isEqualTo(expected)
    }

    @Test
    fun `UNI2 move properties are loaded back`() = runTest {
        // given
        val database = TestWikiDatabase()
        val expected = listOf(hydeStandingA)

        // when
        database.save(Game.Uni2, hyde, expected)

        // then
        val moveList = database.moveAdapter.subscribe(Game.Uni2, hyde.id).first()
        assertThat(moveList).isEqualTo(expected)
    }

    @Test
    fun `VSAV move properties are loaded back`() = runTest {
        // given
        val database = TestWikiDatabase()
        val expected = listOf(morriganStandingJab)

        // when
        database.save(Game.VSAV, morrigan, expected)

        // then
        val moveList = database.moveAdapter.subscribe(Game.VSAV, morrigan.id).first()
        assertThat(moveList).isEqualTo(expected)
    }
}
