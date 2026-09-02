package io.github.sophon.fightingnerd.feat.move.usecase

import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.FeatureRepo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.fightingnerd.core.model.AppError

@ExcludeFromCoverage("plain client call")
internal class LoadMoveFiltersUseCase(
    private val repo: FeatureRepo,
) {
    operator fun invoke(gameId: String): Result<Set<Filter>, AppError> {
        val game = Game.fromId(gameId) ?: return Result.Error(AppError.GameNotFound(gameId))
        val wiki = repo.getWikiClientFor(game) ?: return Result.Error(AppError.WikiClientNotFound(gameId))

        val filters = wiki.getFiltersFor(game)
        return Result.Success(filters)
    }
}
