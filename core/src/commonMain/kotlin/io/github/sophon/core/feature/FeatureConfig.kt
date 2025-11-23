package io.github.sophon.core.feature

import kotlinx.serialization.Serializable

/**
 * Class that's loaded from `config.json`.
 *
 * Might become redundant one day with refactor from `config.json` → static class
 */
@Serializable
data class FeatureConfig(
    val featureList: List<Feature>,
) {
    @Serializable
    data class Feature(
        val name: String,
        val isEnabled: Boolean,
        val supportedGameList: List<Game>,
    )
}
