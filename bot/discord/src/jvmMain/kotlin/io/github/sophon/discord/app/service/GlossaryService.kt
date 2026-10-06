package io.github.sophon.discord.app.service

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.onError
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.response.GlossaryResponse
import io.github.sophon.discord.app.outPort.GlossaryPort
import io.github.sophon.discord.app.outPort.RefreshGlossaryPort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal interface GlossaryService {
    suspend fun findTerm(query: String): Result<GlossaryResponse, BotError>
}

internal class GlossaryServiceImpl(
    private val glossaryPort: GlossaryPort,
    private val refreshGlossaryPort: RefreshGlossaryPort,
    private val coroutineScope: CoroutineScope,
): GlossaryService {
    /**
     * An empty glossary starts a refresh in the background - a download outlasts Discord's response window.
     */
    override suspend fun findTerm(query: String): Result<GlossaryResponse, BotError> {
        val result = glossaryPort.search(query)
            .flatMap { glossaryList -> pickBestMatch(query = query, glossaryList = glossaryList) }
            .onError { error ->
                if (error is BotError.EmptyGlossary) {
                    coroutineScope.launch { refreshGlossaryPort.refresh() }
                }
            }
        return result
    }


    private fun pickBestMatch(
        query: String,
        glossaryList: List<GlossaryResponse>,
    ): Result<GlossaryResponse, BotError> {
        val bestMatch = glossaryList.firstOrNull()
        val result = if (bestMatch == null) {
            Result.Error(BotError.GlossaryTermNotFound(query))
        } else {
            Result.Success(bestMatch)
        }
        return result
    }
}
