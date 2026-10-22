package io.github.sophon.fightingnerd.app.model

import kotlinx.serialization.Serializable

@Serializable
internal data class ComposeConfig(
    val featureList: List<Feature>,
) {
    /**
     * Games of features disabled in the config aren't available, same as the legacy `FeatureRepo`.
     */
    val availableFeatureList: List<Feature>
        get() {
            return featureList.filter { feature -> feature.isEnabled }
        }

    @Serializable
    data class Feature(
        val name: String,
        val isEnabled: Boolean,
        val supportedGames: List<String>,
    )
}
