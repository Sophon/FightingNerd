package io.github.sophon.fightingnerd.feat.home.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.feature.module.CoreFeatureRepo
import io.github.sophon.fightingnerd.core.model.AppError
import io.github.sophon.fightingnerd.feat.home.ui.HomeViewState

internal class LoadEmptyWidgetsUseCase(
    private val featureRepo: CoreFeatureRepo,
) {
    fun invoke(): Result<List<HomeViewState.GameWidget>, AppError> {
        val gameClientMap = featureRepo.getGameClients()
        val widgetList = gameClientMap.map { (game, wikiClient) ->
            val widget = HomeViewState.GameWidget(
                game = game,
                featureName = wikiClient.getFeatureInfo().name,
                isLoading = true,
            )
            widget
        }
        return Result.Success(widgetList)
    }
}
