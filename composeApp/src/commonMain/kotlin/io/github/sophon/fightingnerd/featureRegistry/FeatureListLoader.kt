package io.github.sophon.fightingnerd.featureRegistry

import fightingnerd.composeapp.generated.resources.Res
import io.github.aakira.napier.Napier
import io.github.sophon.core.feature.Config
import io.github.sophon.core.util.getGame
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

//internal interface FeatureListLoader {
//    suspend fun loadFeatureList(): Config
//}
//
//internal class FeatureListLoaderImpl(
//    private val json: Json,
//): FeatureListLoader {
//    override suspend fun loadFeatureList(): Config {
//        val featureListString = Res.readBytes(CONFIG_PATH).decodeToString()
//        val jsonConfig = json.decodeFromString<JsonConfig>(featureListString).apply {
//            Napier.d(tag = TAG) { this.toString() }
//        }
//
//        return Config(
//            featureList = jsonConfig.featureList.map { feature ->
//                Config.Feature(
//                    name = feature.name,
//                    isEnabled = feature.isEnabled,
//                    supportedGameList = feature.supportedGames.mapNotNull { gameId ->
//                        gameId.getGame()
//                    }
//                )
//            }
//        )
//    }
//
//
//    private companion object {
//        const val CONFIG_PATH = "files/modules.json"
//        const val TAG = "FeatureListLoader"
//    }
//
//    @Serializable
//    private data class JsonConfig(
//        val featureList: List<Feature>,
//    ) {
//        @Serializable
//        data class Feature(
//            val name: String,
//            val isEnabled: Boolean,
//            val supportedGames: List<String>,
//            val feedbackDiscordChannelId: String? = null,
//        )
//    }
//}