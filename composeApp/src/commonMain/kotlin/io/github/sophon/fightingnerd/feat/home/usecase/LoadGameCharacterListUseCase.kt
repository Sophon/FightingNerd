package io.github.sophon.fightingnerd.feat.home.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.flatMap
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.fightingnerd.core.model.AppError
import io.github.sophon.fightingnerd.feat.home.ui.HomeViewState
import io.github.sophon.fightingnerd.feat.module.ModuleRepo

internal class LoadGameCharacterListUseCase(
    private val moduleRepo: ModuleRepo,
) {
    suspend fun invoke(
        gameWidget: HomeViewState.GameWidget,
    ): Result<HomeViewState.GameWidget, AppError> {
        val wikiClient = moduleRepo.getWikiClientFor(gameWidget.game)
            ?: return Result.Error(AppError.WikiClientNotFound(gameWidget.game.id))

        val result = getCachedCharacterList(wikiClient).flatMap { cachedCharacterList ->
            if (cachedCharacterList.isEmpty()) {
                downloadCharacterList(wikiClient).flatMap { downloadedCharacterList ->
                    wikiClient.cacheCharacterList(downloadedCharacterList)
                        .flatMap {
                            val loadedWidget = gameWidget.updateWithMoveList(downloadedCharacterList)
                            Result.Success(loadedWidget)
                        }
                        .mapError { wikiError ->
                            val appError = AppError.WikiError(wikiError.toString())
                            appError
                        }
                }
            } else {
                val loadedWidget = gameWidget.updateWithMoveList(cachedCharacterList)
                Result.Success(loadedWidget)
            }
        }

        return result
    }


    private suspend fun getCachedCharacterList(wikiClient: WikiClient): Result<List<Character>, AppError> {
        val result = wikiClient.fetchCharacterList()
            .mapError { wikiError ->
                val appError = AppError.WikiError(wikiError.toString())
                appError
            }
        return result
    }

    private suspend fun downloadCharacterList(wikiClient: WikiClient): Result<List<Character>, AppError> {
        val result = wikiClient.downloadCharacterList()
            .mapError { wikiError ->
                val appError = AppError.WikiError(wikiError.toString())
                appError
            }
        return result
    }

    private fun HomeViewState.GameWidget.updateWithMoveList(moveList: List<Character>): HomeViewState.GameWidget {
        val updatedWidget = this.copy(
            characterList = moveList.map { domainCharacter ->
                HomeViewState.GameWidget.Character(
                    id = domainCharacter.id,
                    displayName = domainCharacter.displayName,
                    queryName = domainCharacter.queryName,
                    iconUrl = domainCharacter.images?.iconUrl,
                )
            },
            isLoading = false,
        )
        return updatedWidget
    }
}
