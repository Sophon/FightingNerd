package io.github.sophon.wiki.app.outPort

import io.github.sophon.wiki.model.WikiEvent

internal interface PublishWikiEventPort {
    suspend fun publish(event: WikiEvent)
}
