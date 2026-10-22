package io.github.sophon.fightingnerd.app.service

import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.fightingnerd.app.model.Wiki
import io.github.sophon.fightingnerd.app.outPort.AvailableWikisPort
import io.github.sophon.fightingnerd.inPort.SubscribeToWikisUseCase
import kotlinx.coroutines.flow.Flow

@ExcludeFromCoverage("plain port call")
internal class SubscribeToWikisService(
    private val availableWikisPort: AvailableWikisPort,
): SubscribeToWikisUseCase {
    override fun invoke(): Flow<Set<Wiki>> {
        return availableWikisPort.subscribeToWikis()
    }
}
