package io.github.sophon.core.feature

import kotlinx.serialization.Serializable

@Serializable
data class FeatureConfig(
    val featureList: List<Feature>,
) {
    @Serializable
    data class Feature(
        val name: String,
        val isEnabled: Boolean,
    )
}
