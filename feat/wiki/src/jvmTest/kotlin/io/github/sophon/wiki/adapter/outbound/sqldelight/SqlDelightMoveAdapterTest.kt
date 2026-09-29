package io.github.sophon.wiki.adapter.outbound.sqldelight

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import io.github.sophon.core.featureConfig.model.Game
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

internal class SqlDelightMoveAdapterTest {
    @Test
    fun `saved moves are loaded in wiki order with their game properties`() = runTest {
        // given
        val database = TestWikiDatabase()
        val expected = listOf(windHookFist, demonsPaw, midLeftPunch, spinningSidekick)

        // when
        database.save(Game.Tekken8, jin, expected)

        // then
        val moveList = database.moveAdapter.subscribe(Game.Tekken8, jin.id).first()
        assertThat(moveList).isEqualTo(expected)
    }

    @Test
    fun `a move missing from four saves is kept`() = runTest {
        // given
        val database = TestWikiDatabase()
        database.save(Game.Tekken8, jin, listOf(midLeftPunch, spinningSidekick))
        val expected = listOf(midLeftPunch.input, spinningSidekick.input)

        // when
        repeat(4) { database.save(Game.Tekken8, jin, listOf(midLeftPunch)) }

        // then
        val inputList = database.moveAdapter.subscribe(Game.Tekken8, jin.id).first().map { move -> move.input }
        assertThat(inputList).isEqualTo(expected)
    }

    @Test
    fun `a move missing from five saves is deleted`() = runTest {
        // given
        val database = TestWikiDatabase()
        database.save(Game.Tekken8, jin, listOf(midLeftPunch, spinningSidekick))
        val expected = listOf(midLeftPunch.input)

        // when
        repeat(5) { database.save(Game.Tekken8, jin, listOf(midLeftPunch)) }

        // then
        val inputList = database.moveAdapter.subscribe(Game.Tekken8, jin.id).first().map { move -> move.input }
        assertThat(inputList).isEqualTo(expected)
    }

    @Test
    fun `an input beats another move's alias`() = runTest {
        // given
        val database = TestWikiDatabase()
        val sidekickAliasingMidLeftPunch = spinningSidekick.copy(aliases = listOf("sidekick", midLeftPunch.input))
        val expected = listOf("sidekick")

        // when
        database.save(Game.Tekken8, jin, listOf(sidekickAliasingMidLeftPunch, midLeftPunch))

        // then
        val loadedSidekick = database.moveAdapter.subscribe(Game.Tekken8, jin.id).first()
            .single { move -> move.input == spinningSidekick.input }
        assertThat(loadedSidekick.aliases).isEqualTo(expected)
    }

    @Test
    fun `an alias shared by two moves stays with the first in wiki order`() = runTest {
        // given
        val database = TestWikiDatabase()
        val expected = listOf("ewhf")

        // when
        database.save(Game.Tekken8, jin, listOf(windHookFist, electricWindHookFist))

        // then
        val loadedElectric = database.moveAdapter.subscribe(Game.Tekken8, jin.id).first()
            .single { move -> move.input == electricWindHookFist.input }
        assertThat(loadedElectric.aliases).isEqualTo(expected)
    }

    @Test
    fun `delete removes only that game's moves`() = runTest {
        // given
        val database = TestWikiDatabase()
        database.save(Game.Tekken8, jin, listOf(demonsPaw))
        database.save(Game.GGST, solBadguy, listOf(solFarSlash))
        val expected = listOf(solFarSlash)

        // when
        database.moveAdapter.delete(Game.Tekken8)

        // then
        val jinMoveList = database.moveAdapter.subscribe(Game.Tekken8, jin.id).first()
        val solMoveList = database.moveAdapter.subscribe(Game.GGST, solBadguy.id).first()
        assertThat(jinMoveList).isEmpty()
        assertThat(solMoveList).isEqualTo(expected)
    }

    @Test
    fun `last update is empty before the first save`() = runTest {
        // given
        val database = TestWikiDatabase()

        // when
        val lastUpdate = database.moveAdapter.subscribe(Game.Tekken8).first()

        // then
        assertThat(lastUpdate).isNull()
    }

    @Test
    fun `last update is the game's latest save`() = runTest {
        // given
        val clock = FakeClock()
        val database = TestWikiDatabase(clock)
        val expected = Instant.fromEpochMilliseconds(1_759_107_600_000)

        // when
        database.save(Game.Tekken8, jin)
        clock.now = expected
        database.save(Game.Tekken8, asuka)
        clock.now = (expected + 1.hours)
        database.save(Game.GGST, solBadguy)

        // then
        val lastUpdate = database.moveAdapter.subscribe(Game.Tekken8).first()
        assertThat(lastUpdate).isEqualTo(expected)
    }
}
