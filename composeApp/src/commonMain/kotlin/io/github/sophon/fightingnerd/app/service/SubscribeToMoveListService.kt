package io.github.sophon.fightingnerd.app.service

import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.Character
import io.github.sophon.fightingnerd.app.model.Move
import io.github.sophon.fightingnerd.app.outPort.CharacterPort
import io.github.sophon.fightingnerd.app.outPort.MediaPort
import io.github.sophon.fightingnerd.app.outPort.MovePort
import io.github.sophon.fightingnerd.inPort.SubscribeToMoveListUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal class SubscribeToMoveListService(
    private val characterPort: CharacterPort,
    private val movePort: MovePort,
    private val mediaPort: MediaPort,
): SubscribeToMoveListUseCase {
    override fun invoke(
        gameId: String,
        characterId: String,
    ): Flow<Result<Pair<Character, List<Move>>, AppError>> {
        val flow = combine(
            characterPort.subscribeToCharacters(gameId),
            movePort.subscribeToMoves(gameId, characterId).preferDownloadedMedia(gameId, characterId),
        ) { characterList, moveList ->
            val character = characterList.firstOrNull { it.id == characterId }
            val result: Result<Pair<Character, List<Move>>, AppError> = if (character == null) {
                Result.Error(AppError.WikiError("$characterId not found"))
            } else {
                Result.Success(Pair(character, moveList))
            }
            result
        }.distinctUntilChanged()
        return flow
    }

    private fun Flow<List<Move>>.preferDownloadedMedia(
        gameId: String,
        characterId: String,
    ): Flow<List<Move>> {
        val flow = map { moveList ->
            val updatedMoveList = moveList.map { move ->
                val offlineUrls = mediaPort.toOfflineUrls(
                    gameId = gameId,
                    characterId = characterId,
                    urls = move.urls,
                )
                val updatedMove = move.copy(urls = offlineUrls)
                updatedMove
            }
            updatedMoveList
        }
        return flow
    }
}
