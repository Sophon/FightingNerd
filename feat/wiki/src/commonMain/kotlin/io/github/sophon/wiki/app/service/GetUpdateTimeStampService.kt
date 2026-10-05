package io.github.sophon.wiki.app.service

import io.github.sophon.wiki.model.wiki.Game
import io.github.sophon.wiki.inPort.GetUpdateTimeStampUseCase
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

internal class GetUpdateTimeStampService : GetUpdateTimeStampUseCase {
    override fun invoke(game: Game): Flow<Instant?> {
        TODO("Not yet implemented")
    }
}
