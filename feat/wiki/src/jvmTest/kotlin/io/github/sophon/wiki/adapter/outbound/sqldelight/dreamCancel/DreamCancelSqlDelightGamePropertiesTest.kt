package io.github.sophon.wiki.adapter.outbound.sqldelight.dreamCancel

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.wiki.adapter.outbound.sqldelight.TestWikiDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class DreamCancelSqlDelightGamePropertiesTest {
    @Test
    fun `KoF XV move properties are loaded back`() = runTest {
        // given
        val database = TestWikiDatabase()
        val expected = listOf(kyoCloseC)

        // when
        database.save(kyo, expected)

        // then
        val moveList = database.moveAdapter.subscribe(kyo.id).first()
        assertThat(moveList).isEqualTo(expected)
    }

    @Test
    fun `COTW move properties are loaded back`() = runTest {
        // given
        val database = TestWikiDatabase()
        val expected = listOf(terryStandingC)

        // when
        database.save(terry, expected)

        // then
        val moveList = database.moveAdapter.subscribe(terry.id).first()
        assertThat(moveList).isEqualTo(expected)
    }
}
