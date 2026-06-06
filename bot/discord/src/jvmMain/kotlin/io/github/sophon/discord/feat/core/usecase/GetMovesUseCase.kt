package io.github.sophon.discord.feat.core.usecase

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.toDomainError

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