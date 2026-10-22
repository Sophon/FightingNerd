package io.github.sophon.fightingnerd.app.service

import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.fightingnerd.app.outPort.MediaPort
import io.github.sophon.fightingnerd.inPort.SubscribeToOfflineMediaUseCase
import kotlinx.coroutines.flow.Flow

@ExcludeFromCoverage("plain port call")
internal class SubscribeToOfflineMediaService(
    private val mediaPort: MediaPort,
): SubscribeToOfflineMediaUseCase {
    override fun invoke(gameId: String): Flow<Set<String>> {
        val flow = mediaPort.subscribeToCharactersWithOfflineMedia(gameId)
        return flow
    }
}
