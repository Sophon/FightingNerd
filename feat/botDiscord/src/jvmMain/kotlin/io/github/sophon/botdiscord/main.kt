package io.github.sophon.botdiscord

import CONFIG_FILE_NAME
import io.github.sophon.botdiscord.config.DcConfig
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent.getKoin
import java.io.File

suspend fun main() = coroutineScope {
    val apiKey = getApiKey()
    Napier.base(DebugAntilog())
    initKoin(apiKey)

    val discordBot = getKoin().get<DiscordBot>()

    launch {
        discordBot.startSession()
    }.join()
}

private fun getApiKey(): String {
    // env var first (for production/Docker)
    System.getenv("discordBotApiKey")?.let { return it }

    // fall back to config file (for local development)
    val configFile = File(CONFIG_FILE_NAME)
    if (configFile.exists().not()) {
        throw IllegalStateException("No API key found. Set DISCORD_API_KEY env var or create $CONFIG_FILE_NAME")
    }

    val json = Json {
        ignoreUnknownKeys = true
    }
    val dcConfig = json.decodeFromString<DcConfig>(configFile.readText())

    return dcConfig.discordBotApiKey
}