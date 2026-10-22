package io.github.sophon.wiki.app.service

import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.wiki.SubscribeToWikiEventsUseCase
import io.github.sophon.wiki.app.outPort.SubscribeToWikiEventsPort
import io.github.sophon.wiki.model.WikiEvent
import kotlinx.coroutines.flow.Flow

@ExcludeFromCoverage("plain port call")
internal class SubscribeToWikiEventsService(
    private val subscribeToWikiEventsPort: SubscribeToWikiEventsPort,
) : SubscribeToWikiEventsUseCase {
    override fun invoke(): Flow<WikiEvent> {
        val flow = subscribeToWikiEventsPort.subscribe()
        return flow
    }
}
