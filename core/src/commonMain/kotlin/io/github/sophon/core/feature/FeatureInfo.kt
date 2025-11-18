package io.github.sophon.core.feature

data class FeatureInfo(
    val name: String,
    val url: String,
    val version: String,
    val supportedGames: Set<String> = setOf(),
    val iconUrl: String? = null,
)
