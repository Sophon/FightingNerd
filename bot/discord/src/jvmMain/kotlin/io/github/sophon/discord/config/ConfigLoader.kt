package io.github.sophon.discord.config

import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.FeatureConfig
import kotlinx.serialization.json.Json
import java.io.File

internal class ConfigLoader(
    private val json: Json,
) {
    fun loadConfig(): FeatureConfig {
        val configText = File(CONFIG_PATH).readText()
        return json.decodeFromString<FeatureConfig>(configText).apply {
            Napier.d(tag = TAG) { this.toString() }
        }
    }


    private companion object {
        const val CONFIG_PATH = "res/config.json"
        const val TAG = "ConfigLoader"
    }
}