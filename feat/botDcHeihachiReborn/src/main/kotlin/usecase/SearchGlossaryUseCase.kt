package usecase

import InfilGlossary
import com.example.core.domain.Result
import BotError
import model.GlossaryItem
import io.github.aakira.napier.Napier

internal class SearchGlossaryUseCase(
    private val glossary: InfilGlossary,
    private val startGlossaryUseCase: StartGlossaryUseCase,
) {
    suspend fun invoke(query: String): Result<GlossaryItem, BotError> {
        val cleanQuery = query.substringAfter(' ')
        return when (val result = glossary.search(cleanQuery)) {
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