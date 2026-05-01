package io.github.sophon.discord

import dev.kord.core.Kord
import io.github.aakira.napier.Antilog
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import io.github.sophon.core.util.maskSecret
import io.github.sophon.discord.feat.bot.DiscordBot
import io.github.sophon.discord.feat.config.DiscordConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent.getKoin
import java.io.EOFException
import java.io.File

internal suspend fun main() = coroutineScope {
    initLogging()
    val kord = createKord()
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

                        if (throwable !is EOFException) {
                            throwable?.printStackTrace()
                        }
                    }
                }
            }
        )
    }
}

private suspend fun createKord(): Kord {
    return Kord(token = getApiKey()) {
        httpClient = HttpClient(Java) {
            install(ContentNegotiation) {
                json(
                    Json {
                        encodeDefaults = false
                        allowStructuredMapKeys = true
                        ignoreUnknownKeys = true
                        isLenient = true
                    }
                )
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 1L shl 20
                socketTimeoutMillis = requestTimeoutMillis
            }

            install(WebSockets)
        }
    }
}

private fun getApiKey(): String {
    // env var first (for production/Docker)
    System.getenv(ENV_API_DISCORD)?.let { apiKey ->
        Napier.i(tag = TAG) { "API from env: ${apiKey.maskSecret()}" }
        return apiKey
    }

    // fall back to config file (for local development)
    val configFile = File(CONFIG_FILE_NAME)
    if (configFile.exists().not()) {
        throw IllegalStateException("No API key found. Set $ENV_API_DISCORD env var or create $CONFIG_FILE_NAME")
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