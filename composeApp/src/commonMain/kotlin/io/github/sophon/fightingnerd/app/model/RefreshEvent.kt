package io.github.sophon.fightingnerd.app.model

sealed interface RefreshEvent {
    data class Failed(val game: Game, val error: AppError) : RefreshEvent
    data class Finished(val game: Game, val successCount: Int) : RefreshEvent
}
