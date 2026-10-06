package io.github.sophon.botdiscord.app.service

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.discord.DiscordConfig
import io.github.sophon.discord.app.outPort.ConfigureAdminPort
import io.github.sophon.discord.app.outPort.ConfigureWikiPort
import io.github.sophon.discord.app.service.StartFeaturesService
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class StartFeaturesServiceTest {
    @Test
    fun `stats are configured`() = runTest {
        // given
        val statsPort = FakeStatsPort()
        val service = startFeaturesService(statsPort = statsPort)

        // when
        service()

        // then
        assertThat(statsPort.configureCount).isEqualTo(1)
    }

    @Test
    fun `failed stats configuration doesn't stop the start`() = runTest {
        // given
        val expected = Result.Success(Unit)
        val service = startFeaturesService(
            statsPort = FakeStatsPort(configureResult = Result.Error(BotError.FileError("stats.json"))),
        )

        // when
        val result = service()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `admin and wiki are configured with the loaded config`() = runTest {
        // given
        val configureAdminPort = FakeConfigureAdminPort()
        val configureWikiPort = FakeConfigureWikiPort()
        val service = startFeaturesService(
            configureAdminPort = configureAdminPort,
            configureWikiPort = configureWikiPort,
        )

        // when
        service()

        // then
        assertThat(configureAdminPort.configList + configureWikiPort.configList)
            .containsExactly(discordConfig, discordConfig)
    }

    @Test
    fun `failed config load is returned`() = runTest {
        // given
        val expected = Result.Error(BotError.FileError("discordConfig.json"))
        val service = startFeaturesService(loadConfigPort = FakeLoadConfigPort(expected))

        // when
        val result = service()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `failed config load configures nothing`() = runTest {
        // given
        val configureAdminPort = FakeConfigureAdminPort()
        val configureWikiPort = FakeConfigureWikiPort()
        val service = startFeaturesService(
            loadConfigPort = FakeLoadConfigPort(Result.Error(BotError.FileError("discordConfig.json"))),
            configureAdminPort = configureAdminPort,
            configureWikiPort = configureWikiPort,
        )

        // when
        service()

        // then
        assertThat(configureAdminPort.configList + configureWikiPort.configList).isEmpty()
    }

    @Test
    fun `failed admin configuration is returned`() = runTest {
        // given
        val expected = Result.Error(BotError.AdminError("Database(error=UNKNOWN)"))
        val service = startFeaturesService(configureAdminPort = FakeConfigureAdminPort(result = expected))

        // when
        val result = service()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `failed admin configuration skips the wiki`() = runTest {
        // given
        val configureWikiPort = FakeConfigureWikiPort()
        val service = startFeaturesService(
            configureAdminPort = FakeConfigureAdminPort(result = Result.Error(BotError.AdminError("Database(error=UNKNOWN)"))),
            configureWikiPort = configureWikiPort,
        )

        // when
        service()

        // then
        assertThat(configureWikiPort.configList).isEmpty()
    }

    @Test
    fun `failed wiki configuration is returned`() = runTest {
        // given
        val expected = Result.Error(BotError.WikiError("InvalidConfig(Tekken_9)"))
        val service = startFeaturesService(configureWikiPort = FakeConfigureWikiPort(result = expected))

        // when
        val result = service()

        // then
        assertThat(result).isEqualTo(expected)
    }


    private class FakeConfigureAdminPort(
        private val result: EmptyResult<BotError> = Result.Success(Unit),
    ): ConfigureAdminPort {
        val configList = mutableListOf<DiscordConfig>()

        override fun configure(discordConfig: DiscordConfig): EmptyResult<BotError> {
            configList += discordConfig
            return result
        }
    }

    private class FakeConfigureWikiPort(
        private val result: EmptyResult<BotError> = Result.Success(Unit),
    ): ConfigureWikiPort {
        val configList = mutableListOf<DiscordConfig>()

        override suspend fun configure(discordConfig: DiscordConfig): EmptyResult<BotError> {
            configList += discordConfig
            return result
        }
    }

    private fun startFeaturesService(
        loadConfigPort: FakeLoadConfigPort = FakeLoadConfigPort(Result.Success(discordConfig)),
        configureAdminPort: FakeConfigureAdminPort = FakeConfigureAdminPort(),
        configureWikiPort: FakeConfigureWikiPort = FakeConfigureWikiPort(),
        statsPort: FakeStatsPort = FakeStatsPort(),
    ): StartFeaturesService {
        val service = StartFeaturesService(
            loadConfigPort = loadConfigPort,
            configureAdminPort = configureAdminPort,
            configureWikiPort = configureWikiPort,
            statsPort = statsPort,
        )
        return service
    }
}


private val discordConfig = discordConfigOf(
    featureList = listOf(
        DiscordConfig.Feature(name = "Wavu Wiki", isEnabled = true, supportedGames = listOf("Tekken_8")),
    ),
)
