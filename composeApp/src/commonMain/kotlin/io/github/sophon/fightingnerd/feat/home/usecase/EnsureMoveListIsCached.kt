package io.github.sophon.fightingnerd.feat.home.usecase

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.featureConfig.CoreFeatureRepo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.fightingnerd.core.model.AppError
import io.github.sophon.fightingnerd.core.util.mapWikiError

internal class EnsureMoveListIsCached(
    private val featureRepo: CoreFeatureRepo,
) {
    suspend fun invoke(
        game: Game,
        characterId: String,
    ): EmptyResult<AppError> {
        val wikiClient = featureRepo.getWikiClientFor(game)
            ?: return Result.Error(AppError.WikiError(game.id))
        val character = when (val result = wikiClient.fetchCharacter(characterQuery = characterId)) {
            is Result.Success -> result.data
            is Result.Error -> return Result.Error(AppError.WikiError(result.error.toString()))
        }

        val result = wikiClient.checkHasCachedMoves(characterId = character.id)
            .mapWikiError()
            .flatMap { hasCachedMoveList ->
                if (hasCachedMoveList) {
                    Result.Success(Unit)
                } else {
                    Napier.d(tag = "Sorry") { "refreshing: $characterId (${game.id})" }
                    refreshMoveList(character, wikiClient)
                }
            }

        return result
    }

    private suspend fun refreshMoveList(
        character: Character,
        wikiClient: WikiClient
    ): EmptyResult<AppError> {
        val result = wikiClient.downloadMoveListFor(character)
            .mapWikiError()
            .flatMap { moveList ->
                wikiClient.cacheMoveList(character, moveList)
                    .mapWikiError()
            }
        return result
    }


    private companion object {
        const val TAG = "EnsureMoveListIsCached"
    }
}