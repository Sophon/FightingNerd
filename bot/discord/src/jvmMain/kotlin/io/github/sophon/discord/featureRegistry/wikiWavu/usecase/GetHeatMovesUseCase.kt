package io.github.sophon.discord.featureRegistry.wikiWavu.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.BotError
import io.github.sophon.discord.domain.toDomainError
import io.github.sophon.wikiwavu.WavuWikiClient

class GetHeatMovesUseCase(
    private val wiki: WavuWikiClient,
) {
    suspend fun invoke(charName: String): Result<List<Move>, BotError> {
        return wiki.getHeatMoves(charName)
            .mapError { it.toDomainError() }
    }
}