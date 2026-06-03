package io.github.sophon.fightingnerd.feat.home.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.flatMap
import io.github.sophon.core.domain.map
import io.github.sophon.core.feature.Game
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.core.wiki.usecase.DownloadMoveListUseCase
import io.github.sophon.fightingnerd.core.model.AppError
import io.github.sophon.fightingnerd.core.util.mapWikiError
import io.github.sophon.fightingnerd.feat.module.ModuleRepo

internal class LoadMoveListUseCase(
    private val moduleRepo: ModuleRepo,
) {
    suspend fun invoke(
        game: Game,
        characterQueryId: String,
    ): Result<List<Move>, AppError> {
        val wikiClient = moduleRepo.getWikiClientFor(game)
            ?: return Result.Error(AppError.WikiError(game.id))
        val character = when (val result = wikiClient.fetchCharacter(charName = characterQueryId)) {
            is Result.Success -> result.data
            is Result.Error -> return Result.Error(AppError.WikiError(result.error.toString()))
        }

        val result = wikiClient.fetchMoveList(charName = characterQueryId)
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
        val characterData = DownloadMoveListUseCase.CharacterData(name = character.queryName, imageUrl = null)

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