package io.github.sophon.wiki.model

import io.github.sophon.wiki.model.wiki.Game

sealed interface WikiEvent {
    /**
     * Per game: [Started], then any number of [Progress] and [Failure], then exactly one [Finished].
     * A game cancelled before it started sends nothing.
     */
    sealed interface Refresh : WikiEvent {
        val game: Game

        /**
         * The game got a download slot.
         */
        data class Started(override val game: Game) : Refresh

        data class Progress(override val game: Game, val fraction: Float) : Refresh

        /**
         * Not terminal - the refresh carries on. [characterId] is `null` when it's the whole game that failed.
         */
        data class Failure(
            override val game: Game,
            val error: WikiError,
            val characterId: CharacterId? = null,
        ) : Refresh

        /**
         * Crashed and cancelled games too - [successCount] is what got saved before they stopped.
         */
        data class Finished(override val game: Game, val successCount: Int) : Refresh
    }
}
