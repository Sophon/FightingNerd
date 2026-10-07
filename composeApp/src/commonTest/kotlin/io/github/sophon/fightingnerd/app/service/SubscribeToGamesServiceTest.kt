package io.github.sophon.fightingnerd.app.service

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.Game
import io.github.sophon.fightingnerd.app.model.Wiki
import io.github.sophon.fightingnerd.app.outPort.AvailableGamesPort
import io.github.sophon.fightingnerd.app.outPort.SubscribeToGameSettingsPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class SubscribeToGamesServiceTest {
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
    private val mbtl = Game(
        id = "MBTL",
        displayName = "Melty Blood: Type Lumina",
        iconUrl = "https://i.imgur.com/E6O7DMi.png",
        wiki = Wiki(name = "Mizuumi Wiki", url = "https://mizuumi.wiki", iconUrl = "https://mizuumi.wiki/mizulogo.png?1fe5d"),
    )
    private val availableGameSet = setOf(tekken8, streetFighter6, ggst, mbtl)

    @Test
    fun `only enabled games are emitted in available order`() = runTest {
        // given
        val settingsPort = FakeSubscribeToGameSettingsPort(
            isEnabledById = mapOf("GGST" to true, "Tekken_8" to true, "Street_Fighter_6" to false),
        )
        val service = SubscribeToGamesService(FakeAvailableGamesPort(availableGameSet), settingsPort)
        val expected = Result.Success(listOf(tekken8, ggst))

        // when
        val result = service().first()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `settings changes are emitted`() = runTest {
        // given
        val settingsPort = FakeSubscribeToGameSettingsPort(isEnabledById = mapOf("Tekken_8" to true))
        val service = SubscribeToGamesService(FakeAvailableGamesPort(availableGameSet), settingsPort)
        val expected = Result.Success(listOf(tekken8, mbtl))

        service().test {
            awaitItem()

            // when
            settingsPort.setEnabled(gameId = "MBTL", isEnabled = true)

            // then
            assertThat(awaitItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `settings of unavailable games don't re-emit`() = runTest {
        // given
        val settingsPort = FakeSubscribeToGameSettingsPort(isEnabledById = mapOf("Tekken_8" to true))
        val service = SubscribeToGamesService(FakeAvailableGamesPort(availableGameSet), settingsPort)

        service().test {
            awaitItem()

            // when
            settingsPort.setEnabled(gameId = "Under_Night_In-Birth_II", isEnabled = true)

            // then
            expectNoEvents()
        }
    }

    @Test
    fun `settings error is emitted`() = runTest {
        // given
        val error = AppError.IOError("corrupted preferences")
        val settingsPort = FakeSubscribeToGameSettingsPort(isEnabledById = emptyMap(), error = error)
        val service = SubscribeToGamesService(FakeAvailableGamesPort(availableGameSet), settingsPort)
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
            val flow = flowOf(gameSet)
            return flow
        }
    }

    /**
     * Like DataStore - every stored flag change emits, even for games outside the requested set.
     */
    private class FakeSubscribeToGameSettingsPort(
        isEnabledById: Map<String, Boolean>,
        private val error: AppError? = null,
    ): SubscribeToGameSettingsPort {
        private val storedIsEnabledById = MutableStateFlow(isEnabledById)

        fun setEnabled(gameId: String, isEnabled: Boolean) {
            storedIsEnabledById.update { current -> current + (gameId to isEnabled) }
        }

        override fun subscribeToGameSettings(gameSet: Set<Game>): Flow<Result<Map<Game, Boolean>, AppError>> {
            val flow = storedIsEnabledById.map { isEnabledById ->
                val result: Result<Map<Game, Boolean>, AppError> = if (error == null) {
                    Result.Success(gameSet.associateWith { game -> isEnabledById[game.id] == true })
                } else {
                    Result.Error(error)
                }
                result
            }
            return flow
        }
    }
}
