package io.github.sophon.discord.feat.infilGlossary.usecase

import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.Result
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.glossaryinfil.integration.InfilGlossaryClient
import io.github.sophon.glossaryinfil.integration.GlossaryItem

internal class SearchGlossaryUseCase(
    private val glossary: InfilGlossaryClient,
    private val startGlossaryUseCase: StartGlossaryUseCase,
) {
    suspend fun invoke(query: String): Result<GlossaryItem, BotError> {
        return when (val result = glossary.search(query)) {
            is Result.Success -> {
                result.data
                    .firstOrNull()
                    ?.let { Result.Success(it) }
                    ?: Result.Error(BotError.GlossaryTermNotFound(query))
            }
            is Result.Error -> {
                Napier.e(tag = TAG) { result.error.toString() }
                startGlossaryUseCase.invoke()
                Result.Error(BotError.DatabaseError())
            }
        }
    }
}

private const val TAG = "SearchGlossaryUseCase"