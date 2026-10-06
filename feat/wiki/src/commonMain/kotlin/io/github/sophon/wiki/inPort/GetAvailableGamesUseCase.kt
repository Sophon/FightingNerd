package io.github.sophon.wiki.inPort

import io.github.sophon.wiki.model.wiki.Game
import kotlinx.coroutines.flow.Flow

interface GetAvailableGamesUseCase {
    operator fun invoke(): Flow<Set<Game>>
}
