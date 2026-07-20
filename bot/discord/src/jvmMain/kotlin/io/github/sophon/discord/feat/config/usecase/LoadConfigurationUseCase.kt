package io.github.sophon.discord.feat.config.usecase

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.featureConfig.model.Config
import io.github.sophon.core.util.getGame
import io.github.sophon.discord.feat.config.data.JsonConfig
import io.github.sophon.discord.feat.core.data.FileManager
import io.github.sophon.discord.feat.core.domain.model.BotError
import kotlinx.serialization.json.Json

internal class LoadConfigurationUseCase(
    private val json: Json,
    private val fileManager: FileManager,
) {
    fun invoke(configPath: String = CONFIG_PATH): Result<Config, BotError> {
        val result = fileManager.read(configPath)
            .map { configText ->
                val jsonConfig = json.decodeFromString<JsonConfig>(configText).apply {
                    Napier.d(tag = TAG) { this.toString() }
                }
                Config(
                    featureList = jsonConfig.featureList.map { feature ->
                        Config.Feature(
                            name = feature.name,
                            isEnabled = feature.isEnabled,
                            supportedGameList = feature.supportedGames.mapNotNull { gameId ->
                                gameId.getGame()
                            },
                        )
                    },
                    adminConfig = Config.AdminConfig(
                        administratorIdList = jsonConfig.adminConfig.administratorIdList,
                        feedbackChannelIdList = jsonConfig.adminConfig.feedbackChannelIdList,
                        adminServerId = jsonConfig.adminConfig.adminServerId,
                    ),
                    statsConfig = Config.StatsConfig(
                        isEnabled = jsonConfig.statsConfig.isEnabled,
                        statsChannelIdList = jsonConfig.statsConfig.statsChannelIdList,
                    )
                )
            }
        return result
    }


    private companion object {
        const val CONFIG_PATH = "res/config.json"
        const val TAG = "LoadConfigurationUseCase"
    }
}