package io.github.sophon.discord.feat.infilGlossary.usecase

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.mapError
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.glossaryinfil.integration.InfilGlossaryClient

internal class StartGlossaryUseCase(
    private val glossary: InfilGlossaryClient,
) {
    suspend fun invoke(): EmptyResult<BotError> {
        val result = glossary.downloadGlossary()
            .mapError { BotError.Unknown(it.toString()) }
        return result
    }
}