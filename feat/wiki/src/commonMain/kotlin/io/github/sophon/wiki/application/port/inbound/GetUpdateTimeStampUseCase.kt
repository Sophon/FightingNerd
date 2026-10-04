package io.github.sophon.wiki.application.port.inbound

import io.github.sophon.wiki.application.domain.model.wiki.Game
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

interface GetUpdateTimeStampUseCase {
    operator fun invoke(game: Game): Flow<Instant?>
}
