package io.github.sophon.core.feature

import kotlinx.serialization.Serializable

/**
 * Class that's loaded from `config.json`.
 *
 * Might become redundant one day with refactor from `config.json` → static class
 */
@Serializable
data class Config(
    val featureList: List<Feature>,
    val adminConfig: AdminConfig,
) {
    @Serializable
    data class Feature(
        val name: String,
        val isEnabled: Boolean,
        val supportedGameList: List<Game>,
    )

    @Serializable
    data class AdminConfig(
        val administratorIdList: List<String>,
        val feedbackChannelIdList: List<String>,
        val adminServerId: String,
    )
}
