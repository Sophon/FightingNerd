package io.github.sophon.wiki.application.domain.service

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.wiki.application.domain.model.WikiConfig
import io.github.sophon.wiki.application.domain.model.WikiError
import io.github.sophon.wiki.application.domain.model.toWikiError
import io.github.sophon.wiki.application.port.inbound.ConfigureWikiUseCase
import io.github.sophon.wiki.application.port.outbound.DeleteCharacterListPort
import io.github.sophon.wiki.application.port.outbound.DeleteMoveListPort
import io.github.sophon.wiki.application.port.outbound.LoadWikiConfigPort
import io.github.sophon.wiki.application.port.outbound.SaveWikiConfigPort
import kotlinx.coroutines.flow.first

internal class ConfigureWikiService(
    private val loadWikiConfigPort: LoadWikiConfigPort,
    private val saveWikiConfigPort: SaveWikiConfigPort,
    private val deleteMoveListPort: DeleteMoveListPort,
    private val deleteCharacterListPort: DeleteCharacterListPort,
) : ConfigureWikiUseCase {
    override suspend fun invoke(wikiConfig: WikiConfig): EmptyResult<WikiError> {
        val previousConfig = loadWikiConfigPort.subscribe().first()
        val disabledGameSet = previousConfig?.enabledGameSet.orEmpty() - wikiConfig.enabledGameSet

        val result = saveWikiConfigPort.save(wikiConfig)
            .flatMap { deleteDataFor(disabledGameSet) }
            .onSuccess {
                val enabledGames = wikiConfig.enabledGameSet.map { it.id }
                Napier.i(tag = TAG) { "${enabledGames.size}: $enabledGames" }
            }
            .onError { error ->
                Napier.e(tag = TAG) { error.toString() }
            }

        return result
    }

    private suspend fun deleteDataFor(gameSet: Set<Game>): EmptyResult<WikiError> {
        val resultList = gameSet.map { game -> deleteDataFor(game) }
        val result = resultList.firstOrNull { it is Result.Error } ?: Result.Success(Unit)
        return result
    }

    private suspend fun deleteDataFor(game: Game): EmptyResult<WikiError> {
        val result = deleteMoveListPort.delete(game)
            .flatMap { deleteCharacterListPort.delete(game) }
            .mapError { error -> error.toWikiError() }
            .onSuccess {
                Napier.i(tag = TAG) { "Deleted data for ${game.id}" }
            }
            .onError { error ->
                Napier.w(tag = TAG) { "Failed to delete data for ${game.id}: $error" }
            }

        return result
    }


    private companion object {
        const val TAG = "ConfigureWikiService"
    }
}
