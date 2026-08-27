package io.github.sophon.fightingnerd.feat.move.usecase

import io.github.sophon.core.wiki.model.CharacterId
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.util.getMediaCount
import io.github.sophon.fightingnerd.core.data.MediaRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class DownloadMediaUseCase(
    private val mediaRepo: MediaRepo,
) {
    //should show errors and successful result
    fun invoke(
        gameId: String,
        characterId: CharacterId,
        moveList: List<Move>,
    ): Flow<Int> {
        return flow {
            var runningCount = 0
            moveList.forEach { move ->
                mediaRepo.save(
                    gameId = gameId,
                    characterId = characterId,
                    media = move.urls,
                )
                runningCount += move.getMediaCount()
                emit(runningCount)
            }
        }
    }
}
