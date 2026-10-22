package io.github.sophon.fightingnerd.app.service

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.Game
import io.github.sophon.fightingnerd.app.model.Wiki
import io.github.sophon.fightingnerd.app.outPort.AvailableGamesPort
import io.github.sophon.fightingnerd.app.outPort.LastUpdatePort
import io.github.sophon.fightingnerd.app.outPort.SubscribeToGameSettingsPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Instant

internal class SubscribeToLastUpdatesServiceTest {
    private val tekken8 = Game(
        id = "Tekken_8",
        displayName = "Tekken 8",
        iconUrl = "https://i.imgur.com/Yl6j809.png",
        wiki = Wiki(name = "Wavu Wiki", url = "https://wavu.wiki/", iconUrl = "https://wavu.wiki/android-chrome-512x512.png"),
    )
    private val streetFighter6 = Game(
        id = "Street_Fighter_6",
        displayName = "Street Fighter 6",
        iconUrl = "https://i.imgur.com/N9wYA5K.png",
        wiki = Wiki(name = "SuperCombo Wiki", url = "https://wiki.supercombo.gg/", iconUrl = "https://wiki.supercombo.gg/srk_wordmark.png"),
    )
    private val ggst = Game(
        id = "GGST",
        displayName = "Guilty Gear -Strive-",
        iconUrl = "https://i.imgur.com/07yTLtj.png",
        wiki = Wiki(name = "DustLoop Wiki", url = "https://www.dustloop.com/wiki/", iconUrl = "https://www.dustloop.com/wiki/images/archive/3/30/20260601135625%21Dustloop_Wiki.png"),
    )
    private val availableGameSet = setOf(tekken8, streetFighter6, ggst)

    private val tekken8LastUpdate = Instant.parse("2026-09-01T08:15:00Z")
    private val ggstLastUpdate = Instant.parse("2026-09-04T11:27:00Z")

    @Test
    fun `only enabled games are mapped to their last update`() = runTest {
        // given
        val service = SubscribeToLastUpdatesService(
            availableGamesPort = FakeAvailableGamesPort(availableGameSet),
            subscribeToGameSettingsPort = FakeSubscribeToGameSettingsPort(enabledGameIdSet = setOf("Tekken_8", "GGST")),
            lastUpdatePort = FakeLastUpdatePort(mapOf("Tekken_8" to tekken8LastUpdate, "GGST" to ggstLastUpdate)),
        )
        val expected = Result.Success(mapOf(tekken8 to tekken8LastUpdate, ggst to ggstLastUpdate))

        // when
        val result = service().first()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `a game that was never downloaded maps to null`() = runTest {
        // given
        val service = SubscribeToLastUpdatesService(
            availableGamesPort = FakeAvailableGamesPort(availableGameSet),
            subscribeToGameSettingsPort = FakeSubscribeToGameSettingsPort(enabledGameIdSet = setOf("Street_Fighter_6")),
            lastUpdatePort = FakeLastUpdatePort(emptyMap()),
        )
        val expected = Result.Success(mapOf(streetFighter6 to null))

        // when
        val result = service().first()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `a refresh emits the new last update`() = runTest {
        // given
        val lastUpdatePort = FakeLastUpdatePort(mapOf("Tekken_8" to tekken8LastUpdate))
        val service = SubscribeToLastUpdatesService(
            availableGamesPort = FakeAvailableGamesPort(availableGameSet),
            subscribeToGameSettingsPort = FakeSubscribeToGameSettingsPort(enabledGameIdSet = setOf("Tekken_8")),
            lastUpdatePort = lastUpdatePort,
        )
        val refreshedLastUpdate = Instant.parse("2026-10-07T09:30:00Z")
        val expected = Result.Success(mapOf(tekken8 to refreshedLastUpdate))

        service().test {
            awaitItem()

            // when
            lastUpdatePort.setLastUpdate(gameId = "Tekken_8", lastUpdate = refreshedLastUpdate)

            // then
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `no enabled games emit an empty map`() = runTest {
        // given
        val service = SubscribeToLastUpdatesService(
            availableGamesPort = FakeAvailableGamesPort(availableGameSet),
            subscribeToGameSettingsPort = FakeSubscribeToGameSettingsPort(enabledGameIdSet = emptySet()),
            lastUpdatePort = FakeLastUpdatePort(mapOf("Tekken_8" to tekken8LastUpdate)),
        )
        val expected = Result.Success(emptyMap<Game, Instant?>())

        // when
        val result = service().first()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `settings error is emitted`() = runTest {
        // given
        val error = AppError.IOError("corrupted preferences")
        val service = SubscribeToLastUpdatesService(
            availableGamesPort = FakeAvailableGamesPort(availableGameSet),
            subscribeToGameSettingsPort = FakeSubscribeToGameSettingsPort(enabledGameIdSet = emptySet(), error = error),
            lastUpdatePort = FakeLastUpdatePort(emptyMap()),
        )
        val expected = Result.Error(error)

        // when
        val result = service().first()

        // then
        assertThat(result).isEqualTo(expected)
    }


    private class FakeAvailableGamesPort(
        private val gameSet: Set<Game>,
    ): AvailableGamesPort {
        override fun subscribe(): Flow<Set<Game>> {
            return flowOf(gameSet)
        }
    }

    private class FakeSubscribeToGameSettingsPort(
        private val enabledGameIdSet: Set<String>,
        private val error: AppError? = null,
    ): SubscribeToGameSettingsPort {
        override fun subscribeToGameSettings(gameSet: Set<Game>): Flow<Result<Map<Game, Boolean>, AppError>> {
            val result: Result<Map<Game, Boolean>, AppError> = if (error == null) {
                Result.Success(gameSet.associateWith { game -> game.id in enabledGameIdSet })
            } else {
                Result.Error(error)
            }
            return flowOf(result)
        }
    }

    private class FakeLastUpdatePort(
        lastUpdateById: Map<String, Instant>,
    ): LastUpdatePort {
        private val lastUpdateFlowById = mutableMapOf<String, MutableStateFlow<Instant?>>().apply {
            lastUpdateById.forEach { (gameId, lastUpdate) -> put(gameId, MutableStateFlow(lastUpdate)) }
        }

        fun setLastUpdate(gameId: String, lastUpdate: Instant) {
            lastUpdateFlowById.getOrPut(gameId) { MutableStateFlow(null) }.value = lastUpdate
        }

        override fun subscribeToLastUpdate(gameId: String): Flow<Instant?> {
            return lastUpdateFlowById.getOrPut(gameId) { MutableStateFlow(null) }
        }
    }
}
