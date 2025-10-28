package com.example.cornerman.featureRegistry

interface RegisteredFeature {
    val featureInfo: FeatureInfo

    suspend fun search(query: String)
}

data class FeatureInfo(
    val name: String,
    val url: String,
    val iconUrl: String? = null,
)