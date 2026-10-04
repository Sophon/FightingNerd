package io.github.sophon.wiki.application.domain.service

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.wiki.application.domain.model.Character
import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.Move
import io.github.sophon.wiki.application.domain.model.RefreshEvent
import io.github.sophon.wiki.application.domain.model.WikiError
import io.github.sophon.wiki.application.domain.model.toWikiError
import io.github.sophon.wiki.application.domain.model.wiki.Game
import io.github.sophon.wiki.application.domain.util.normalize
import io.github.sophon.wiki.application.domain.util.normalizeDreamCancel
import io.github.sophon.wiki.application.domain.util.normalizeDustLoop
import io.github.sophon.wiki.application.domain.util.normalizeMizuumi
import io.github.sophon.wiki.application.domain.util.normalizeSuperCombo
import io.github.sophon.wiki.application.domain.util.normalizeT8
import io.github.sophon.wiki.application.domain.util.normalizeXko
import io.github.sophon.wiki.application.port.inbound.RefreshDataUseCase
import io.github.sophon.wiki.application.port.outbound.FetchGameDataPort
import io.github.sophon.wiki.application.port.outbound.LoadWikiConfigPort
import io.github.sophon.wiki.application.port.outbound.SaveCharacterMoveListPort
import io.github.sophon.wiki.application.port.outbound.StrikeCharacterListPort
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
    private val strikeCharacterListPort: StrikeCharacterListPort,
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
                    var downloadCount = 0
                    val downloadedIdSet = mutableSetOf<CharacterId>()
                    fetchGameDataPort.fetch(game).collect { characterWithMovesResult ->
                        characterWithMovesResult
                            .onSuccess { (character, moveList) ->
                                downloadCount++
                                Napier.d(tag = TAG) { "${character.id.naturalId} (${game.id}): ${moveList.size} moves downloaded" }
                            }
                            .onError { error ->
                                Napier.w(tag = TAG) { "${game.id}: download failed - $error" }
                            }
                            .mapError { error -> error.toWikiError() }
                            .flatMap { (character, moveList) ->
                                val normalizedCharacter = character.normalize()
                                val normalizedMoveList = moveList
                                    .normalize(normalizedCharacter.id)
                                    .dropDuplicateInputs(normalizedCharacter.id)
                                downloadedIdSet.add(normalizedCharacter.id)
                                saveCharacterMoveList(normalizedCharacter, normalizedMoveList)
                            }
                            .onSuccess { successCount++ }
                            .onError { error -> emit(RefreshEvent.Failed(error)) }
                    }

                    Napier.i(tag = TAG) { "${game.id}: $downloadCount characters downloaded" }

                    // nothing downloaded - the wiki failed, not its characters
                    if (downloadedIdSet.isNotEmpty()) {
                        strikeAbsentCharacters(game, downloadedIdSet)
                            .onError { error -> emit(RefreshEvent.Failed(error)) }
                    }
                }

                emit(RefreshEvent.Finished(successCount))
            }
        }

        return flow
    }

    private suspend fun saveCharacterMoveList(
        character: Character,
        moveList: List<Move>,
    ): EmptyResult<WikiError> {
        val saveResult = saveCharacterMoveListPort.save(character, moveList)
            .mapError { error -> error.toWikiError() }
            .onError { error ->
                Napier.w(tag = TAG) { "${character.id.naturalId} (${character.id.game.id}): save failed - $error" }
            }

        return saveResult
    }

    private suspend fun strikeAbsentCharacters(
        game: Game,
        downloadedIdSet: Set<CharacterId>,
    ): EmptyResult<WikiError> {
        val strikeResult = strikeCharacterListPort.strike(game, downloadedIdSet)
            .mapError { error -> error.toWikiError() }
            .onError { error ->
                Napier.w(tag = TAG) { "${game.id}: character strikes failed - $error" }
            }

        return strikeResult
    }

    /**
     * The first move in wiki order keeps the input; the rest are wiki errors or true duplicates.
     */
    private fun List<Move>.dropDuplicateInputs(characterId: CharacterId): List<Move> {
        val moveListByInput = this.groupBy { move -> move.input }
        val uniqueMoveList = moveListByInput.values.map { sameInputList -> sameInputList.first() }
        val droppedMoveList = moveListByInput.values.flatMap { sameInputList -> sameInputList.drop(1) }

        if (droppedMoveList.isNotEmpty()) {
            val droppedIdList = droppedMoveList.map { move -> move.remoteId ?: move.input }
            Napier.w(tag = TAG) { "${characterId.naturalId} (${characterId.game.id}): duplicate inputs dropped - $droppedIdList" }
        }
        return uniqueMoveList
    }

    private fun List<Move>.normalize(characterId: CharacterId): List<Move> {
        val normalizedList = when (characterId.game) {
            Game.Tekken8 -> this.map { move -> move.normalizeT8() }
            Game.MBTL, Game.Uni2, Game.VSAV -> this.map { move -> move.normalizeMizuumi() }
            Game.GGST, Game.DBFZ, Game.GBVSR, Game.BBCF, Game.MTFS -> this.map { move -> move.normalizeDustLoop(characterId) }
            Game.StreetFighter6, Game.MK1, Game.AVL -> this.map { move -> move.normalizeSuperCombo() }
            Game.Xko -> this.map { move -> move.normalizeXko() }
            Game.KoFXV, Game.COTW -> this.map { move -> move.normalizeDreamCancel() }
            else -> this
        }
        return normalizedList
    }


    private companion object {
        const val TAG = "RefreshDataService"
    }
}
