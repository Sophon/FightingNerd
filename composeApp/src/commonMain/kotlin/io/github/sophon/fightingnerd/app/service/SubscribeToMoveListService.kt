package io.github.sophon.fightingnerd.app.service

import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.game.Move
import io.github.sophon.fightingnerd.app.model.game.MoveListData
import io.github.sophon.fightingnerd.app.outPort.AvailableGamesPort
import io.github.sophon.fightingnerd.app.outPort.CharacterPort
import io.github.sophon.fightingnerd.app.outPort.MediaPort
import io.github.sophon.fightingnerd.app.outPort.MovePort
import io.github.sophon.fightingnerd.inPort.SubscribeToMoveListUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal class SubscribeToMoveListService(
    private val availableGamesPort: AvailableGamesPort,
    private val characterPort: CharacterPort,
    private val movePort: MovePort,
    private val mediaPort: MediaPort,
): SubscribeToMoveListUseCase {
    override fun invoke(
        gameId: String,
        characterId: String,
    ): Flow<Result<MoveListData, AppError>> {
        val flow = combine(
            availableGamesPort.subscribe(),
            characterPort.subscribeToCharacters(gameId),
            movePort.subscribeToMoves(gameId, characterId).preferDownloadedMedia(gameId, characterId),
        ) { gameSet, characterList, moveList ->
            val game = gameSet.firstOrNull { it.id == gameId }
            val character = characterList.firstOrNull { it.id == characterId }
            val result: Result<MoveListData, AppError> = when {
                game == null -> Result.Error(AppError.WikiError("$gameId not found"))
                character == null -> Result.Error(AppError.WikiError("$characterId not found"))
                else -> Result.Success(MoveListData(character = character, moveList = moveList, game = game))
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
