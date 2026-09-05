package io.github.sophon.fightingnerd.feat.quiz.usecase

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import io.github.sophon.core.featureConfig.FeatureRepo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.CharacterId
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.fightingnerd.core.ui.components.CharacterCard
import io.github.sophon.fightingnerd.feat.more.util.featureKey
import io.github.sophon.fightingnerd.feat.quiz.model.QuizGameWidget
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

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
                combine(enabledPairs.toCharacterCardFlows()) { perGameCards ->
                    val gameWidgetList = enabledPairs.mapIndexed { index, (game, wikiClient) ->
                        val cards = perGameCards[index]
                        QuizGameWidget(
                            game = game,
                            featureName = wikiClient.featureInfo.name,
                            isReady = cards.isNotEmpty(),
                            isPlayable = cards.isNotEmpty() && cards.all { it.isLoading.not() },
                            characterList = cards,
                        )
                    }
                    gameWidgetList
                }
            }
        }

        return widgets
    }

    private fun List<Pair<Game, WikiClient>>.toCharacterCardFlows(): List<Flow<ImmutableList<CharacterCard>>> {
        val flows = map { (_, wikiClient) ->
            wikiClient.subscribeToCharacterCards()
        }
        return flows
    }

    private fun WikiClient.subscribeToCharacterCards(): Flow<ImmutableList<CharacterCard>> {
        val flow = subscribeToCharacterList().flatMapLatest { characters ->
            if (characters.isEmpty()) {
                flowOf(persistentListOf())
            } else {
                combine(characters.map { character -> subscribeToCharacterCard(character) }) { cards ->
                    cards.toList().toImmutableList()
                }
            }
        }
        return flow
    }

    private fun WikiClient.subscribeToCharacterCard(character: Character): Flow<CharacterCard> {
        val flow = subscribeToMoveList(CharacterId(character.id))
            .map { moveList ->
                CharacterCard(
                    id = character.id,
                    displayName = character.displayName,
                    iconUrl = character.images?.iconUrl,
                    isLoading = moveList.isEmpty(),
                )
            }
        return flow
    }
}
