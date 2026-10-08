package io.github.sophon.fightingnerd.app.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.ComposeConfig
import io.github.sophon.fightingnerd.app.model.Game
import io.github.sophon.fightingnerd.app.model.RefreshEvent
import io.github.sophon.fightingnerd.app.model.Wiki
import io.github.sophon.fightingnerd.app.outPort.ConfigureWikiPort
import io.github.sophon.fightingnerd.app.outPort.EnabledGamesPort
import io.github.sophon.fightingnerd.app.outPort.FirstLaunchPort
import io.github.sophon.fightingnerd.app.outPort.LoadConfigPort
import io.github.sophon.fightingnerd.app.outPort.RefreshWikiPort
import io.github.sophon.fightingnerd.app.outPort.SaveGameSettingsPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class OnLaunchSetupServiceTest {
    private val composeConfig = ComposeConfig(
        featureList = listOf(
            ComposeConfig.Feature(name = "Wavu Wiki", isEnabled = true, supportedGames = listOf("Tekken_8")),
            ComposeConfig.Feature(name = "SuperCombo Wiki", isEnabled = true, supportedGames = listOf("Street_Fighter_6")),
            ComposeConfig.Feature(name = "DustLoop Wiki", isEnabled = true, supportedGames = listOf("GGST")),
            ComposeConfig.Feature(name = "Mizuumi Wiki", isEnabled = true, supportedGames = listOf("MBTL")),
        ),
    )
    private val tekken8 = Game(
        id = "Tekken_8",
        displayName = "Tekken 8",
        iconUrl = "https://i.imgur.com/Yl6j809.png",
        wiki = Wiki(name = "Wavu Wiki", url = "https://wavu.wiki/", iconUrl = "https://wavu.wiki/android-chrome-512x512.png"),
    )
    private val refreshEventList = listOf(
        RefreshEvent.Started(game = tekken8),
        RefreshEvent.Finished(game = tekken8, successCount = 37),
    )

    private val loadConfigPort = FakeLoadConfigPort(composeConfig)
    private val firstLaunchPort = FakeFirstLaunchPort()
    private val gameSettingsPort = FakeGameSettingsPort()
    private val configureWikiPort = FakeConfigureWikiPort()
    private val refreshWikiPort = FakeRefreshWikiPort(refreshEventList)
    private val service = OnLaunchSetupService(
        loadConfigPort = loadConfigPort,
        firstLaunchPort = firstLaunchPort,
        saveGameSettingsPort = gameSettingsPort,
        enabledGamesPort = gameSettingsPort,
        configureWikiPort = configureWikiPort,
        refreshWikiPort = refreshWikiPort,
    )

    @Test
    fun `first launch enables only the default games`() = runTest {
        // given
        val expected = mapOf(
            "Tekken_8" to true,
            "Street_Fighter_6" to true,
            "GGST" to true,
            "MBTL" to false,
        )

        // when
        service()

        // then
        assertThat(gameSettingsPort.isEnabledByGameId).isEqualTo(expected)
    }

    @Test
    fun `first launch configures the wiki with the default games`() = runTest {
        // given
        val expected = listOf(composeConfig to setOf("Tekken_8", "Street_Fighter_6", "GGST"))

        // when
        service()

        // then
        assertThat(configureWikiPort.configuredList).isEqualTo(expected)
    }

    @Test
    fun `first launch is remembered and returns the wiki refresh`() = runTest {
        // given
        val expected = Pair(Result.Success(refreshEventList), true)

        // when
        val result = service().map { refreshEventFlow -> refreshEventFlow.toList() }

        // then
        assertThat(result to firstLaunchPort.hasLaunched).isEqualTo(expected)
    }

    @Test
    fun `later launches configure the wiki with the saved games`() = runTest {
        // given
        firstLaunchPort.hasLaunched = true
        gameSettingsPort.isEnabledByGameId = mapOf("Tekken_8" to true, "MBTL" to true, "GGST" to false)
        val expected = listOf(composeConfig to setOf("Tekken_8", "MBTL"))

        // when
        service()

        // then
        assertThat(configureWikiPort.configuredList).isEqualTo(expected)
    }

    @Test
    fun `later launches keep the saved games and skip the refresh`() = runTest {
        // given
        firstLaunchPort.hasLaunched = true
        val isEnabledByGameId = mapOf("Tekken_8" to true, "MBTL" to true, "GGST" to false)
        gameSettingsPort.isEnabledByGameId = isEnabledByGameId
        val expected = Triple(Result.Success(emptyList<RefreshEvent>()), isEnabledByGameId, 0)

        // when
        val result = service().map { refreshEventFlow -> refreshEventFlow.toList() }

        // then
        val sideEffects = Triple(result, gameSettingsPort.isEnabledByGameId, refreshWikiPort.refreshCount)
        assertThat(sideEffects).isEqualTo(expected)
    }

    @Test
    fun `missing config leaves the wiki unconfigured and the launch unmarked`() = runTest {
        // given
        val error = AppError.ConfigNotFoundError("files/composeConfig.json")
        loadConfigPort.error = error
        val expected = Triple(Result.Error(error), false, emptyList<Pair<ComposeConfig, Set<String>>>())

        // when
        val result = service()

        // then
        assertThat(Triple(result, firstLaunchPort.hasLaunched, configureWikiPort.configuredList)).isEqualTo(expected)
    }

    @Test
    fun `failed first launch check leaves the wiki unconfigured`() = runTest {
        // given
        val error = AppError.IOError("corrupted preferences")
        firstLaunchPort.error = error
        val expected = Pair(Result.Error(error), emptyList<Pair<ComposeConfig, Set<String>>>())

        // when
        val result = service()

        // then
        assertThat(result to configureWikiPort.configuredList).isEqualTo(expected)
    }

    @Test
    fun `failed save leaves the launch unmarked`() = runTest {
        // given
        gameSettingsPort.error = AppError.IOError("disk full")

        // when
        service()

        // then
        assertThat(firstLaunchPort.hasLaunched).isFalse()
    }

    @Test
    fun `failed wiki configuration skips the refresh`() = runTest {
        // given
        val error = AppError.WikiError("InvalidConfig(GGST)")
        configureWikiPort.error = error
        val expected = Pair(Result.Error(error), 0)

        // when
        val result = service()

        // then
        assertThat(result to refreshWikiPort.refreshCount).isEqualTo(expected)
    }


    private class FakeLoadConfigPort(
        private val composeConfig: ComposeConfig,
    ): LoadConfigPort {
        var error: AppError? = null

        override suspend fun load(): Result<ComposeConfig, AppError> {
            val currentError = error
            val result = if (currentError == null) {
                Result.Success(composeConfig)
            } else {
                Result.Error(currentError)
            }
            return result
        }
    }

    private class FakeFirstLaunchPort: FirstLaunchPort {
        var hasLaunched = false
        var error: AppError? = null

        override suspend fun hasLaunchedBefore(): Result<Boolean, AppError> {
            val currentError = error
            val result = if (currentError == null) {
                Result.Success(hasLaunched)
            } else {
                Result.Error(currentError)
            }
            return result
        }

        override suspend fun markLaunched(): EmptyResult<AppError> {
            hasLaunched = true
            return Result.Success(Unit)
        }
    }

    private class FakeGameSettingsPort: SaveGameSettingsPort, EnabledGamesPort {
        var error: AppError? = null
        var isEnabledByGameId: Map<String, Boolean> = emptyMap()

        override suspend fun saveGameSettings(
            composeConfig: ComposeConfig,
            enabledGameIdSet: Set<String>,
        ): EmptyResult<AppError> {
            val currentError = error
            val result = if (currentError == null) {
                isEnabledByGameId = composeConfig.availableFeatureList
                    .flatMap { feature -> feature.supportedGames }
                    .associateWith { gameId -> gameId in enabledGameIdSet }
                Result.Success(Unit)
            } else {
                Result.Error(currentError)
            }
            return result
        }

        override suspend fun load(): Result<Set<String>, AppError> {
            val enabledGameIdSet = isEnabledByGameId
                .filterValues { isEnabled -> isEnabled }
                .keys
            return Result.Success(enabledGameIdSet)
        }
    }

    private class FakeConfigureWikiPort: ConfigureWikiPort {
        var error: AppError? = null
        val configuredList = mutableListOf<Pair<ComposeConfig, Set<String>>>()

        override suspend fun configure(
            composeConfig: ComposeConfig,
            enabledGameIdSet: Set<String>,
        ): EmptyResult<AppError> {
            val currentError = error
            val result = if (currentError == null) {
                configuredList.add(composeConfig to enabledGameIdSet)
                Result.Success(Unit)
            } else {
                Result.Error(currentError)
            }
            return result
        }
    }

    private class FakeRefreshWikiPort(
        private val eventList: List<RefreshEvent>,
    ): RefreshWikiPort {
        var refreshCount = 0
            private set

        override fun refresh(): Flow<RefreshEvent> {
            refreshCount++
            return eventList.asFlow()
        }

        override fun refresh(gameIdSet: Set<String>): Flow<RefreshEvent> {
            error("not used")
        }
    }
}
