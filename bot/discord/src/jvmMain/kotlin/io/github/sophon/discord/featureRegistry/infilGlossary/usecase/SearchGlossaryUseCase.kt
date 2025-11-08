package io.github.sophon.discord.featureRegistry.infilGlossary.usecase

import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.Result
import io.github.sophon.discord.BotError
import io.github.sophon.glossaryinfil.InfilGlossary
import io.github.sophon.glossaryinfil.domain.GlossaryItem

internal class SearchGlossaryUseCase(
    private val glossary: InfilGlossary,
    private val startGlossaryUseCase: StartGlossaryUseCase,
) {
    suspend fun invoke(query: String): Result<GlossaryItem, BotError> {
        return when (val result = glossary.search(query)) {
            is Result.Success -> {
                result.data
                    .firstOrNull()
                    ?.let { Result.Success(it) }
                    ?: Result.Error(BotError.GLOSSARY_TERM_NOT_FOUND)
            }
            is Result.Error -> {
                Napier.e(tag = TAG) { result.error.toString() }
                startGlossaryUseCase.invoke()
                Result.Error(BotError.EMPTY_GLOSSARY)
            }
        }
    }
}

private const val TAG = "SearchGlossaryUseCase"