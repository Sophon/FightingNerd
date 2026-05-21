package io.github.sophon.fightingnerd.feat.home.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.fightingnerd.core.model.AppError
import io.github.sophon.fightingnerd.feat.home.ui.HomeViewState
import io.github.sophon.fightingnerd.feat.module.ModuleRepo

internal class LoadEmptyWidgetsUseCase(
    private val moduleRepo: ModuleRepo,
) {
    fun invoke(): Result<List<HomeViewState.WikiWidget>, AppError> {
        val gameClientMap = moduleRepo.getGameClients()
        val widgetList = gameClientMap.map { (game, wikiClient) ->
            val widget = HomeViewState.WikiWidget(
                game = game,
                featureInfo = wikiClient.getFeatureInfo(),
                isLoading = true,
            )
            widget
        }
        return Result.Success(widgetList)
    }
}
