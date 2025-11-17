package io.github.sophon.core.feature

data class FeatureInfo(
    val name: String,
    val url: String,
    val iconUrl: String? = null,
    val version: String,
)
