package io.github.sophon.wiki.adapter.outbound.sqldelight.wavu

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.wiki.adapter.outbound.sqldelight.TestWikiDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class WavuSqlDelightGamePropertiesTest {
    @Test
    fun `Tag 2 move properties are loaded back`() = runTest {
        // given
        val database = TestWikiDatabase()
        val expected = listOf(tag2HwoarangRff1)

        // when
        database.save(tag2Hwoarang, expected)

        // then
        val moveList = database.moveAdapter.subscribe(tag2Hwoarang.id).first()
        assertThat(moveList).isEqualTo(expected)
    }
}
