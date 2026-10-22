package io.github.sophon.wiki.app.service

import io.github.sophon.wiki.GetUpdateTimeStampUseCase
import io.github.sophon.wiki.app.outPort.LoadLastUpdatePort
import io.github.sophon.wiki.model.wiki.Game
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

internal class GetUpdateTimeStampService(
    private val loadLastUpdatePort: LoadLastUpdatePort,
) : GetUpdateTimeStampUseCase {
    override fun invoke(game: Game): Flow<Instant?> = loadLastUpdatePort.subscribe(game)
}
