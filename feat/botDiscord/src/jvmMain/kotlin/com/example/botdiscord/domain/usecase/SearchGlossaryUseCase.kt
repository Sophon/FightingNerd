package com.example.botdiscord.domain.usecase

import com.example.botdiscord.BotError
import io.github.sophon.core.domain.Result
import com.example.glossaryinfil.InfilGlossary
import com.example.glossaryinfil.domain.GlossaryItem
import io.github.aakira.napier.Napier

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