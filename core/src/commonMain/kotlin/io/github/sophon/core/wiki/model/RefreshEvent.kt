package io.github.sophon.core.wiki.model

import io.github.sophon.core.wiki.data.WikiError

sealed interface RefreshEvent {
    data class Failed(val error: WikiError) : RefreshEvent
    data class Finished(val successCount: Int) : RefreshEvent
}
