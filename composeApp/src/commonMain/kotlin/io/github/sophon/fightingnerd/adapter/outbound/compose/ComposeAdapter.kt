package io.github.sophon.fightingnerd.adapter.outbound.compose

import fightingnerd.composeapp.generated.resources.Res
import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.ComposeConfig
import io.github.sophon.fightingnerd.app.outPort.LoadConfigPort
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.MissingResourceException

@ExcludeFromCoverage("TODO: find a way to mock RES")
internal class ComposeAdapter(
    private val json: Json,
): LoadConfigPort {
    override suspend fun load(): Result<ComposeConfig, AppError> {
        val result = try {
            val configText = Res.readBytes(CONFIG_PATH).decodeToString()
            Result.Success(json.decodeFromString<ComposeConfig>(configText))
        } catch (e: MissingResourceException) {
            Result.Error(AppError.ConfigNotFoundError(e.message.orEmpty()))
        } catch (e: SerializationException) {
            Result.Error(AppError.ConfigParseError(e.message.orEmpty()))
        }
        return result
    }
}


private const val CONFIG_PATH = "files/composeConfig.json"
