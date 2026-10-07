package io.github.sophon.fightingnerd.app.model

sealed interface RefreshEvent {
    data class Failed(val error: AppError) : RefreshEvent
    data class Finished(val successCount: Int) : RefreshEvent
}
