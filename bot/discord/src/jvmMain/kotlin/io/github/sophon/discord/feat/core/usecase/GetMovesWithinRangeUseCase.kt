package io.github.sophon.discord.feat.core.usecase

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.util.equalsIgnoreCase
import io.github.sophon.core.wiki.model.CharacterId
import io.github.sophon.core.wiki.model.CoreFilters
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.core.wiki.util.findMatching
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.Command
import io.github.sophon.discord.feat.core.domain.model.MoveRange
import kotlinx.coroutines.flow.first

internal class GetMovesWithinRangeUseCase {
    suspend operator fun invoke(
        wiki: WikiClient,
        command: Command,
        query: String,
    ): Result<MoveRange, BotError> {
        val rangeQuery = query.substringAfter(" ", missingDelimiterValue = "")
        val (from, to) = rangeQuery.parseIntoRange()
            ?: return rangeQuery.toFormattedError()

        val filter = when (command) {
            Command.Startup -> CoreFilters.Startup(from, to)
            Command.OnBlock -> CoreFilters.OnBlock(from, to)
            Command.OnHit -> CoreFilters.OnHit(from, to)
            Command.OnCounter -> CoreFilters.OnCounter(from, to)
            else -> return rangeQuery.toFormattedError()
        }

        val characterQuery = query.substringBefore(" ")
        val characterList = wiki.subscribeToCharacterList().first()
        val character = characterList.findMatching(characterQuery)
            ?: return Result.Error(BotError.UnknownCharacter(characterQuery))

        val moveList = wiki.subscribeToMoveList(CharacterId(character.id)).first()
            .filter(filter.predicate)
            .distinctBy { it.input }

        val range = MoveRange(
            rangeType = filter,
            character = character,
            from = from,
            to = to,
            moveList = moveList,
        )
        return Result.Success(range)
    }


    private fun String.parseIntoRange(): Pair<Int, Int>? {
        val range = this
            .split(" ")
            .mapNotNull {
                when {
                    it.equalsIgnoreCase("inf") || it.equalsIgnoreCase("+inf") -> Int.MAX_VALUE
                    it.equalsIgnoreCase("-inf") -> Int.MIN_VALUE
                    else -> it.toIntOrNull()
                }
            }
            .take(2)

        if (range.isEmpty()) return null

        val (low, high) = if (range.size < 2) {
            listOf(range[0], range[0])
        } else {
            range.sorted()
        }
        return low to high
    }

    private fun String.toFormattedError(): Result.Error<BotError> {
        return Result.Error(BotError.InvalidQuery("SYNTAX: <charName> <value> [bound]: $this"))
    }
}
