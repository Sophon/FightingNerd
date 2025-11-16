package io.github.sophon.fightingnerd.featureRegistry

import fightingnerd.composeapp.generated.resources.Res
import io.github.aakira.napier.Napier
import io.github.sophon.core.feature.FeatureConfig
import kotlinx.serialization.json.Json

interface FeatureListLoader {
    suspend fun loadFeatureList(): FeatureConfig
}

internal class FeatureListLoaderImpl(
    private val json: Json,
): FeatureListLoader {
    override suspend fun loadFeatureList(): FeatureConfig {
        val featureListString = Res.readBytes(CONFIG_PATH).decodeToString()
        return json.decodeFromString<FeatureConfig>(featureListString).apply {
            Napier.d(tag = TAG) { this.toString() }
        }
    }


    private companion object {
        const val CONFIG_PATH = "files/features.json"
        const val TAG = "FeatureListLoader"
    }
}