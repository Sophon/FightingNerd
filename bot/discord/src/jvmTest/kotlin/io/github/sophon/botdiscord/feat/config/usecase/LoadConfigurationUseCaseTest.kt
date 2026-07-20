package io.github.sophon.botdiscord.feat.config.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Config
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.discord.feat.config.usecase.LoadConfigurationUseCase
import io.github.sophon.discord.feat.core.data.FileManager
import io.github.sophon.discord.feat.core.domain.model.BotError
import kotlinx.serialization.json.Json
import org.junit.Test

internal class LoadConfigurationUseCaseTest {
    private val json = Json { ignoreUnknownKeys = true }
    private val fileManager = FileManager()
    private val usecase = LoadConfigurationUseCase(json, fileManager)

    @Test
    fun `usecase loads config from json`() {
        // given
        val expected = Config(
            featureList = listOf(
                Config.Feature(name = "EWGF", isEnabled = true, supportedGameList = emptyList()),
                Config.Feature(name = "Infil Glossary", isEnabled = true, supportedGameList = emptyList()),
                Config.Feature(name = "Wavu Wiki", isEnabled = true, supportedGameList = listOf(Game.Tekken8)),
                Config.Feature(
                    name = "SuperCombo Wiki",
                    isEnabled = true,
                    supportedGameList = listOf(Game.StreetFighter6, Game.MK1, Game.AVL),
                ),
            ),
            adminConfig = Config.AdminConfig(
                administratorIdList = listOf("administratorIdList"),
                feedbackChannelIdList = listOf("feedbackChannelIdList"),
                adminServerId = "adminServerId",
            ),
            statsConfig = Config.StatsConfig(
                isEnabled = true,
                statsChannelIdList = listOf("statsChannelIdList"),
            ),
        )

        // when
        val result = usecase.invoke(TEST_CONFIG_PATH)

        // then
        assertThat(result).isInstanceOf(Result.Success::class)
        assertThat((result as Result.Success).data).isEqualTo(expected)
    }

    @Test
    fun `usecase returns error when file not found`() {
        // given
        val missingConfigPath = "src/jvmTest/kotlin/io/github/sophon/botdiscord/feat/config/usecase/missingConfig.json"

        // when
        val result = usecase.invoke(missingConfigPath)

        // then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat((result as Result.Error).error).isInstanceOf(BotError.FileError::class)
    }


    private companion object {
        const val TEST_CONFIG_PATH = "src/jvmTest/kotlin/io/github/sophon/botdiscord/feat/config/usecase/testConfig.json"
    }
}
