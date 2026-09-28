package io.github.sophon.wiki.application.domain.service

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.asEmptyDataResult
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.wiki.application.domain.model.RefreshEvent
import io.github.sophon.wiki.application.domain.model.WikiError
import io.github.sophon.wiki.application.domain.model.toWikiError
import io.github.sophon.wiki.application.port.inbound.RefreshDataUseCase
import io.github.sophon.wiki.application.port.outbound.FetchCharacterListPort
import io.github.sophon.wiki.application.port.outbound.FetchMoveListPort
import io.github.sophon.wiki.application.port.outbound.LoadWikiConfigPort
import io.github.sophon.wiki.application.port.outbound.SaveCharacterListPort
import io.github.sophon.wiki.application.port.outbound.SaveMoveListPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class RefreshDataService(
    private val loadWikiConfigPort: LoadWikiConfigPort,
    private val fetchCharacterListPort: FetchCharacterListPort,
    private val saveCharacterListPort: SaveCharacterListPort,
    private val fetchMoveListPort: FetchMoveListPort,
    private val saveMoveListPort: SaveMoveListPort,
) : RefreshDataUseCase {
    private val refreshMutex = Mutex()

    override fun invoke(): Flow<RefreshEvent> {
        val flow = flow {
            refreshMutex.withLock {
                val gameSet = loadWikiConfigPort
                    .subscribe()
                    .filterNotNull()
                    .first()
                    .enabledGameSet

                var successCount = 0
                for (game in gameSet) {
                    refreshCharacterList(game)
                        .onSuccess { characterList ->
                            for (character in characterList) {
                                refreshMoveList(game, character)
                                    .onSuccess { successCount++ }
                                    .onError { error -> emit(RefreshEvent.Failed(error)) }
                            }
                        }
                        .onError { error -> emit(RefreshEvent.Failed(error)) }
                }

                emit(RefreshEvent.Finished(successCount))
            }
        }

        return flow
    }

    private suspend fun refreshCharacterList(game: Game): Result<List<Character>, WikiError> {
        val characterListResult = fetchCharacterListPort.fetch(game)
            .mapError { error -> error.toWikiError() }
            .flatMap { characterList ->
                saveCharacterListPort.save(game, characterList)
                    .map { characterList }
                    .mapError { error -> error.toWikiError() }
            }
            .onSuccess { characterList ->
                Napier.i(tag = TAG) { "${game.id}: ${characterList.size} characters downloaded" }
            }
            .onError { error ->
                Napier.e(tag = TAG) { "${game.id}: character refresh failed - $error" }
            }

        return characterListResult
    }

    private suspend fun refreshMoveList(
        game: Game,
        character: Character,
    ): EmptyResult<WikiError> {
        val moveListResult = fetchMoveListPort.fetch(game, character)
            .mapError { error -> error.toWikiError() }
            .flatMap { moveList ->
                saveMoveListPort.save(game, character, moveList)
                    .map { moveList }
                    .mapError { error -> error.toWikiError() }
            }
            .onSuccess { moveList ->
                Napier.d(tag = TAG) { "${character.id} (${game.id}): ${moveList.size} moves downloaded" }
            }
            .onError { error ->
                Napier.w(tag = TAG) { "${character.id} (${game.id}): move refresh failed - $error" }
            }
            .asEmptyDataResult()

        return moveListResult
    }


    private companion object {
        const val TAG = "RefreshDataService"
    }
}
