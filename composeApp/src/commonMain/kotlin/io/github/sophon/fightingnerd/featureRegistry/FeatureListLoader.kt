package io.github.sophon.fightingnerd.featureRegistry

import fightingnerd.composeapp.generated.resources.Res
import io.github.aakira.napier.Napier
import io.github.sophon.core.feature.FeatureConfig
import io.github.sophon.core.feature.Game
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

interface FeatureListLoader {
    suspend fun loadFeatureList(): FeatureConfig
}

internal class FeatureListLoaderImpl(
    private val json: Json,
): FeatureListLoader {
    override suspend fun loadFeatureList(): FeatureConfig {
        val featureListString = Res.readBytes(CONFIG_PATH).decodeToString()
        val jsonConfig = json.decodeFromString<JsonConfig>(featureListString).apply {
            Napier.d(tag = TAG) { this.toString() }
        }

        return FeatureConfig(
            featureList = jsonConfig.featureList.map { feature ->
                FeatureConfig.Feature(
                    name = feature.name,
                    isEnabled = feature.isEnabled,
                    supportedGameList = feature.supportedGames.mapNotNull { gameId ->
                        Game.entries.find { it.id == gameId }
                    }
                )
            }
        )
    }


    private companion object {
        const val CONFIG_PATH = "files/features.json"
        const val TAG = "FeatureListLoader"
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