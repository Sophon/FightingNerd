package io.github.sophon.fightingnerd.feat.module.usecase

import fightingnerd.composeapp.generated.resources.Res
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.Result
import io.github.sophon.core.feature.Config
import io.github.sophon.core.feature.Game
import io.github.sophon.fightingnerd.core.model.AppError
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.MissingResourceException

internal class LoadConfigUseCase(
    private val json: Json,
) {
    suspend fun invoke(): Result<Config, AppError> {
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
        const val CONFIG_PATH = "files/modules.json"
        const val TAG = "LoadConfigUseCase"
    }
}
