package io.github.sophon.discord.feat.wikiDustLoop

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.feature.Game
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.Filter
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.domain.model.BotError
import io.github.sophon.discord.domain.toDomainError
import io.github.sophon.wikidustloop.domain.DustLoopFilter

internal class FetchDustLoopInvincibleMovesUseCase {
    suspend fun invoke(
        gameId: String,
        wiki: WikiClient,
        charName: String,
    ): Result<List<Move>, BotError> {
        return getInvincibleMoves(gameId, charName, wiki)
            .mapError { it.toDomainError() }
    }

    private suspend fun getInvincibleMoves(
        gameId: String,
        charName: String,
        wiki: WikiClient,
    ): Result<List<Move>, WikiError> {
        val game = Game.fromId(gameId) ?: Game.GGST

        val filter = when (game) {
            Game.BBCF -> DustLoopFilter.BBInvincible
            Game.GGST -> DustLoopFilter.GGSTInvincible
            else -> Filter.None
        }

        return wiki.fetchMoveList(charName, filter)
    }
}