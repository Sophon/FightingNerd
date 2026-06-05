package io.github.sophon.fightingnerd.feat.home.usecase

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.core.featureConfig.CoreFeatureRepo
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.fightingnerd.core.model.AppError
import io.github.sophon.fightingnerd.core.util.mapWikiError
import io.github.sophon.fightingnerd.feat.home.ui.HomeViewState

internal class LoadGameCharacterListUseCase(
    private val featureRepo: CoreFeatureRepo,
) {
    suspend fun invoke(
        gameWidget: HomeViewState.GameWidget,
    ): Result<HomeViewState.GameWidget, AppError> {
        val wikiClient = featureRepo.getWikiClientFor(gameWidget.game)
            ?: return Result.Error(AppError.WikiClientNotFound(gameWidget.game.id))

        val result = wikiClient.fetchCharacterList()
            .mapWikiError()
            .flatMap { cachedCharacterList ->
                if (cachedCharacterList.isEmpty()) {
                    refreshCharacterList(wikiClient)
                } else {
                    Result.Success(cachedCharacterList)
                }
            }
            .map { characterList ->
                gameWidget.updateWithCharacterList(characterList)
            }

        return result
    }

    private suspend fun refreshCharacterList(wikiClient: WikiClient): Result<List<Character>, AppError> {
        val result = wikiClient.downloadCharacterList()
            .mapWikiError()
            .flatMap { characterList ->
                wikiClient.cacheCharacterList(characterList)
                    .map { characterList }
                    .mapWikiError()
            }
        return result
    }

    private fun HomeViewState.GameWidget.updateWithCharacterList(characterList: List<Character>): HomeViewState.GameWidget {
        val updatedWidget = this.copy(
            characterList = characterList.map { domainCharacter ->
                HomeViewState.GameWidget.Character(
                    id = domainCharacter.id,
                    displayName = domainCharacter.displayName,
                    queryName = domainCharacter.remoteQueryId,
                    iconUrl = domainCharacter.images?.iconUrl,
                )
            },
            isLoading = false,
        )
        return updatedWidget
    }
}
