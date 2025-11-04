package io.github.sophon.cornerman.featureRegistry.wavuWiki

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import io.github.sophon.core.domain.FeatureInfo
import io.github.sophon.cornerman.Destination
import io.github.sophon.cornerman.featureRegistry.ComposeRegisteredFeature
import kotlinx.coroutines.flow.Flow

class WavuWikiFeatureCompose: ComposeRegisteredFeature {
    override val featureInfo = FeatureInfo(
        name = "Wavu Wiki",
        url = "https://wavu.wiki/t/Main_Page",
        iconUrl = "https://i.imgur.com/0cnTzNk.png",
    )

    @Composable
    override fun HomeScreenContent(
        navHostController: NavHostController,
    ) {
        WavuHomeScreenView(
            featureInfo = featureInfo,
            onCharacterClick = { charName ->
                navHostController.navigate(Destination.MoveList(charName))
            },
        )
    }

    override suspend fun onInit() { /* not needed */ }

    override suspend fun search(query: String) {
        TODO("not yet implemented")
    }

    override fun subscribeToSearchResults(): Flow<String> {
        TODO("not yet implemented")
    }
}