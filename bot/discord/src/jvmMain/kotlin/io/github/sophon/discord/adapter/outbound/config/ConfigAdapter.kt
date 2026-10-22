package io.github.sophon.discord.adapter.outbound.config

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.discord.DiscordConfig
import io.github.sophon.discord.app.outPort.LoadConfigPort
import io.github.sophon.discord.app.outPort.ReadFilePort
import kotlinx.serialization.json.Json
import java.io.File

internal class ConfigAdapter(
    private val json: Json,
): ReadFilePort, LoadConfigPort {
    private var discordConfig: DiscordConfig? = null

    override fun read(path: String): Result<String, BotError> {
        val result = try {
            Result.Success(File(path).readText())
        } catch (e: Exception) {
            Result.Error(BotError.FileError(e.toErrorMessage(path)))
        }
        return result
    }

    override fun load(): Result<DiscordConfig, BotError> {
        val cachedConfig = discordConfig
        val result = if (cachedConfig != null) {
            Result.Success(cachedConfig)
        } else {
            decodeConfig().onSuccess { discordConfig = it }
        }
        return result
    }


    private fun decodeConfig(): Result<DiscordConfig, BotError> {
        val result = read(CONFIG_PATH)
            .flatMap { configText ->
                try {
                    Result.Success(json.decodeFromString<DiscordConfig>(configText))
                } catch (e: Exception) {
                    Result.Error(BotError.FileError(e.toErrorMessage(CONFIG_PATH)))
                }
            }
        return result
    }

    private fun Exception.toErrorMessage(path: String): String {
        val message = "${this.message}: $path"
        return message
    }
}


private const val CONFIG_PATH = "res/discordConfig.json"
