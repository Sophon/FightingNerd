package io.github.sophon.wiki.application.domain.service

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.wiki.application.port.inbound.GetUpdateTimeStampUseCase
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

internal class GetUpdateTimeStampService : GetUpdateTimeStampUseCase {
    override fun invoke(game: Game): Flow<Instant?> {
        TODO("Not yet implemented")
    }
}
