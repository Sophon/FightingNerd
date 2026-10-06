package io.github.sophon.wiki.model

sealed interface RefreshEvent {
    data class Failed(val error: WikiError) : RefreshEvent
    data class Finished(val successCount: Int) : RefreshEvent
}
