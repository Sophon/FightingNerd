package io.github.sophon.fightingnerd.feat.move.usecase

import io.github.sophon.fightingnerd.core.data.MediaRepo

internal class WipeMediaUseCase(
    private val mediaRepo: MediaRepo,
) {
    //TODO: we should do it per character, not per game
    suspend fun invoke(gameId: String) {
        mediaRepo.wipe(gameId)
    }
}