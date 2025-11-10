package io.github.sophon.cornerman.featureRegistry.superComboWiki

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.cornerman.featureRegistry.ComposeRegisteredFeature
import io.github.sophon.cornerman.featureRegistry.superComboWiki.ui.SuperComboHomeScreenView
import kotlinx.coroutines.flow.Flow

class SupercomboWikiComposeFeature: ComposeRegisteredFeature {
    override val featureInfo = FeatureInfo(
        name = "SuperCombo Wiki",
        url = "https://wiki.supercombo.gg/w/Main_Page",
        iconUrl = "https://i.imgur.com/aW5ys7q.png",
    )

    @Composable
    override fun HomeScreenContent(
        navHostController: NavHostController
    ) {
        SuperComboHomeScreenView(
            featureInfo = featureInfo,
            onCharacterClick = {
                //TODO: navigate
            }
        )
    }

    override suspend fun onInit() { /* not needed */ }

    override suspend fun search(query: String) {
        TODO("Not yet implemented")
    }

    override fun subscribeToSearchResults(): Flow<String> {
        TODO("Not yet implemented")
    }
}