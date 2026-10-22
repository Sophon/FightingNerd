package io.github.sophon.wiki.adapter.outbound.memory

import io.github.sophon.wiki.app.outPort.PublishWikiEventPort
import io.github.sophon.wiki.app.outPort.SubscribeToWikiEventsPort
import io.github.sophon.wiki.model.WikiEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

internal class InMemoryWikiEventAdapter : PublishWikiEventPort, SubscribeToWikiEventsPort {
    private val wikiEventFlow = MutableSharedFlow<WikiEvent>(extraBufferCapacity = EVENT_BUFFER_CAPACITY)

    override suspend fun publish(event: WikiEvent) {
        wikiEventFlow.emit(event)
    }

    override fun subscribe(): Flow<WikiEvent> {
        val flow = wikiEventFlow.asSharedFlow()
        return flow
    }
}


private const val EVENT_BUFFER_CAPACITY = 64
