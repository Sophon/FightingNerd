package io.github.sophon.fightingnerd.feat.move.usecase

import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.FeatureRepo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Group
import io.github.sophon.fightingnerd.core.model.AppError

@ExcludeFromCoverage("plain client call")
internal class LoadMoveGroupsUseCase(
    private val repo: FeatureRepo,
) {
    operator fun invoke(gameId: String): Result<List<Group>, AppError> {
        val game = Game.fromId(gameId) ?: return Result.Error(AppError.GameNotFound(gameId))
        val wiki = repo.getWikiClientFor(game) ?: return Result.Error(AppError.WikiClientNotFound(gameId))

        val groups = wiki.getGroupsFor(game = game)
        return Result.Success(groups)
    }
}
