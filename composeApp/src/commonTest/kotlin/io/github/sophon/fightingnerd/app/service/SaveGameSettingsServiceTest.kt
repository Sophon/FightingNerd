package io.github.sophon.fightingnerd.app.service

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.ComposeConfig
import io.github.sophon.fightingnerd.app.model.Move
import io.github.sophon.fightingnerd.app.model.RefreshEvent
import io.github.sophon.fightingnerd.app.outPort.ConfigureWikiPort
import io.github.sophon.fightingnerd.app.outPort.EnabledGamesPort
import io.github.sophon.fightingnerd.app.outPort.LoadConfigPort
import io.github.sophon.fightingnerd.app.outPort.MediaPort
import io.github.sophon.fightingnerd.app.outPort.RefreshWikiPort
import io.github.sophon.fightingnerd.app.outPort.SaveGameSettingsPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class SaveGameSettingsServiceTest {
    private val composeConfig = ComposeConfig(
        featureList = listOf(
            ComposeConfig.Feature(name = "Wavu Wiki", isEnabled = true, supportedGames = listOf("Tekken_8")),
            ComposeConfig.Feature(name = "SuperCombo Wiki", isEnabled = true, supportedGames = listOf("Street_Fighter_6")),
            ComposeConfig.Feature(name = "DustLoop Wiki", isEnabled = true, supportedGames = listOf("GGST")),
        ),
    )

    @Test
    fun `the new settings are saved and configure the wiki`() = runTest {
        // given
        val store = FakeGameSettingsStore(enabledGameIdSet = setOf("Tekken_8"))
        val wiki = FakeWiki()
        val service = saveGameSettingsService(store = store, wiki = wiki)
        val expected = listOf(composeConfig to setOf("Tekken_8", "GGST"))

        // when
        service(setOf("Tekken_8", "GGST"))

        // then
        assertThat(store.savedList).isEqualTo(expected)
        assertThat(wiki.configuredList).isEqualTo(expected)
    }

    @Test
    fun `newly disabled games have their media wiped`() = runTest {
        // given
        val mediaPort = FakeMediaPort()
        val service = saveGameSettingsService(
            store = FakeGameSettingsStore(enabledGameIdSet = setOf("Tekken_8", "GGST")),
            mediaPort = mediaPort,
        )
        val expected = listOf("GGST")

        // when
        service(setOf("Tekken_8"))

        // then
        assertThat(mediaPort.wipedGameIdList).isEqualTo(expected)
    }

    @Test
    fun `newly enabled games start downloading`() = runTest {
        // given
        val wiki = FakeWiki()
        val service = saveGameSettingsService(
            store = FakeGameSettingsStore(enabledGameIdSet = setOf("Tekken_8")),
            wiki = wiki,
        )
        val expected = listOf(setOf("Street_Fighter_6"))

        // when
        service(setOf("Tekken_8", "Street_Fighter_6"))
        runCurrent()

        // then
        assertThat(wiki.refreshedList).isEqualTo(expected)
    }

    @Test
    fun `nothing downloads when no game was enabled`() = runTest {
        // given
        val wiki = FakeWiki()
        val service = saveGameSettingsService(
            store = FakeGameSettingsStore(enabledGameIdSet = setOf("Tekken_8", "GGST")),
            wiki = wiki,
        )

        // when
        service(setOf("Tekken_8"))
        runCurrent()

        // then
        assertThat(wiki.refreshedList).isEmpty()
    }

    @Test
    fun `a failed save configures, wipes and downloads nothing`() = runTest {
        // given
        val error = AppError.IOError("disk full")
        val wiki = FakeWiki()
        val mediaPort = FakeMediaPort()
        val service = saveGameSettingsService(
            store = FakeGameSettingsStore(enabledGameIdSet = setOf("Tekken_8"), saveError = error),
            wiki = wiki,
            mediaPort = mediaPort,
        )
        val expected = Result.Error(error)

        // when
        val result = service(setOf("Street_Fighter_6"))
        runCurrent()

        // then
        assertThat(result).isEqualTo(expected)
        assertThat(wiki.configuredList).isEmpty()
        assertThat(wiki.refreshedList).isEmpty()
        assertThat(mediaPort.wipedGameIdList).isEmpty()
    }

    @Test
    fun `a failed media wipe still saves`() = runTest {
        // given
        val service = saveGameSettingsService(
            store = FakeGameSettingsStore(enabledGameIdSet = setOf("Tekken_8", "GGST")),
            mediaPort = FakeMediaPort(wipeError = AppError.IOError("permission denied")),
        )

        // when
        val result = service(setOf("Tekken_8"))

        // then
        assertThat(result).isInstanceOf(Result.Success::class)
    }


    private fun TestScope.saveGameSettingsService(
        store: FakeGameSettingsStore,
        wiki: FakeWiki = FakeWiki(),
        mediaPort: FakeMediaPort = FakeMediaPort(),
    ): SaveGameSettingsService {
        val service = SaveGameSettingsService(
            loadConfigPort = FakeLoadConfigPort(composeConfig),
            enabledGamesPort = store,
            saveGameSettingsPort = store,
            configureWikiPort = wiki,
            mediaPort = mediaPort,
            refreshWikiPort = wiki,
            appScope = backgroundScope,
        )
        return service
    }

    private class FakeLoadConfigPort(
        private val composeConfig: ComposeConfig,
    ): LoadConfigPort {
        override suspend fun load(): Result<ComposeConfig, AppError> {
            return Result.Success(composeConfig)
        }
    }

    /**
     * Stands in for both preference ports - the enabled games only change through a successful save.
     */
    private class FakeGameSettingsStore(
        private var enabledGameIdSet: Set<String>,
        private val saveError: AppError? = null,
    ): EnabledGamesPort, SaveGameSettingsPort {
        val savedList = mutableListOf<Pair<ComposeConfig, Set<String>>>()

        override suspend fun load(): Result<Set<String>, AppError> {
            return Result.Success(enabledGameIdSet)
        }

        override suspend fun saveGameSettings(
            composeConfig: ComposeConfig,
            enabledGameIdSet: Set<String>,
        ): EmptyResult<AppError> {
            if (saveError != null) return Result.Error(saveError)

            savedList.add(composeConfig to enabledGameIdSet)
            this.enabledGameIdSet = enabledGameIdSet
            return Result.Success(Unit)
        }
    }

    /**
     * Records the refreshes only once they're collected - the service has to launch them.
     */
    private class FakeWiki: ConfigureWikiPort, RefreshWikiPort {
        val configuredList = mutableListOf<Pair<ComposeConfig, Set<String>>>()
        val refreshedList = mutableListOf<Set<String>>()

        override suspend fun configure(
            composeConfig: ComposeConfig,
            enabledGameIdSet: Set<String>,
        ): EmptyResult<AppError> {
            configuredList.add(composeConfig to enabledGameIdSet)
            return Result.Success(Unit)
        }

        override fun refresh(): Flow<RefreshEvent> {
            error("not used")
        }

        override fun refresh(gameIdSet: Set<String>): Flow<RefreshEvent> {
            val flow = flow {
                refreshedList.add(gameIdSet)
                emit(RefreshEvent.Finished(successCount = 43))
            }
            return flow
        }
    }

    private class FakeMediaPort(
        private val wipeError: AppError? = null,
    ): MediaPort {
        val wipedGameIdList = mutableListOf<String>()

        override suspend fun wipe(gameId: String): EmptyResult<AppError> {
            wipedGameIdList.add(gameId)
            val result = if (wipeError == null) Result.Success(Unit) else Result.Error(wipeError)
            return result
        }

        override fun subscribeToCharactersWithOfflineMedia(gameId: String): Flow<Set<String>> {
            error("not used")
        }

        override suspend fun save(gameId: String, characterId: String, urls: Move.Urls): EmptyResult<AppError> {
            error("not used")
        }

        override suspend fun wipe(gameId: String, characterId: String): EmptyResult<AppError> {
            error("not used")
        }

        override fun toOfflineUrls(gameId: String, characterId: String, urls: Move.Urls): Move.Urls {
            error("not used")
        }
    }
}
