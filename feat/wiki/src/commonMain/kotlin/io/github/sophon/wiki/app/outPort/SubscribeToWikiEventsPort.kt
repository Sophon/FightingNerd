package io.github.sophon.wiki.app.outPort

import io.github.sophon.wiki.model.WikiEvent
import kotlinx.coroutines.flow.Flow

internal interface SubscribeToWikiEventsPort {
    fun subscribe(): Flow<WikiEvent>
}
