package io.github.sophon.fightingnerd.feat.home.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.mapError
import io.github.sophon.fightingnerd.core.model.AppError
import io.github.sophon.fightingnerd.feat.home.ui.HomeViewState
import io.github.sophon.fightingnerd.feat.module.ModuleRepo

internal class LoadGameCharacterListUseCase(
    private val moduleRepo: ModuleRepo,
) {
    suspend fun invoke(
        gameWidget: HomeViewState.WikiWidget,
    ): Result<HomeViewState.WikiWidget, AppError> {
        val wikiClient = moduleRepo.getWikiClientFor(gameWidget.game)
            ?: return Result.Error(AppError.WikiClientNotFound(gameWidget.game.id))

        val result = wikiClient.downloadCharacterList()
            .map { characterList ->
                val loadedWidget = gameWidget.copy(
                    characterList = characterList,
                    isLoading = false,
                )
                loadedWidget
            }
            .mapError { wikiError ->
                val appError = AppError.WikiError(wikiError.toString())
                appError
            }

        return result
    }
}
