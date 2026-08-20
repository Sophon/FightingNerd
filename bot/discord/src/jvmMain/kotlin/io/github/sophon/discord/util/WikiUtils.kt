package io.github.sophon.discord.util

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.BotOutput
import io.github.sophon.discord.feat.core.usecase.GetCharactersUseCase
import io.github.sophon.discord.feat.core.usecase.GetMovesUseCase

internal suspend fun withWiki(
    wikis: Map<Game, WikiClient>,
    game: Game,
    query: String,
    action: suspend (Game, WikiClient, String) -> Result<BotOutput, BotError>,
): Result<BotOutput, BotError> {
    return wikis[game]?.let { wiki ->
        action(game, wiki, query)
    } ?: Result.Error(BotError.UnsupportedGame(query))
}

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

internal fun List<Character>.findMatching(query: String): Character? {
    val normalizedQuery = query.normalizeForMatch()

    firstOrNull { it.id == normalizedQuery }?.let { return it }
    firstOrNull {
        it.displayName.normalizeForMatch() == normalizedQuery
    }?.let { return it }
    firstOrNull { character ->
        character.aliasList.any { it.normalizeForMatch() == normalizedQuery }
    }?.let { return it }

    return null
}

fun List<Move>.findMatching(query: String): Move? {
    val normalizedQuery = query.normalizeForMatch()

    firstOrNull { it.id == normalizedQuery }
        ?.let { return it }
    firstOrNull { it.input.normalizeForMatch() == normalizedQuery }
        ?.let { return it }
    firstOrNull { move ->
        move.aliases.any { it.normalizeForMatch() == normalizedQuery }
    }?.let { return it }

    firstOrNull { move ->
        move.name?.normalizeForMatch() == normalizedQuery
    }?.let { return it }

    return null
}

internal suspend fun firstMatchingWikiMoves(
    wikiClientMap: Map<Game, WikiClient>,
    getMovesUseCase: GetMovesUseCase,
    characterId: String,
): Result<List<Move>, BotError> {
    var lastError: BotError? = null
    for (wiki in wikiClientMap.values) {
        when (val result = getMovesUseCase.invoke(wiki = wiki, characterQuery = characterId)) {
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


private fun String.normalizeForMatch(): String {
    val normalized = replace(" ", "").lowercase()
    return normalized
}
