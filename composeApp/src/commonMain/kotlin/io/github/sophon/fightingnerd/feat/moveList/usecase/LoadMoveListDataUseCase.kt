package io.github.sophon.fightingnerd.feat.moveList.usecase

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.featureConfig.CoreFeatureRepo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.fightingnerd.core.model.AppError
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

internal class LoadMoveListDataUseCase(
    private val featureRepo: CoreFeatureRepo,
) {
    suspend fun invoke(
        gameId: String,
        characterId: String,
    ): Result<Pair<Character, List<Move>>, AppError> {
        val game = Game.fromId(id = gameId) ?: return Result.Error(AppError.GameNotFound(gameId))
        val wiki = featureRepo.getWikiClientFor(game) ?: return Result.Error(AppError.WikiClientNotFound(gameId))

        val result = coroutineScope {
            val deferredCharacter = async { wiki.fetchCharacter(characterQuery = characterId) }
            val deferredMoveList =  async { wiki.fetchMoveList(characterQuery = characterId) }

            deferredCharacter.await()
                .mapError { AppError.WikiError(it.inputs.joinToString(";")) }
                .flatMap { character ->
                    deferredMoveList.await()
                        .map { moveList -> character to moveList }
                        .mapError { AppError.WikiError(it.inputs.toString()) }
                }
        }

        return result
    }
}
