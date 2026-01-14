package io.github.sophon.discord.featureRegistry.wikiDustLoop

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.feature.Game
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.Filter
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.BotError
import io.github.sophon.discord.domain.toDomainError
import io.github.sophon.wikidustloop.domain.DustLoopFilter

internal class FetchDustLoopInvincibleMovesUseCase {
    suspend fun invoke(
        game: Game,
        wiki: WikiClient,
        charName: String,
    ): Result<List<Move>, BotError> {
        return getInvincibleMoves(game, charName, wiki)
            .mapError { it.toDomainError() }
    }

    private suspend fun getInvincibleMoves(
        game: Game,
        charName: String,
        wiki: WikiClient,
    ): Result<List<Move>, WikiError> {
        val filter = when (game) {
            Game.BBCF -> DustLoopFilter.BBInvincible
            Game.GGST -> DustLoopFilter.GGSTInvincible
            else -> Filter.None
        }

        return wiki.fetchMoveList(charName, filter)
    }
}