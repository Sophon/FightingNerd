package io.github.sophon.fightingnerd.feat.home.usecase

import io.github.sophon.core.featureConfig.FeatureRepo
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.fightingnerd.feat.home.ui.HomeViewState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.onEach

internal class SubscribeToCharacterListUseCase(
    private val featureRepo: FeatureRepo,
) {
    fun invoke(gameWidget: HomeViewState.GameWidget): Flow<List<Character>> {
        val wikiClient = featureRepo.getWikiClientFor(gameWidget.game)
            ?: return emptyFlow()

        val characterListFlow = wikiClient.subscribeToCharacterList()
            .onEach { characters ->
                if (characters.isEmpty()) {
                    wikiClient.refreshData()
                }
            }
        return characterListFlow
    }
}