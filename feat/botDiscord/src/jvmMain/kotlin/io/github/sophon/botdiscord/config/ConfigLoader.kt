package io.github.sophon.botdiscord.config

import io.github.aakira.napier.Napier
import kotlinx.serialization.json.Json
import java.io.File

internal class ConfigLoader(
    private val json: Json,
) {
    fun loadConfig(): BotConfig {
        val configText = File(CONFIG_PATH).readText()
        return json.decodeFromString<BotConfig>(configText).apply {
            Napier.d(tag = TAG) { this.toString() }
        }
    }


    private companion object {
        const val CONFIG_PATH = "res/config.json"
        const val TAG = "ConfigLoader"
    }
}