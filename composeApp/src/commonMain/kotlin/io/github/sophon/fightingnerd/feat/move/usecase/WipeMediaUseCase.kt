package io.github.sophon.fightingnerd.feat.move.usecase

import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.wiki.model.CharacterId
import io.github.sophon.fightingnerd.core.data.MediaRepo

@ExcludeFromCoverage("pure repo call")
internal class WipeMediaUseCase(
    private val mediaRepo: MediaRepo,
) {
    suspend fun invoke(gameId: String, characterId: CharacterId) {
        mediaRepo.wipe(gameId, characterId)
    }
}
