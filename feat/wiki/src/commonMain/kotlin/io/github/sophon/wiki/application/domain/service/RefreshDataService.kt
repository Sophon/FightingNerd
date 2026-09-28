package io.github.sophon.wiki.application.domain.service

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wiki.application.domain.model.RefreshEvent
import io.github.sophon.wiki.application.domain.model.WikiError
import io.github.sophon.wiki.application.domain.model.toWikiError
import io.github.sophon.wiki.application.domain.util.normalizeDustLoop
import io.github.sophon.wiki.application.domain.util.normalizeMizuumi
import io.github.sophon.wiki.application.domain.util.normalizeSuperCombo
import io.github.sophon.wiki.application.domain.util.normalizeT8
import io.github.sophon.wiki.application.domain.util.normalizeXko
import io.github.sophon.wiki.application.port.inbound.RefreshDataUseCase
import io.github.sophon.wiki.application.port.outbound.FetchGameDataPort
import io.github.sophon.wiki.application.port.outbound.LoadWikiConfigPort
import io.github.sophon.wiki.application.port.outbound.SaveCharacterMoveListPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class RefreshDataService(
    private val loadWikiConfigPort: LoadWikiConfigPort,
    private val fetchGameDataPort: FetchGameDataPort,
    private val saveCharacterMoveListPort: SaveCharacterMoveListPort,
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
                    fetchGameDataPort.fetch(game).collect { characterWithMovesResult ->
                        characterWithMovesResult
                            .onSuccess { (character, moveList) ->
                                Napier.d(tag = TAG) { "${character.id} (${game.id}): ${moveList.size} moves downloaded" }
                            }
                            .onError { error ->
                                Napier.w(tag = TAG) { "${game.id}: download failed - $error" }
                            }
                            .mapError { error -> error.toWikiError() }
                            .flatMap { (character, moveList) ->
                                saveCharacterMoveList(game, character, moveList.normalize(game))
                            }
                            .onSuccess { successCount++ }
                            .onError { error -> emit(RefreshEvent.Failed(error)) }
                    }
                }

                emit(RefreshEvent.Finished(successCount))
            }
        }

        return flow
    }

    private suspend fun saveCharacterMoveList(
        game: Game,
        character: Character,
        moveList: List<Move>,
    ): EmptyResult<WikiError> {
        val saveResult = saveCharacterMoveListPort.save(game, character, moveList)
            .mapError { error -> error.toWikiError() }
            .onError { error ->
                Napier.w(tag = TAG) { "${character.id} (${game.id}): save failed - $error" }
            }

        return saveResult
    }

    private fun List<Move>.normalize(
        game: Game,
    ): List<Move> {
        val normalizedList = when (game) {
            Game.Tekken8 -> this.map { move -> move.normalizeT8() }
            Game.MBTL, Game.Uni2, Game.VSAV -> this.map { move -> move.normalizeMizuumi() }
            Game.GGST, Game.DBFZ, Game.GBVSR, Game.BBCF, Game.MTFS -> this.map { move -> move.normalizeDustLoop(game) }
            Game.StreetFighter6, Game.MK1, Game.AVL -> this.map { move -> move.normalizeSuperCombo() }
            Game.Xko -> this.map { move -> move.normalizeXko() }
            else -> this
        }
        return normalizedList
    }


    private companion object {
        const val TAG = "RefreshDataService"
    }
}
