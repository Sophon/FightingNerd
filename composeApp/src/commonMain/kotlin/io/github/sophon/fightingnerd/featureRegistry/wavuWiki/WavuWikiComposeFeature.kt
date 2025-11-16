package io.github.sophon.fightingnerd.featureRegistry.wavuWiki

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.fightingnerd.Destination
import io.github.sophon.fightingnerd.QUALIFIER_WAVU
import io.github.sophon.fightingnerd.featureRegistry.ComposeRegisteredFeature
import io.github.sophon.fightingnerd.featureRegistry.wavuWiki.ui.WavuHomeScreenView
import io.github.sophon.fightingnerd.featureRegistry.wavuWiki.usecase.GetWavuFeatureUseCase
import kotlinx.coroutines.flow.Flow

internal class WavuWikiComposeFeature(
    getWavuFeatureUseCase: GetWavuFeatureUseCase,
): ComposeRegisteredFeature {
    override val featureInfo: FeatureInfo = getWavuFeatureUseCase.invoke()

    @Composable
    override fun HomeScreenContent(
        navHostController: NavHostController,
    ) {
        WavuHomeScreenView(
            featureInfo = featureInfo,
            onCharacterClick = { charName ->
                navHostController.navigate(
                    Destination.MoveList(
                        charName = charName,
                        wikiQualifier = QUALIFIER_WAVU,
                    )
                )
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