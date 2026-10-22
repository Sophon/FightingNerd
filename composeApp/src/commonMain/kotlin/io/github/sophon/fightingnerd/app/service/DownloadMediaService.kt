package io.github.sophon.fightingnerd.app.service

import io.github.sophon.fightingnerd.app.model.Move
import io.github.sophon.fightingnerd.app.outPort.MediaPort
import io.github.sophon.fightingnerd.inPort.DownloadMediaUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class DownloadMediaService(
    private val mediaPort: MediaPort,
): DownloadMediaUseCase {
    override fun invoke(
        gameId: String,
        characterId: String,
        moveList: List<Move>,
    ): Flow<Int> {
        val flow = flow {
            var runningCount = 0
            moveList.forEach { move ->
                mediaPort.save(
                    gameId = gameId,
                    characterId = characterId,
                    urls = move.urls,
                )
                runningCount += move.urls.mediaCount
                emit(runningCount)
            }
        }
        return flow
    }
}
