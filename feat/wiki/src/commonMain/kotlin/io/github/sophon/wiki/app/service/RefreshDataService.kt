package io.github.sophon.wiki.app.service

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.wiki.RefreshDataUseCase
import io.github.sophon.wiki.app.model.toWikiError
import io.github.sophon.wiki.app.outPort.FetchGameDataPort
import io.github.sophon.wiki.app.outPort.LoadWikiConfigPort
import io.github.sophon.wiki.app.outPort.SaveCharacterListPort
import io.github.sophon.wiki.app.outPort.SaveGameDataPort
import io.github.sophon.wiki.app.outPort.SaveMoveListPort
import io.github.sophon.wiki.app.outPort.StrikeCharacterListPort
import io.github.sophon.wiki.app.util.normalize
import io.github.sophon.wiki.app.util.normalizeDreamCancel
import io.github.sophon.wiki.app.util.normalizeDustLoop
import io.github.sophon.wiki.app.util.normalizeMizuumi
import io.github.sophon.wiki.app.util.normalizeSuperCombo
import io.github.sophon.wiki.app.util.normalizeT8
import io.github.sophon.wiki.app.util.normalizeXko
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.Move
import io.github.sophon.wiki.model.RefreshEvent
import io.github.sophon.wiki.model.WikiError
import io.github.sophon.wiki.model.wiki.Game
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class RefreshDataService(
    private val loadWikiConfigPort: LoadWikiConfigPort,
    private val fetchGameDataPort: FetchGameDataPort,
    private val saveCharacterListPort: SaveCharacterListPort,
    private val saveMoveListPort: SaveMoveListPort,
    private val saveGameDataPort: SaveGameDataPort,
    private val strikeCharacterListPort: StrikeCharacterListPort,
) : RefreshDataUseCase {
    private val refreshMutex = Mutex()

    override fun invoke(): Flow<RefreshEvent> {
        val flow = refresh { enabledGameSet -> enabledGameSet }
        return flow
    }

    override fun invoke(gameSet: Set<Game>): Flow<RefreshEvent> {
        val flow = refresh { enabledGameSet -> (enabledGameSet intersect gameSet) }
        return flow
    }

    private fun refresh(selectGameSet: (Set<Game>) -> Set<Game>): Flow<RefreshEvent> {
        val flow = flow {
            refreshMutex.withLock {
                val enabledGameSet = loadWikiConfigPort
                    .subscribe()
                    .filterNotNull()
                    .first()
                    .enabledGameSet
                val gameSet = selectGameSet(enabledGameSet)

                var successCount = 0
                for (game in gameSet) {
                    successCount += if (game.separateCharMoveDownload) {
                        refreshSeparate(game)
                    } else {
                        refreshBulk(game)
                    }
                }

                emit(RefreshEvent.Finished(successCount))
            }
        }

        return flow
    }

    /**
     * Character list download, then move list download per character.
     *
     * Returns how many characters got their move list saved.
     */
    private suspend fun FlowCollector<RefreshEvent>.refreshSeparate(game: Game): Int {
        var successCount = 0
        fetchGameDataPort.fetchCharacterList(game)
            .onError { error ->
                Napier.w(tag = TAG) { "${game.id}: character list download failed - $error" }
            }
            .mapError { error -> error.toWikiError() }
            .map { characterList -> characterList.map { character -> character.normalize() } }
            .flatMap { characterList -> saveCharacterList(game, characterList).map { characterList } }
            .onSuccess { characterList ->
                Napier.i(tag = TAG) { "${game.id}: ${characterList.size} characters downloaded" }

                // nothing downloaded - the wiki failed, not its characters
                if (characterList.isNotEmpty()) {
                    val downloadedIdSet = characterList.map { character -> character.id }.toSet()
                    strikeAbsentCharacters(game, downloadedIdSet)
                        .onError { error -> emit(RefreshEvent.Failed(error)) }
                }

                for (character in characterList) {
                    fetchGameDataPort.fetchMoveList(character)
                        .onSuccess { moveList ->
                            Napier.d(tag = TAG) { "${character.id.naturalId} (${game.id}): ${moveList.size} moves downloaded" }
                        }
                        .onError { error ->
                            Napier.w(tag = TAG) { "${character.id.naturalId} (${game.id}): move list download failed - $error" }
                        }
                        .mapError { error -> error.toWikiError() }
                        .flatMap { moveList ->
                            val normalizedMoveList = moveList
                                .normalize(character.id)
                                .dropDuplicateInputs(character.id)
                            saveMoveList(character.id, normalizedMoveList)
                        }
                        .onSuccess { successCount++ }
                        .onError { error -> emit(RefreshEvent.Failed(error)) }
                }
            }
            .onError { error -> emit(RefreshEvent.Failed(error)) }

        return successCount
    }

    /**
     * Whole data set downloaded at once.
     *
     * Returns how many characters got their move list saved.
     */
    private suspend fun FlowCollector<RefreshEvent>.refreshBulk(game: Game): Int {
        var successCount = 0
        fetchGameDataPort.fetchGameData(game)
            .onError { error ->
                Napier.w(tag = TAG) { "${game.id}: download failed - $error" }
            }
            .mapError { error -> error.toWikiError() }
            .map { gameData -> gameData.map { characterWithMoves -> characterWithMoves.normalize() } }
            .flatMap { gameData -> saveGameData(game, gameData).map { gameData } }
            .onSuccess { gameData ->
                Napier.i(tag = TAG) { "${game.id}: ${gameData.size} characters downloaded" }
                successCount = gameData.size

                // nothing downloaded - the wiki failed, not its characters
                if (gameData.isNotEmpty()) {
                    val downloadedIdSet = gameData.map { (character, _) -> character.id }.toSet()
                    strikeAbsentCharacters(game, downloadedIdSet)
                        .onError { error -> emit(RefreshEvent.Failed(error)) }
                }
            }
            .onError { error -> emit(RefreshEvent.Failed(error)) }

        return successCount
    }

    private suspend fun saveCharacterList(
        game: Game,
        characterList: List<Character>,
    ): EmptyResult<WikiError> {
        val saveResult = saveCharacterListPort.saveCharacterList(characterList)
            .mapError { error -> error.toWikiError() }
            .onError { error ->
                Napier.w(tag = TAG) { "${game.id}: character list save failed - $error" }
            }

        return saveResult
    }

    private suspend fun saveMoveList(
        characterId: CharacterId,
        moveList: List<Move>,
    ): EmptyResult<WikiError> {
        val saveResult = saveMoveListPort.saveMoveList(characterId, moveList)
            .mapError { error -> error.toWikiError() }
            .onError { error ->
                Napier.w(tag = TAG) { "${characterId.naturalId} (${characterId.game.id}): move list save failed - $error" }
            }

        return saveResult
    }

    private suspend fun saveGameData(
        game: Game,
        gameData: List<Pair<Character, List<Move>>>,
    ): EmptyResult<WikiError> {
        val saveResult = saveGameDataPort.saveGameData(gameData)
            .mapError { error -> error.toWikiError() }
            .onError { error ->
                Napier.w(tag = TAG) { "${game.id}: save failed - $error" }
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

    private fun Pair<Character, List<Move>>.normalize(): Pair<Character, List<Move>> {
        val (character, moveList) = this
        val normalizedCharacter = character.normalize()
        val normalizedMoveList = moveList
            .normalize(normalizedCharacter.id)
            .dropDuplicateInputs(normalizedCharacter.id)
        val normalized = (normalizedCharacter to normalizedMoveList)
        return normalized
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
