package io.github.sophon.fightingnerd.feat.quiz.usecase

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import io.github.sophon.core.featureConfig.FeatureRepo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.fightingnerd.core.ui.components.CharacterCard
import io.github.sophon.fightingnerd.feat.more.util.featureKey
import io.github.sophon.fightingnerd.feat.quiz.model.QuizGameWidget
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalCoroutinesApi::class)
internal class SubscribeGameWidgetsUseCase(
    private val store: DataStore<Preferences>,
    private val featureRepo: FeatureRepo,
) {
    fun invoke(): Flow<List<QuizGameWidget>> {
        val widgets = store.data.flatMapLatest { preferences ->
            val enabledPairs = featureRepo.getGameClients()
                .filter { (game, wikiClient) ->
                    preferences[featureKey(wikiClient.featureInfo.name, game.id)] == true
                }
                .map { (game, wikiClient) ->
                    game to wikiClient
                }

            if (enabledPairs.isEmpty()) {
                flowOf(emptyList())
            } else {
                combine(enabledPairs.toCharacterFlows()) { gameCharacterLists ->
                    val gameWidgetList = enabledPairs.mapIndexed { index, (game, wikiClient) ->
                        val characters = gameCharacterLists[index]
                        QuizGameWidget(
                            game = game,
                            featureName = wikiClient.featureInfo.name,
                            isReady = characters.isNotEmpty(),
                            characterList = characters.toCharacterCards(),
                        )
                    }
                    gameWidgetList
                }
            }
        }

        return widgets
    }

    private fun List<Pair<Game, WikiClient>>.toCharacterFlows(): List<Flow<List<Character>>> {
        val characterFlows = map { (_, wikiClient) ->
            wikiClient.subscribeToCharacterList()
        }
        return characterFlows
    }

    private fun List<Character>.toCharacterCards(): ImmutableList<CharacterCard> {
        val cards = map { character ->
            CharacterCard(
                id = character.id,
                displayName = character.displayName,
                iconUrl = character.images?.iconUrl,
            )
        }.toImmutableList()

        return cards
    }
}
