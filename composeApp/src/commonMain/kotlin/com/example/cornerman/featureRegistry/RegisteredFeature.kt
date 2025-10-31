package com.example.cornerman.featureRegistry

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.Flow

interface RegisteredFeature {
    val featureInfo: FeatureInfo

    @Composable
    fun HomeScreenContent(navHostController: NavHostController)
    suspend fun onInit()

    suspend fun search(query: String)
    fun subscribeToSearchResults(): Flow<String>
}

data class FeatureInfo(
    val name: String,
    val url: String,
    val iconUrl: String? = null,
)