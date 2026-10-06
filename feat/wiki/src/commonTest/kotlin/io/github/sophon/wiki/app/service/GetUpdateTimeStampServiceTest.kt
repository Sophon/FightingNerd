package io.github.sophon.wiki.app.service

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import io.github.sophon.wiki.app.outPort.LoadLastUpdatePort
import io.github.sophon.wiki.model.wiki.Game
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Instant

internal class GetUpdateTimeStampServiceTest {
    @Test
    fun `the last update of the game is returned`() = runTest {
        // given
        val service = GetUpdateTimeStampService(
            FakeLoadLastUpdatePort(
                Game.Tekken8 to tekken8Update,
                Game.StreetFighter6 to streetFighter6Update,
            )
        )
        val expected = tekken8Update

        // when
        val result = service(Game.Tekken8).first()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `a never updated game has no timestamp`() = runTest {
        // given
        val service = GetUpdateTimeStampService(FakeLoadLastUpdatePort(Game.StreetFighter6 to streetFighter6Update))

        // when
        val result = service(Game.Tekken8).first()

        // then
        assertThat(result).isNull()
    }

    @Test
    fun `a new update re-emits`() = runTest {
        // given
        val lastUpdatePort = FakeLoadLastUpdatePort(Game.Tekken8 to tekken8Update)
        val service = GetUpdateTimeStampService(lastUpdatePort)
        val expected = tekken8NextUpdate

        service(Game.Tekken8).test {
            awaitItem()

            // when
            lastUpdatePort.store(Game.Tekken8, tekken8NextUpdate)
            val result = awaitItem()

            // then
            assertThat(result).isEqualTo(expected)
        }
    }
}


private val tekken8Update = Instant.parse("2026-10-05T02:00:00Z")

private val tekken8NextUpdate = Instant.parse("2026-10-06T02:00:00Z")

private val streetFighter6Update = Instant.parse("2026-10-04T02:00:00Z")

private class FakeLoadLastUpdatePort(vararg storedUpdates: Pair<Game, Instant>) : LoadLastUpdatePort {
    private val lastUpdateByGame = storedUpdates
        .associate { (game, instant) -> game to MutableStateFlow<Instant?>(instant) }
        .toMutableMap()

    fun store(
        game: Game,
        instant: Instant,
    ) {
        lastUpdateByGame.getOrPut(game) { MutableStateFlow(null) }.value = instant
    }

    override fun subscribe(game: Game): Flow<Instant?> {
        val lastUpdateFlow = lastUpdateByGame.getOrPut(game) { MutableStateFlow(null) }
        return lastUpdateFlow
    }
}
