package io.github.sophon.fightingnerd.featureRegistry

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import io.github.sophon.core.feature.FeatureInfo
import kotlinx.coroutines.flow.Flow

interface ComposeRegisteredFeature {
    val featureInfo: FeatureInfo

    @Composable
    fun HomeScreenContent(navHostController: NavHostController)
    suspend fun onInit()

    suspend fun search(query: String)
    fun subscribeToSearchResults(): Flow<String>
}
