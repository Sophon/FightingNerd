package io.github.sophon.discord

import dev.kord.core.Kord
import io.github.aakira.napier.Antilog
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import io.github.sophon.core.util.maskSecret
import io.github.sophon.discord.config.DiscordConfig
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent.getKoin
import java.io.File

suspend fun main() = coroutineScope {
    initLogging()
    val kord = Kord(token = getApiKey())
    initKoin(kord)

    val discordBot = getKoin().get<DiscordBot>()

    launch {
        discordBot.startSession()
    }.join()
}

private fun initLogging() {
    if (isDebugBuild()) {
        Napier.base(DebugAntilog())
    } else {
        Napier.base(
            object : Antilog() {
                override fun performLog(
                    priority: LogLevel,
                    tag: String?,
                    throwable: Throwable?,
                    message: String?,
                ) {
                    if (priority == LogLevel.INFO || priority == LogLevel.ERROR) {
                        println("${priority.name.uppercase()}: ${tag ?: "null"} - $message")
                        throwable?.printStackTrace()
                    }
                }
            }
        )
    }
}

private fun getApiKey(): String {
    // env var first (for production/Docker)
    System.getenv("discordBotApiKey")?.let { apiKey ->
        Napier.d(tag = TAG) { "API from env: ${apiKey.maskSecret()}" }
        return apiKey
    }

    // fall back to config file (for local development)
    val configFile = File(CONFIG_FILE_NAME)
    if (configFile.exists().not()) {
        throw IllegalStateException("No API key found. Set DISCORD_API_KEY env var or create $CONFIG_FILE_NAME")
    }

    val json = Json {
        ignoreUnknownKeys = true
    }
    val discordConfig = json.decodeFromString<DiscordConfig>(configFile.readText())

    return discordConfig.discordBotApiKey.also { apiKey ->
        Napier.d(tag = TAG) { "API from file: $apiKey" }
    }
}

private fun isDebugBuild(): Boolean {
    return System.getenv(BUILD_KEY_ENV) != BUILD_VAL_PROD
}

private const val TAG = "DiscordBot"