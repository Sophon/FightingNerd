package io.github.sophon.fightingnerd.feat.more.usecase

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.FeatureRepo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.RefreshEvent
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.fightingnerd.app.model.AppError

internal class ManualRefreshUseCase(
    private val featureRepo: FeatureRepo,
) {
    suspend fun refreshWiki(gameIdList: List<String>): EmptyResult<AppError> {
        val clientList = gameIdList
            .mapNotNull { Game.fromId(it) }
            .mapNotNull { featureRepo.getWikiClientFor(it) }
            .distinct()
        if (clientList.isEmpty()) return Result.Error(AppError.WikiClientNotFound(gameIdList.toString()))

        for (client in clientList) {
            val result = client.refresh()
            if (result is Result.Error) return result
        }
        return Result.Success(Unit)
    }

    suspend fun refreshGame(gameId: String): EmptyResult<AppError> {
        val game = Game.fromId(gameId)
            ?: return Result.Error(AppError.GameNotFound(gameId))
        val client = featureRepo.getWikiClientFor(game)
            ?: return Result.Error(AppError.WikiClientNotFound(gameId))
        val result = client.refresh()
        return result
    }

    private suspend fun WikiClient.refresh(): EmptyResult<AppError> {
        var failure: AppError? = null
        refreshData().collect { event ->
            if (event is RefreshEvent.Failed) {
                failure = AppError.WikiError(event.error.toString())
            }
        }
        val result = failure?.let { Result.Error(it) } ?: Result.Success(Unit)
        return result
    }
}
