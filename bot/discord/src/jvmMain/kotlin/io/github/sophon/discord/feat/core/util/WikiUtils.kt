package io.github.sophon.discord.feat.core.util

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.usecase.GetCharactersUseCase
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.discord.feat.core.usecase.GetMovesUseCase
import kotlin.collections.plusAssign

internal suspend fun aggregateCharacters(
    wikiClientMap: Map<Game, WikiClient>,
    getCharactersUseCase: GetCharactersUseCase,
): Result<List<Pair<Game, Character>>, BotError> {
    val aggregated = mutableListOf<Pair<Game, Character>>()
    var lastError: BotError? = null
    for ((game, wiki) in wikiClientMap) {
        when (val result = getCharactersUseCase.invoke(wiki)) {
            is Result.Success -> aggregated += result.data.map { game to it }
            is Result.Error -> lastError = result.error
        }
    }
    val result: Result<List<Pair<Game, Character>>, BotError> =
        if (aggregated.isEmpty() && lastError != null) {
            Result.Error(lastError)
        } else {
            Result.Success(aggregated.toList())
        }
    return result
}

internal suspend fun firstMatchingWikiMoves(
    wikiClientMap: Map<Game, WikiClient>,
    getMovesUseCase: GetMovesUseCase,
    characterId: String,
): Result<List<Move>, BotError> {
    var lastError: BotError? = null
    for (wiki in wikiClientMap.values) {
        when (val result = getMovesUseCase.invoke(wiki = wiki, characterId = characterId)) {
            is Result.Success -> {
                val moveList = result.data.second
                if (moveList.isNotEmpty()) {
                    val success: Result<List<Move>, BotError> = Result.Success(moveList)
                    return success
                }
            }
            is Result.Error -> lastError = result.error
        }
    }
    val fallback: Result<List<Move>, BotError> = Result.Error(lastError ?: BotError.UnknownMove(characterId))
    return fallback
}
