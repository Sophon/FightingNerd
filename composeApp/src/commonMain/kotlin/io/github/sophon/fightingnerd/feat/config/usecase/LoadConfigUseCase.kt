package io.github.sophon.fightingnerd.feat.config.usecase

import fightingnerd.composeapp.generated.resources.Res
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.Result
import io.github.sophon.core.feature.Config
import io.github.sophon.fightingnerd.core.model.AppError
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.MissingResourceException

internal class LoadConfigUseCase(
    private val json: Json,
) {
    suspend fun invoke(): Result<Config, AppError> {
        val result = try {
            val moduleListString = Res.readBytes(CONFIG_PATH).decodeToString()
            val config = json.decodeFromString<Config>(moduleListString).apply {
                Napier.d(tag = TAG) { this.toString() }
            }
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


    private companion object {
        const val CONFIG_PATH = "files/modules.json"
        const val TAG = "LoadModulesUseCase"
    }
}
