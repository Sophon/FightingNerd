package com.example.cornerman.featureRegistry.wavuWiki

import androidx.compose.runtime.Composable
import com.example.cornerman.featureRegistry.FeatureInfo
import com.example.cornerman.featureRegistry.RegisteredFeature
import kotlinx.coroutines.flow.Flow

class WavuWikiFeature: RegisteredFeature {
    override val featureInfo = FeatureInfo(
        name = "WavuWiki",
        url = "https://wavu.wiki/t/Main_Page"
    )

    override val homeScreenContent: @Composable () -> Unit = {
        WavuHomeScreenView(
            onCharacterClick = {} //TODO: handle navigation
        )
    }

    override suspend fun onInit() {
        // not needed
    }

    override suspend fun search(query: String) {
        TODO("not yet implemented")
    }

    override fun subscribeToSearchResults(): Flow<String> {
        TODO("not yet implemented")
    }
}