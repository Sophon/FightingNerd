package io.github.sophon.fightingnerd.app.service

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.ComposeConfig
import io.github.sophon.fightingnerd.app.model.RefreshEvent
import io.github.sophon.fightingnerd.app.outPort.ConfigureWikiPort
import io.github.sophon.fightingnerd.app.outPort.EnabledGamesPort
import io.github.sophon.fightingnerd.app.outPort.FirstLaunchPort
import io.github.sophon.fightingnerd.app.outPort.LoadConfigPort
import io.github.sophon.fightingnerd.app.outPort.RefreshWikiPort
import io.github.sophon.fightingnerd.app.outPort.SaveGameSettingsPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class SyncWikiConfigServiceTest {
    private val composeConfig = ComposeConfig(
        featureList = listOf(
            ComposeConfig.Feature(name = "Wavu Wiki", isEnabled = true, supportedGames = listOf("Tekken_8")),
            ComposeConfig.Feature(name = "SuperCombo Wiki", isEnabled = true, supportedGames = listOf("Street_Fighter_6")),
            ComposeConfig.Feature(name = "DustLoop Wiki", isEnabled = true, supportedGames = listOf("GGST")),
            ComposeConfig.Feature(name = "Mizuumi Wiki", isEnabled = true, supportedGames = listOf("MBTL")),
        ),
    )

    private val loadConfigPort = FakeLoadConfigPort(composeConfig)
    private val firstLaunchPort = FakeFirstLaunchPort()
    private val gameSettingsPort = FakeGameSettingsPort()
    private val configureWikiPort = FakeConfigureWikiPort()
    private val refreshWikiPort = FakeRefreshWikiPort()
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
        service().first()

        // then
        assertThat(gameSettingsPort.isEnabledByGameId.value).isEqualTo(expected)
    }

    @Test
    fun `first launch configures the wiki once with the default games`() = runTest {
        // given
        val expected = listOf(composeConfig to setOf("Tekken_8", "Street_Fighter_6", "GGST"))

        // when
        service().first()

        // then
        assertThat(configureWikiPort.configuredList).isEqualTo(expected)
    }

    @Test
    fun `first launch is remembered and refreshes the wiki`() = runTest {
        // given
        val expected = Triple(Result.Success(Unit), true, 1)

        // when
        val result = service().first()

        // then
        assertThat(Triple(result, firstLaunchPort.hasLaunched, refreshWikiPort.collectCount)).isEqualTo(expected)
    }

    @Test
    fun `later launches configure the wiki with the saved games`() = runTest {
        // given
        firstLaunchPort.hasLaunched = true
        gameSettingsPort.isEnabledByGameId.value = mapOf("Tekken_8" to true, "MBTL" to true, "GGST" to false)
        val expected = listOf(composeConfig to setOf("Tekken_8", "MBTL"))

        // when
        service().first()

        // then
        assertThat(configureWikiPort.configuredList).isEqualTo(expected)
    }

    @Test
    fun `later launches keep the saved games and skip the refresh`() = runTest {
        // given
        firstLaunchPort.hasLaunched = true
        val isEnabledByGameId = mapOf("Tekken_8" to true, "MBTL" to true, "GGST" to false)
        gameSettingsPort.isEnabledByGameId.value = isEnabledByGameId
        val expected = Triple(Result.Success(Unit), isEnabledByGameId, 0)

        // when
        val result = service().first()

        // then
        val sideEffects = Triple(result, gameSettingsPort.isEnabledByGameId.value, refreshWikiPort.collectCount)
        assertThat(sideEffects).isEqualTo(expected)
    }

    @Test
    fun `toggled game reconfigures the wiki without another refresh`() = runTest {
        // given
        val expected = Pair(
            listOf(
                composeConfig to setOf("Tekken_8", "Street_Fighter_6", "GGST"),
                composeConfig to setOf("Tekken_8", "Street_Fighter_6"),
            ),
            1,
        )

        service().test {
            awaitItem()

            // when
            gameSettingsPort.isEnabledByGameId.update { isEnabledByGameId -> isEnabledByGameId + ("GGST" to false) }
            awaitItem()

            // then
            assertThat(configureWikiPort.configuredList to refreshWikiPort.collectCount).isEqualTo(expected)
        }
    }

    @Test
    fun `disabling every game still configures the wiki`() = runTest {
        // given
        firstLaunchPort.hasLaunched = true
        gameSettingsPort.isEnabledByGameId.value = mapOf("Tekken_8" to true)
        val expected = listOf(
            composeConfig to setOf("Tekken_8"),
            composeConfig to emptySet(),
        )

        service().test {
            awaitItem()

            // when
            gameSettingsPort.isEnabledByGameId.value = mapOf("Tekken_8" to false)
            awaitItem()

            // then
            assertThat(configureWikiPort.configuredList).isEqualTo(expected)
        }
    }

    @Test
    fun `missing config leaves the wiki unconfigured and the launch unmarked`() = runTest {
        // given
        val error = AppError.ConfigNotFoundError("files/composeConfig.json")
        loadConfigPort.error = error
        val expected = Triple(Result.Error(error), false, emptyList<Pair<ComposeConfig, Set<String>>>())

        service().test {
            // when
            val result = awaitItem()
            awaitComplete()

            // then
            assertThat(Triple(result, firstLaunchPort.hasLaunched, configureWikiPort.configuredList)).isEqualTo(expected)
        }
    }

    @Test
    fun `failed save leaves the launch unmarked and the wiki unconfigured`() = runTest {
        // given
        val error = AppError.IOError("disk full")
        gameSettingsPort.error = error
        val expected = Triple(Result.Error(error), false, emptyList<Pair<ComposeConfig, Set<String>>>())

        service().test {
            // when
            val result = awaitItem()
            awaitComplete()

            // then
            assertThat(Triple(result, firstLaunchPort.hasLaunched, configureWikiPort.configuredList)).isEqualTo(expected)
        }
    }

    @Test
    fun `failed wiki configuration skips the refresh`() = runTest {
        // given
        val error = AppError.WikiError("InvalidConfig(GGST)")
        configureWikiPort.error = error
        val expected = Pair(Result.Error(error), 0)

        // when
        val result = service().first()

        // then
        assertThat(result to refreshWikiPort.collectCount).isEqualTo(expected)
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

        override suspend fun hasLaunchedBefore(): Result<Boolean, AppError> {
            val result = Result.Success(hasLaunched)
            return result
        }

        override suspend fun markLaunched(): EmptyResult<AppError> {
            hasLaunched = true
            return Result.Success(Unit)
        }
    }

    private class FakeGameSettingsPort: SaveGameSettingsPort, EnabledGamesPort {
        var error: AppError? = null
        val isEnabledByGameId = MutableStateFlow<Map<String, Boolean>>(emptyMap())

        override suspend fun saveGameSettings(
            composeConfig: ComposeConfig,
            enabledGameIdSet: Set<String>,
        ): EmptyResult<AppError> {
            val currentError = error
            val result = if (currentError == null) {
                isEnabledByGameId.value = composeConfig.availableFeatureList
                    .flatMap { feature -> feature.supportedGames }
                    .associateWith { gameId -> gameId in enabledGameIdSet }
                Result.Success(Unit)
            } else {
                Result.Error(currentError)
            }
            return result
        }

        override fun subscribe(composeConfig: ComposeConfig): Flow<Result<Set<String>, AppError>> {
            val flow = isEnabledByGameId.map { isEnabledByGameId ->
                val enabledGameIdSet = isEnabledByGameId
                    .filterValues { isEnabled -> isEnabled }
                    .keys
                val result: Result<Set<String>, AppError> = Result.Success(enabledGameIdSet)
                result
            }
            return flow
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
