package io.github.sophon.fightingnerd.feat.move.usecase

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.FeatureRepo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.CharacterId
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.fightingnerd.core.data.MediaRepo
import io.github.sophon.fightingnerd.core.model.AppError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map

internal class SubscribeToMoveListUseCase(
    private val featureRepo: FeatureRepo,
    private val mediaRepo: MediaRepo,
) {
    fun invoke(
        gameId: String,
        characterId: CharacterId,
    ): Flow<Result<Pair<Character, List<Move>>, AppError>> {
        val game = Game.fromId(id = gameId) ?: return emptyFlow()
        val wiki = featureRepo.getWikiClientFor(game) ?: return emptyFlow()

        val flow = combine(
            wiki.subscribeToCharacterList(),
            subscribeToMoveListWithOfflineMedia(wiki, gameId, characterId),
        ) { characterList, moveList ->
            val character = characterList.firstOrNull { it.id == characterId.value }
                ?: return@combine Result.Error(AppError.WikiError("$characterId not found"))
            Result.Success(Pair(character, moveList))
        }.distinctUntilChanged()
        return flow
    }

    private fun subscribeToMoveListWithOfflineMedia(
        wiki: WikiClient,
        gameId: String,
        characterId: CharacterId,
    ): Flow<List<Move>> {
        val flow = wiki.subscribeToMoveList(characterId).map { moveList ->
            val updatedMoves = moveList.map { move ->
                val updatedUrls = mediaRepo.createUpdatedUrls(
                    gameId = gameId,
                    characterId = characterId,
                    media = move.urls,
                )
                val updatedMove = move.copy(urls = updatedUrls)
                updatedMove
            }
            updatedMoves
        }
        return flow
    }
}
