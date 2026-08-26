package io.github.sophon.fightingnerd.feat.move.usecase

import io.github.sophon.core.wiki.model.Move
import io.github.sophon.fightingnerd.core.data.MediaRepo

internal class DownloadMediaUseCase(
    private val mediaRepo: MediaRepo,
) {
    //should show errors and successful result
    suspend fun invoke(gameId: String, moveList: List<Move>) {
        moveList.forEach { move ->
            mediaRepo.save(
                gameId = gameId,
                media = move.urls,
            )
        }
    }
}