package io.github.sophon.wiki.model

//TODO: add which game this event is about
sealed interface RefreshEvent {
    data class Failed(val error: WikiError) : RefreshEvent
    data class Finished(val successCount: Int) : RefreshEvent
}
