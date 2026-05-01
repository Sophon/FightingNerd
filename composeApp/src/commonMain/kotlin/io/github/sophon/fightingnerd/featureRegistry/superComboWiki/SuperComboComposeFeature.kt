package io.github.sophon.fightingnerd.featureRegistry.superComboWiki

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.feature.Game
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.fightingnerd.Destination
import io.github.sophon.fightingnerd.featureRegistry.ComposeRegisteredFeature
import io.github.sophon.fightingnerd.featureRegistry.superComboWiki.ui.SuperComboHomeScreenView
import io.github.sophon.wikiSuperCombo.integration.SuperComboFeatureInfo
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

internal class SuperComboComposeFeature(
    supercomboFeatureInfo: SuperComboFeatureInfo,
    private val dbFactory: (String) -> Pair<CharacterListDB, MoveListDB>,
): ComposeRegisteredFeature, KoinComponent {
    override val featureInfo: FeatureInfo = supercomboFeatureInfo.featureInfo
    private val wikis = mutableMapOf<String, WikiClient>()

    override fun registerGames(enabledGames: List<Game>) {
        enabledGames.filter { it in featureInfo.supportedGameSet }
            .forEach { game ->
                val (characterDB, moveDB) = dbFactory(game.id)
                wikis[game.id] = get(named("supercombo")) {
                    parametersOf(
                        game.id,
                        characterDB,
                        moveDB,
                    )
                }
            }
    }

    override fun getWikiClient(gameId: String): WikiClient? = wikis[gameId]

    @Composable
    override fun HomeScreenContent(
        navHostController: NavHostController
    ) {
        val gameId = wikis.keys.firstOrNull() ?: return

        SuperComboHomeScreenView(
            featureInfo = featureInfo,
            onCharacterClick = { charName ->
                navHostController.navigate(
                    Destination.MoveList(
                        gameId = gameId, //TODO: we should pass all games
                        charName = charName,
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