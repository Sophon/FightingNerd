package io.github.sophon.discord.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.discord.BotError
import io.github.sophon.discord.domain.toDomainError

//TODO: write unit tests
internal class GetStancesUseCase {
    suspend fun invoke(
        wiki: WikiClient,
        charName: String,
    ): Result<List<String>, BotError> {
        return wiki.fetchMoveList(charName)
            .mapError { it.toDomainError() }
            .map { moveList ->
                moveList
                    .filter { it.t8Properties?.stance?.isNotBlank() == true }
                    .map { it.t8Properties!!.stance!! }
                    .distinct()
            }
    }
}