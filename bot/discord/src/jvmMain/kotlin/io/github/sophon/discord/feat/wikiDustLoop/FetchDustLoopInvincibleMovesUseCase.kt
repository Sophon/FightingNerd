package io.github.sophon.discord.feat.wikiDustLoop

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.toDomainError
import io.github.sophon.wikidustloop.integration.model.BBFilters
import io.github.sophon.wikidustloop.integration.model.GGFilters

internal class FetchDustLoopInvincibleMovesUseCase {
    suspend fun invoke(
        game: Game,
        wiki: WikiClient,
        charName: String,
    ): Result<Pair<Character, List<Move>>, BotError> {
        return getInvincibleMoves(game, charName, wiki)
            .mapError { it.toDomainError() }
    }

    private suspend fun getInvincibleMoves(
        game: Game,
        charName: String,
        wiki: WikiClient,
    ): Result<Pair<Character, List<Move>>, WikiError> {
        val filter = when (game) {
            Game.BBCF -> BBFilters.Invincible
            Game.GGST -> GGFilters.Invincible
            else -> Filter.None
        }

        val result = wiki.fetchCharacter(characterQuery = charName)
            .flatMap { character ->
                wiki.fetchMoveList(character.id, filter)
                    .map { moveList -> character to moveList }
            }
        return result
    }
}