package io.github.sophon.fightingnerd.feat.home.usecase

import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.feature.Game
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.core.wiki.usecase.DownloadMoveListUseCase
import io.github.sophon.fightingnerd.core.model.AppError
import io.github.sophon.fightingnerd.feat.module.ModuleRepo

internal class LoadMoveListUseCase(
    private val moduleRepo: ModuleRepo,
) {
    suspend fun invoke(
        game: Game,
        characterQueryId: String,
    ): Result<List<Move>, AppError> {
        val wiki = moduleRepo.getWikiClientFor(game)
            ?: return Result.Error(AppError.WikiClientNotFound(game.id))
        val characterData = DownloadMoveListUseCase.CharacterData(name = characterQueryId, imageUrl = null)
        val result = wiki.downloadMoveList(characterData)
            .mapError {
                Napier.e(tag = TAG) { it.toString() }
                AppError.WikiError(it.toString()) //TODO: map properly
            }

        return result
    }


    private companion object {
        const val TAG = "LoadMoveListUseCase"
    }
}