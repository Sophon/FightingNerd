package io.github.sophon.discord.config

import io.github.aakira.napier.Napier
import io.github.sophon.core.feature.Config
import io.github.sophon.core.util.getGame
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

internal class ConfigLoader(
    private val json: Json,
) {
    fun loadConfig(): Config {
        val configText = File(CONFIG_PATH).readText()
        val jsonConfig = json.decodeFromString<JsonConfig>(configText).apply {
            Napier.d(tag = TAG) { this.toString() }
        }

        return Config(
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
            )
        )
    }


    private companion object {
        const val CONFIG_PATH = "res/config.json"
        const val TAG = "ConfigLoader"
    }

    @Serializable
    private data class JsonConfig(
        val featureList: List<Feature>,
        val adminConfig: AdminConfig,
    ) {
        @Serializable
        data class Feature(
            val name: String,
            val isEnabled: Boolean,
            val supportedGames: List<String>,
        )

        @Serializable
        data class AdminConfig(
            val administratorIdList: List<String>,
            val feedbackChannelIdList: List<String>,
            val adminServerId: String,
        )
    }
}