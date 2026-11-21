package io.github.sophon.discord.adapter.outbound.config

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.core.network.safeCall
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.discord.DiscordConfig
import io.github.sophon.discord.app.outPort.LoadConfigPort
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.serialization.json.Json
import java.io.File

internal class ConfigAdapter(
    private val httpClient: HttpClient,
    private val json: Json,
    private val configPath: String = CONFIG_PATH,
): LoadConfigPort {
    private var discordConfig: DiscordConfig? = null

    override suspend fun load(): Result<DiscordConfig, BotError> {
        val cachedConfig = discordConfig
        val result = if (cachedConfig == null) {
            Result.Error(BotError.ConfigError("Config not downloaded yet"))
        } else {
            Result.Success(cachedConfig)
        }
        return result
    }

    override suspend fun download(): Result<DiscordConfig, BotError> {
        val cachedConfig = discordConfig
        val remoteConfigResult = fetchRemoteConfig()
            .onError { error -> Napier.w(tag = TAG) { "Config download failed: $error" } }

        val result = when {
            remoteConfigResult is Result.Success -> remoteConfigResult
            cachedConfig != null -> Result.Success(cachedConfig)
            else -> readFallbackConfig()
        }
        result.onSuccess { config -> discordConfig = config }

        return result
    }


    private suspend fun fetchRemoteConfig(): Result<DiscordConfig, BotError> {
        val result = safeCall<DiscordConfigDto> { httpClient.get(CONFIG_URL) }
            .map { it.toDomain() }
            .mapError { it.toDomainError() }
        return result
    }

    private fun readFallbackConfig(): Result<DiscordConfig, BotError> {
        val result = readConfigFile()
            .flatMap { configText ->
                try {
                    Result.Success(json.decodeFromString<DiscordConfig>(configText))
                } catch (e: Exception) {
                    Result.Error(BotError.FileError(e.toErrorMessage(configPath)))
                }
            }
        return result
    }

    private fun readConfigFile(): Result<String, BotError> {
        val result = try {
            Result.Success(File(configPath).readText())
        } catch (e: Exception) {
            Result.Error(BotError.FileError(e.toErrorMessage(configPath)))
        }
        return result
    }

    private fun Exception.toErrorMessage(path: String): String {
        val message = "${this.message}: $path"
        return message
    }


    private companion object {
        const val TAG = "ConfigAdapter"
    }
}


private const val CONFIG_PATH = "res/discordConfig.json"
private const val CONFIG_URL = "https://raw.githubusercontent.com/Sophon/FightingNerd/dev/res/discordConfig.json"
