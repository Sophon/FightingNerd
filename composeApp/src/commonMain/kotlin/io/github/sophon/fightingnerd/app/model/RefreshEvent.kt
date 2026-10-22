package io.github.sophon.fightingnerd.app.model

/**
 * Per game: [Started], then any number of [Progress] and [Failure], then exactly one [Finished].
 */
sealed interface RefreshEvent {
    val game: Game

    data class Started(override val game: Game) : RefreshEvent
    data class Progress(override val game: Game, val fraction: Float) : RefreshEvent

    /**
     * Not terminal - the refresh carries on. [characterId] is `null` when it's the whole game that failed.
     */
    data class Failure(override val game: Game, val error: AppError, val characterId: String? = null) : RefreshEvent

    data class Finished(override val game: Game, val successCount: Int) : RefreshEvent
}
