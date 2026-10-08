package io.github.sophon.wiki.model

import io.github.sophon.wiki.model.wiki.Game

sealed interface RefreshEvent {
    data class Failed(val game: Game, val error: WikiError) : RefreshEvent
    data class Finished(val game: Game, val successCount: Int) : RefreshEvent
}
