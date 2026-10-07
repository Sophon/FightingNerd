package io.github.sophon.fightingnerd.app.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.Game
import io.github.sophon.fightingnerd.app.model.RefreshEvent
import io.github.sophon.fightingnerd.app.outPort.ConfigureWikiPort
import io.github.sophon.fightingnerd.app.outPort.FirstLaunchPort
import io.github.sophon.fightingnerd.app.outPort.SubscribeToAvailableGamesPort
import io.github.sophon.fightingnerd.app.outPort.RefreshWikiPort
import io.github.sophon.fightingnerd.app.outPort.SaveGameSettingsPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class FirstTimeConfigServiceTest {
    private val tekken8 = Game(
        id = "Tekken_8",
        displayName = "Tekken 8",
        iconUrl = "https://i.imgur.com/Yl6j809.png",
        wikiName = "Wavu Wiki",
    )
    private val streetFighter6 = Game(
        id = "Street_Fighter_6",
        displayName = "Street Fighter 6",
        iconUrl = "https://i.imgur.com/N9wYA5K.png",
        wikiName = "SuperCombo Wiki",
    )
    private val ggst = Game(
        id = "GGST",
        displayName = "Guilty Gear -Strive-",
        iconUrl = "https://i.imgur.com/07yTLtj.png",
        wikiName = "DustLoop Wiki",
    )
    private val mbtl = Game(
        id = "MBTL",
        displayName = "Melty Blood: Type Lumina",
        iconUrl = "https://i.imgur.com/E6O7DMi.png",
        wikiName = "Mizuumi Wiki",
    )
    private val availableGameSet = setOf(tekken8, streetFighter6, ggst, mbtl)

    private val firstLaunchPort = FakeFirstLaunchPort()
    private val saveGameSettingsPort = FakeSaveGameSettingsPort()
    private val configureWikiPort = FakeConfigureWikiPort()
    private val refreshWikiPort = FakeRefreshWikiPort()
    private val service = FirstTimeConfigService(
        firstLaunchPort = firstLaunchPort,
        subscribeToAvailableGamesPort = FakeSubscribeToAvailableGamesPort(availableGameSet),
        saveGameSettingsPort = saveGameSettingsPort,
        configureWikiPort = configureWikiPort,
        refreshWikiPort = refreshWikiPort,
    )

    @Test
    fun `first launch enables only the default games`() = runTest {
        // given
        val expected = mapOf(
            tekken8 to true,
            streetFighter6 to true,
            ggst to true,
            mbtl to false,
        )

        // when
        service()

        // then
        assertThat(saveGameSettingsPort.savedIsEnabledByGame).isEqualTo(expected)
    }

    @Test
    fun `wiki is configured with the default games enabled`() = runTest {
        // given
        val expected = availableGameSet to setOf(tekken8, streetFighter6, ggst)

        // when
        service()

        // then
        assertThat(configureWikiPort.configuredGameSets).isEqualTo(expected)
    }

    @Test
    fun `first launch is remembered and refreshes the wiki`() = runTest {
        // given
        val expected = Triple(Result.Success(Unit), true, 1)

        // when
        val result = service()

        // then
        assertThat(Triple(result, firstLaunchPort.hasLaunched, refreshWikiPort.collectCount)).isEqualTo(expected)
    }

    @Test
    fun `later launches change nothing`() = runTest {
        // given
        firstLaunchPort.hasLaunched = true
        val expected = Triple(null, null, 0)

        // when
        service()

        // then
        val sideEffects = Triple(
            saveGameSettingsPort.savedIsEnabledByGame,
            configureWikiPort.configuredGameSets,
            refreshWikiPort.collectCount,
        )
        assertThat(sideEffects).isEqualTo(expected)
    }

    @Test
    fun `failed save leaves the launch unmarked and the wiki untouched`() = runTest {
        // given
        val error = AppError.IOError("disk full")
        saveGameSettingsPort.error = error
        val expected = Triple(Result.Error(error), false, null)

        // when
        val result = service()

        // then
        assertThat(Triple(result, firstLaunchPort.hasLaunched, configureWikiPort.configuredGameSets)).isEqualTo(expected)
    }

    @Test
    fun `failed wiki configuration leaves the launch unmarked and skips the refresh`() = runTest {
        // given
        val error = AppError.WikiError("InvalidConfig(GGST)")
        configureWikiPort.error = error
        val expected = Triple(Result.Error(error), false, 0)

        // when
        val result = service()

        // then
        assertThat(Triple(result, firstLaunchPort.hasLaunched, refreshWikiPort.collectCount)).isEqualTo(expected)
    }


    private class FakeFirstLaunchPort: FirstLaunchPort {
        var hasLaunched = false

        override suspend fun hasLaunchedBefore(): Result<Boolean, AppError> {
            val result = Result.Success(hasLaunched)
            return result
        }

        override suspend fun markLaunched(): EmptyResult<AppError> {
            hasLaunched = true
            return Result.Success(Unit)
        }
    }

    private class FakeSubscribeToAvailableGamesPort(
        private val gameSet: Set<Game>,
    ): SubscribeToAvailableGamesPort {
        override fun subscribeToAvailableGames(): Flow<Set<Game>> {
            val flow = flowOf(gameSet)
            return flow
        }
    }

    private class FakeSaveGameSettingsPort: SaveGameSettingsPort {
        var error: AppError? = null
        var savedIsEnabledByGame: Map<Game, Boolean>? = null
            private set

        override suspend fun saveGameSettings(isEnabledByGame: Map<Game, Boolean>): EmptyResult<AppError> {
            val currentError = error
            val result = if (currentError == null) {
                savedIsEnabledByGame = isEnabledByGame
                Result.Success(Unit)
            } else {
                Result.Error(currentError)
            }
            return result
        }
    }

    private class FakeConfigureWikiPort: ConfigureWikiPort {
        var error: AppError? = null
        var configuredGameSets: Pair<Set<Game>, Set<Game>>? = null
            private set

        override suspend fun configure(
            availableGameSet: Set<Game>,
            enabledGameSet: Set<Game>,
        ): EmptyResult<AppError> {
            configuredGameSets = availableGameSet to enabledGameSet
            val currentError = error
            val result = if (currentError == null) {
                Result.Success(Unit)
            } else {
                Result.Error(currentError)
            }
            return result
        }
    }

    private class FakeRefreshWikiPort: RefreshWikiPort {
        var collectCount = 0
            private set

        override fun refresh(): Flow<RefreshEvent> {
            val flow = flow {
                collectCount++
                emit(RefreshEvent.Finished(successCount = 37))
            }
            return flow
        }
    }
}
