package io.github.sophon.discord.config

import io.github.aakira.napier.Napier
import io.github.sophon.core.feature.FeatureConfig
import io.github.sophon.core.util.getGame
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

internal class ConfigLoader(
    private val json: Json,
) {
    fun loadConfig(): FeatureConfig {
        val configText = File(CONFIG_PATH).readText()
        val jsonConfig = json.decodeFromString<JsonConfig>(configText).apply {
            Napier.d(tag = TAG) { this.toString() }
        }

        return FeatureConfig(
            featureList = jsonConfig.featureList.map { feature ->
                FeatureConfig.Feature(
                    name = feature.name,
                    isEnabled = feature.isEnabled,
                    supportedGameList = feature.supportedGames.mapNotNull { gameId ->
                        gameId.getGame()
                    }
                )
            }
        )
    }


    private companion object {
        const val CONFIG_PATH = "res/config.json"
        const val TAG = "ConfigLoader"
    }

    @Serializable
    private data class JsonConfig(
        val featureList: List<Feature>,
    ) {
        @Serializable
        data class Feature(
            val name: String,
            val isEnabled: Boolean,
            val supportedGames: List<String>,
        )
    }
}