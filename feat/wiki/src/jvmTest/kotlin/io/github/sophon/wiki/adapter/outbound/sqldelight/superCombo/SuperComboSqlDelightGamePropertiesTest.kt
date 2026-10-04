package io.github.sophon.wiki.adapter.outbound.sqldelight.superCombo

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.wiki.adapter.outbound.sqldelight.TestWikiDatabase
import io.github.sophon.wiki.application.domain.model.wiki.Game
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
        database.save(ryu)

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
        database.save(ryu, expected)

        // then
        val moveList = database.moveAdapter.subscribe(ryu.id).first()
        assertThat(moveList).isEqualTo(expected)
    }

    @Test
    fun `MK1 character properties are loaded back`() = runTest {
        // given
        val database = TestWikiDatabase()
        val expected = listOf(liuKang)

        // when
        database.save(liuKang)

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
        database.save(liuKang, expected)

        // then
        val moveList = database.moveAdapter.subscribe(liuKang.id).first()
        assertThat(moveList).isEqualTo(expected)
    }

    @Test
    fun `AVL move properties are loaded back`() = runTest {
        // given
        val database = TestWikiDatabase()
        val expected = listOf(aangStandingLight)

        // when
        database.save(aang, expected)

        // then
        val moveList = database.moveAdapter.subscribe(aang.id).first()
        assertThat(moveList).isEqualTo(expected)
    }
}
