package io.github.sophon.wiki.adapter.outbound.sqldelight.dragDown

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.wiki.adapter.outbound.sqldelight.TestWikiDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class DragDownSqlDelightGamePropertiesTest {
    @Test
    fun `RoA2 character properties are loaded back`() = runTest {
        // given
        val database = TestWikiDatabase()
        val expected = listOf(clairen)

        // when
        database.save(Game.ROA2, clairen)

        // then
        val characterList = database.characterAdapter.subscribe(Game.ROA2).first()
        assertThat(characterList).isEqualTo(expected)
    }

    @Test
    fun `RoA2 move properties are loaded back`() = runTest {
        // given
        val database = TestWikiDatabase()
        val expected = listOf(clairenJab)

        // when
        database.save(Game.ROA2, clairen, expected)

        // then
        val moveList = database.moveAdapter.subscribe(Game.ROA2, clairen.id).first()
        assertThat(moveList).isEqualTo(expected)
    }
}
