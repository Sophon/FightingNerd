package io.github.sophon.fightingnerd.feat.move.usecase

import io.github.sophon.core.wiki.model.CharacterId
import io.github.sophon.fightingnerd.core.data.MediaRepo
import kotlinx.coroutines.flow.Flow

internal class SubscribeToOfflineCharsUseCase(
    private val mediaRepo: MediaRepo,
) {
    fun invoke(gameId: String): Flow<Set<CharacterId>> {
        val flow = mediaRepo.subscribeToCharsWithOfflineMedia(gameId)
        return flow
    }
}
