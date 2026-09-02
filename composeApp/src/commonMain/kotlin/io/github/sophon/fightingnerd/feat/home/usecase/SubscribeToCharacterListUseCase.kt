package io.github.sophon.fightingnerd.feat.home.usecase

import io.github.sophon.core.featureConfig.FeatureRepo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

internal class SubscribeToCharacterListUseCase(
    private val featureRepo: FeatureRepo,
) {
    operator fun invoke(game: Game): Flow<List<Character>> {
        val wikiClient = featureRepo.getWikiClientFor(game)
            ?: return emptyFlow()

        val flow = wikiClient.subscribeToCharacterList()
        return flow
    }
}
