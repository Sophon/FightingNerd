package io.github.sophon.discord.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.wiki.domain.Filter
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.domain.model.BotError
import io.github.sophon.discord.domain.toDomainError

internal class GetMovesUseCase {
    suspend fun invoke(
        wiki: WikiClient,
        charName: String,
        filter: Filter,
    ): Result<List<Move>, BotError> {
        return wiki.fetchMoveList(charName, filter)
            .mapError { it.toDomainError() }
            .map { moveList ->
                moveList.distinctBy { it.input }
            }
    }
}