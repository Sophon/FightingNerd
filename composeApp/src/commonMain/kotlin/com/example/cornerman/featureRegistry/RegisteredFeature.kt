package com.example.cornerman.featureRegistry

import kotlinx.coroutines.flow.Flow

interface RegisteredFeature {
    val featureInfo: FeatureInfo

    suspend fun search(query: String)
    fun subscribeToSearchResults(): Flow<String>
}

data class FeatureInfo(
    val name: String,
    val url: String,
    val iconUrl: String? = null,
)