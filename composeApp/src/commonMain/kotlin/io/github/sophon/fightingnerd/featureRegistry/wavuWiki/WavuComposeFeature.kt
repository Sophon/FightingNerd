package io.github.sophon.fightingnerd.featureRegistry.wavuWiki

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.fightingnerd.Destination
import io.github.sophon.fightingnerd.featureRegistry.ComposeRegisteredFeature
import io.github.sophon.fightingnerd.featureRegistry.wavuWiki.ui.WavuHomeScreenView
import io.github.sophon.wikiwavu.domain.WavuFeatureInfo
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

internal class WavuComposeFeature(
    wavuFeatureInfo: WavuFeatureInfo,
    private val characterDBFactory: (String) -> CharacterListDB,
    private val moveDBFactory: (String) -> MoveListDB,
): ComposeRegisteredFeature, KoinComponent {
    override val featureInfo: FeatureInfo = wavuFeatureInfo.featureInfo
    private val wikis = mutableMapOf<String, WikiClient>()

    override fun registerGames(enabledGames: List<String>) {
        enabledGames.filter { it in featureInfo.supportedGames }
            .forEach { gameId ->
                wikis[gameId] = get(named("wavu")) {
                    parametersOf(
                        gameId,
                        characterDBFactory(gameId),
                        moveDBFactory(gameId),
                    )
                }
            }
    }

    override fun getWikiClient(gameId: String): WikiClient? = wikis[gameId]

    @Composable
    override fun HomeScreenContent(
        navHostController: NavHostController,
    ) {
        val gameId = wikis.keys.firstOrNull() ?: return

        WavuHomeScreenView(
            featureInfo = featureInfo,
            onCharacterClick = { charName ->
                navHostController.navigate(
                    Destination.MoveList(
                        gameId = gameId,
                        charName = charName,
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