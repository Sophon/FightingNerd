package io.github.sophon.fightingnerd.feat.move.usecase

import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.wiki.model.CharacterId
import io.github.sophon.fightingnerd.core.data.MediaRepo
import kotlinx.coroutines.flow.Flow

@ExcludeFromCoverage("pure repo call")
internal class SubscribeToOfflineMediaAvailability(
    private val mediaRepo: MediaRepo,
) {
    operator fun invoke(gameId: String): Flow<Set<CharacterId>> {
        val flow = mediaRepo.subscribeToCharsWithOfflineMedia(gameId)
        return flow
    }
}
