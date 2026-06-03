package io.github.sophon.discord.feat.config

import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.flatMap
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.feature.Config
import io.github.sophon.core.feature.module.CoreFeatureRepo
import io.github.sophon.core.util.getGame
import io.github.sophon.discord.feat.core.data.FileManager
import io.github.sophon.discord.feat.core.domain.model.BotError
import kotlinx.serialization.json.Json
import io.github.sophon.discord.feat.config.data.JsonConfig
import io.github.sophon.discord.feat.core.domain.toDomainError

internal class InitializeFeaturesFromConfigUseCase(
    private val json: Json,
    private val fileManager: FileManager,
    private val coreFeatureRepo: CoreFeatureRepo,
) {
    fun invoke(): EmptyResult<BotError> {
        val result = loadConfiguration()
            .flatMap { config ->
                coreFeatureRepo.initialize(config).mapError { it.toDomainError() }
            }

        return result
    }

    private fun loadConfiguration(): Result<Config, BotError> {
        val result = fileManager.read(CONFIG_PATH)
            .map { configText ->
                val jsonConfig = json.decodeFromString<JsonConfig>(configText).apply {
                    Napier.d(tag =TAG) { this.toString() }
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
        const val TAG = "LoadConfigUseCase"
    }
}