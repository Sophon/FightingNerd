package com.example.cornerman.featureRegistry

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.Flow

interface RegisteredFeature {
    val featureInfo: FeatureInfo

    val homeScreenContent: @Composable () -> Unit

    suspend fun onInit()

    suspend fun search(query: String)
    fun subscribeToSearchResults(): Flow<String>
}

data class FeatureInfo(
    val name: String,
    val url: String,
    val iconUrl: String? = null,
)