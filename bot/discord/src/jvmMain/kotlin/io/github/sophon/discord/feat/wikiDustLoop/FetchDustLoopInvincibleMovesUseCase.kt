package io.github.sophon.discord.feat.wikiDustLoop

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.toDomainError
import io.github.sophon.wikidustloop.integration.model.DustLoopFilter

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