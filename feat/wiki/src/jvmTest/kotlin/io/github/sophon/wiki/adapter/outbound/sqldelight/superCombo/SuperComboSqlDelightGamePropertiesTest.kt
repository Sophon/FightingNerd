package io.github.sophon.wiki.adapter.outbound.sqldelight.superCombo

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.wiki.adapter.outbound.sqldelight.TestWikiDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class SuperComboSqlDelightGamePropertiesTest {
    @Test
    fun `SF6 character properties are loaded back`() = runTest {
        // given
        val database = TestWikiDatabase()
        val expected = listOf(ryu)

        // when
        database.save(Game.StreetFighter6, ryu)

        // then
        val characterList = database.characterAdapter.subscribe(Game.StreetFighter6).first()
        assertThat(characterList).isEqualTo(expected)
    }

    @Test
    fun `SF6 move properties are loaded back`() = runTest {
        // given
        val database = TestWikiDatabase()
        val expected = listOf(ryuStandingMediumPunch)

        // when
        database.save(Game.StreetFighter6, ryu, expected)

        // then
        val moveList = database.moveAdapter.subscribe(Game.StreetFighter6, ryu.id).first()
        assertThat(moveList).isEqualTo(expected)
    }

    @Test
    fun `MK1 character properties are loaded back`() = runTest {
        // given
        val database = TestWikiDatabase()
        val expected = listOf(liuKang)

        // when
        database.save(Game.MK1, liuKang)

        // then
        val characterList = database.characterAdapter.subscribe(Game.MK1).first()
        assertThat(characterList).isEqualTo(expected)
    }

    @Test
    fun `MK1 move properties are loaded back`() = runTest {
        // given
        val database = TestWikiDatabase()
        val expected = listOf(liuKangBackOne)

        // when
        database.save(Game.MK1, liuKang, expected)

        // then
        val moveList = database.moveAdapter.subscribe(Game.MK1, liuKang.id).first()
        assertThat(moveList).isEqualTo(expected)
    }

    @Test
    fun `AVL move properties are loaded back`() = runTest {
        // given
        val database = TestWikiDatabase()
        val expected = listOf(aangStandingLight)

        // when
        database.save(Game.AVL, aang, expected)

        // then
        val moveList = database.moveAdapter.subscribe(Game.AVL, aang.id).first()
        assertThat(moveList).isEqualTo(expected)
    }
}
