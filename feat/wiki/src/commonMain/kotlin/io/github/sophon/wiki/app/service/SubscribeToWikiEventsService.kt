package io.github.sophon.wiki.app.service

import io.github.sophon.wiki.SubscribeToWikiEventsUseCase
import io.github.sophon.wiki.model.WikiEvent
import kotlinx.coroutines.flow.Flow

internal class SubscribeToWikiEventsService(
    private val refreshDataService: RefreshDataService,
) : SubscribeToWikiEventsUseCase {
    override fun invoke(): Flow<WikiEvent> {
        val flow = refreshDataService.wikiEventFlow
        return flow
    }
}
