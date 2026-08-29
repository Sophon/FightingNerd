package io.github.sophon.discord.feat.wikiDustLoop.usecase

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.CharacterId
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.core.wiki.util.findMatching
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.wikidustloop.integration.model.BBFilters
import io.github.sophon.wikidustloop.integration.model.GGFilters
import kotlinx.coroutines.flow.first

internal class FetchDustLoopInvincibleMovesUseCase {
    suspend fun invoke(
        game: Game,
        wiki: WikiClient,
        charName: String,
    ): Result<Pair<Character, List<Move>>, BotError> {
        val filter = when (game) {
            Game.BBCF -> BBFilters.Invincible
            Game.GGST -> GGFilters.Invincible
            else -> Filter.None
        }

        val characterList = wiki.subscribeToCharacterList().first()
        val character = characterList.findMatching(charName)
            ?: return Result.Error(BotError.UnknownCharacter(charName))

        val moveList = wiki.subscribeToMoveList(CharacterId(character.id)).first()
            .filter(filter.predicate)

        val result = Result.Success(character to moveList)
        return result
    }
}