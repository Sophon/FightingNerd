package io.github.sophon.wiki.inPort

import io.github.sophon.wiki.model.wiki.Game
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

interface GetUpdateTimeStampUseCase {
    operator fun invoke(game: Game): Flow<Instant?>
}
