package io.github.sophon.fightingnerd.feat.module.usecase

import fightingnerd.composeapp.generated.resources.Res
import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Config
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.feat.module.CONFIG_PATH
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.MissingResourceException

@ExcludeFromCoverage("TODO: find a way to mock RES")
internal class LoadConfigUseCase(
    private val json: Json,

    //region HEX migration
//    private val store: DataStore<Preferences>,
//    private val configureWikiUseCase: ConfigureWikiUseCase,
    //endregion
) {
    suspend operator fun invoke(): Result<Config, AppError> {
        val result = try {
            val configString = Res.readBytes(CONFIG_PATH).decodeToString()
            val jsonConfig = json.decodeFromString<JsonConfig>(configString).apply {
                Napier.d(tag = TAG) { this.toString() }
            }

            val config = Config(
                featureList = jsonConfig.featureList.map { feature ->
                    Config.Feature(
                        name = feature.name,
                        isEnabled = feature.isEnabled,
                        supportedGameList = feature.supportedGameList.mapNotNull { gameId ->
                            val game = Game.fromId(gameId)
                            if (game == null) {
                                Napier.w(tag = TAG) { "Unknown game id, skipping: $gameId" }
                            }
                            game
                        },
                    )
                },
            )

            //region HEX migration
//            bindHexagonalWiki(config)
            //endregion

            Result.Success(config)
        } catch (e: MissingResourceException) {
            val errorMessage = e.message ?: "Config file not found"
            Napier.e(tag = TAG) { errorMessage }
            Result.Error(AppError.ConfigNotFoundError(errorMessage))
        } catch (e: SerializationException) {
            val errorMessage = e.message ?: "Failed to parse config"
            Napier.e(tag = TAG) { errorMessage }
            Result.Error(AppError.ConfigParseError(errorMessage))
        }

        return result
    }

    //region HEX migration
//    private suspend fun bindHexagonalWiki(config: Config) {
//        val prefs = store.data.first()
//
//        config.toWikiConfig(prefs)
//            .flatMap { wikiConfig -> configureWikiUseCase(wikiConfig) }
//            .onError { error ->
//                Napier.e(tag = TAG) { "Hexagonal wiki configuration failed: $error" }
//            }
//    }
//
//    /**
//     * Available games come from `composeConfig.json`, enabled games from DataStore.
//     * Disabled features are dropped, same as the legacy `FeatureRepo`.
//     */
//    private fun Config.toWikiConfig(prefs: Preferences): Result<WikiConfig, WikiError> {
//        val availableFeatureList = featureList.filter { it.isEnabled }
//
//        val availableGameSet = availableFeatureList
//            .flatMap { it.supportedGameList }
//            .toSet()
//        val enabledGameSet = availableFeatureList
//            .flatMap { feature ->
//                feature.supportedGameList.filter { game -> prefs[featureKey(feature.name, game.id)] ?: false }
//            }
//            .toSet()
//
//        val result = WikiConfig.create(
//            availableGameSet = availableGameSet,
//            enabledGameSet = enabledGameSet,
//        )
//        return result
//    }
    //endregion

    @Serializable
    private data class JsonConfig(
        val featureList: List<Feature>,
    ) {
        @Serializable
        data class Feature(
            val name: String,
            val isEnabled: Boolean,
            @SerialName("supportedGames") val supportedGameList: List<String>,
            val feedbackDiscordChannelId: String? = null,
        )
    }


    private companion object {
        const val TAG = "LoadConfigUseCase"
    }
}
