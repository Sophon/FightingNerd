package io.github.sophon.core.feature

data class FeatureInfo(
    val name: String,
    val url: String,
    val version: String,
    val supportedGames: Set<String>,
    val iconUrl: String? = null,
)
