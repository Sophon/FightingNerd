import config.DcConfig
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

// Just update this function that's already there
private fun getApiKey(): String {
    // Try environment variable first (for production/Docker)
    System.getenv("DISCORD_API_KEY")?.let { return it }

    // Fall back to config file (for local development)
    val configFile = File(CONFIG_FILE_NAME)
    if (!configFile.exists()) {
        throw IllegalStateException("No API key found. Set DISCORD_API_KEY env var or create $CONFIG_FILE_NAME")
    }

    val json = Json {
        ignoreUnknownKeys = true
    }
    val dcConfig = json.decodeFromString<DcConfig>(configFile.readText())

    return dcConfig.discordBotApiKey
}