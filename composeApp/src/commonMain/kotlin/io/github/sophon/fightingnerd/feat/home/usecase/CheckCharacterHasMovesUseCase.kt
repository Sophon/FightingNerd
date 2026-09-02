package io.github.sophon.fightingnerd.feat.home.usecase

import io.github.sophon.core.featureConfig.FeatureRepo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.CharacterId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map

internal class CheckCharacterHasMovesUseCase(
    private val featureRepo: FeatureRepo,
) {
    operator fun invoke(
        game: Game,
        characterId: String,
    ): Flow<Boolean> {
        val wikiClient = featureRepo.getWikiClientFor(game) ?: return emptyFlow()
        val flow = wikiClient.subscribeToMoveList(characterId = CharacterId(characterId))
            .map { moveList -> moveList.isNotEmpty() }
        return flow
    }
}
