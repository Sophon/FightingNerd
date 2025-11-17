package io.github.sophon.discord

import dev.kord.core.Kord
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.github.sophon.discord.config.DiscordConfig
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent.getKoin
import java.io.File

suspend fun main() = coroutineScope {
    Napier.base(DebugAntilog())
    val kord = Kord(token = getApiKey())
    initKoin(kord)

    val discordBot = getKoin().get<DiscordBot>()

    launch {
        discordBot.startSession()
    }.join()
}

private fun getApiKey(): String {
    // env var first (for production/Docker)
    System.getenv("discordBotApiKey")?.let { apiKey ->
        Napier.d(tag = TAG) { "API from env: $apiKey" }
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

private const val TAG = "DiscordBot"