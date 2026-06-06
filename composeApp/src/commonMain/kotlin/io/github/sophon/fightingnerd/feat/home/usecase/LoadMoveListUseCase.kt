package io.github.sophon.fightingnerd.feat.home.usecase

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.featureConfig.CoreFeatureRepo
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.usecase.DownloadMoveListUseCase
import io.github.sophon.fightingnerd.core.model.AppError
import io.github.sophon.fightingnerd.core.util.mapWikiError

internal class LoadMoveListUseCase(
    private val featureRepo: CoreFeatureRepo,
) {
    suspend fun invoke(
        game: Game,
        characterQueryId: String,
    ): Result<List<Move>, AppError> {
        val wikiClient = featureRepo.getWikiClientFor(game)
            ?: return Result.Error(AppError.WikiError(game.id))
        val character = when (val result = wikiClient.fetchCharacter(characterQuery = characterQueryId)) {
            is Result.Success -> result.data
            is Result.Error -> return Result.Error(AppError.WikiError(result.error.toString()))
        }

        val result = wikiClient.fetchMoveList(characterQuery = characterQueryId)
            .mapWikiError()
            .flatMap { cachedMoveList ->
                if (cachedMoveList.isEmpty()) {
                    refreshMoveList(character, wikiClient)
                } else {
                    Result.Success(cachedMoveList)
                }
            }

        return result
    }

    private suspend fun refreshMoveList(
        character: Character,
        wikiClient: WikiClient
    ): Result<List<Move>, AppError> {
        val characterData = DownloadMoveListUseCase.CharacterData(name = character.remoteQueryId, imageUrl = null)

        val result = wikiClient.downloadMoveList(characterData)
            .mapWikiError()
            .flatMap { moveList ->
                wikiClient.cacheMoveList(character, moveList)
                    .map { moveList }
                    .mapWikiError()
            }
        return result
    }
}