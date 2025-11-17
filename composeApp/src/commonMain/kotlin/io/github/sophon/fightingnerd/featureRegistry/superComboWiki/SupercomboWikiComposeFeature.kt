package io.github.sophon.fightingnerd.featureRegistry.superComboWiki

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.fightingnerd.Destination
import io.github.sophon.fightingnerd.QUALIFIER_SC
import io.github.sophon.fightingnerd.featureRegistry.ComposeRegisteredFeature
import io.github.sophon.fightingnerd.featureRegistry.superComboWiki.ui.SuperComboHomeScreenView
import io.github.sophon.fightingnerd.featureRegistry.superComboWiki.usecase.GetSuperComboFeatureUseCase
import kotlinx.coroutines.flow.Flow

internal class SupercomboWikiComposeFeature(
    getSuperComboFeatureUseCase: GetSuperComboFeatureUseCase,
): ComposeRegisteredFeature {
    override val featureInfo: FeatureInfo = getSuperComboFeatureUseCase.invoke()

    @Composable
    override fun HomeScreenContent(
        navHostController: NavHostController
    ) {
        SuperComboHomeScreenView(
            featureInfo = featureInfo,
            onCharacterClick = { charName ->
                navHostController.navigate(
                    Destination.MoveList(
                        charName = charName,
                        wikiQualifier = QUALIFIER_SC
                    )
                )
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