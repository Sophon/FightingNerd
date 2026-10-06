package io.github.sophon.glossaryinfil.app.service

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.util.removeWhiteSpace
import io.github.sophon.glossaryinfil.app.outPort.CountGlossaryItemsPort
import io.github.sophon.glossaryinfil.app.outPort.LoadGlossaryItemListPort
import io.github.sophon.glossaryinfil.inPort.SearchGlossaryUseCase
import io.github.sophon.glossaryinfil.model.GlossaryError
import io.github.sophon.glossaryinfil.model.GlossaryItem

internal class SearchGlossaryService(
    private val loadGlossaryItemListPort: LoadGlossaryItemListPort,
    private val countGlossaryItemsPort: CountGlossaryItemsPort,
): SearchGlossaryUseCase {
    override suspend fun invoke(query: String): Result<List<GlossaryItem>, GlossaryError> {
        Napier.d(tag = TAG) { "query: $query" }
        val compactQuery = query.removeWhiteSpace()

        val result = loadGlossaryItemListPort.load(query = query, compactQuery = compactQuery)
            .mapError { error -> GlossaryError.Database(error) }
            .flatMap { itemList -> requireStoredGlossary(itemList) }
            .map { itemList -> itemList.sortedByRelevance(query, compactQuery) }
            .onError { error -> Napier.e(tag = TAG) { "$query: $error" } }
        return result
    }

    // nothing found in an empty glossary means it hasn't been downloaded yet, not that the term is unknown
    private suspend fun requireStoredGlossary(
        itemList: List<GlossaryItem>,
    ): Result<List<GlossaryItem>, GlossaryError> {
        if (itemList.isNotEmpty()) return Result.Success(itemList)

        val result = countGlossaryItemsPort.count()
            .mapError { error -> GlossaryError.Database(error) }
            .flatMap { count ->
                if (count == 0L) {
                    Result.Error(GlossaryError.EmptyGlossary)
                } else {
                    Result.Success(itemList)
                }
            }
        return result
    }

    private fun List<GlossaryItem>.sortedByRelevance(
        query: String,
        compactQuery: String,
    ): List<GlossaryItem> {
        val sorted = this.sortedWith(
            compareByDescending<GlossaryItem> { item ->
                // Exact match (case insensitive)
                item.term.equals(query, ignoreCase = true)
            }.thenByDescending { item ->
                // Exact match without whitespace
                item.term.removeWhiteSpace().equals(compactQuery, ignoreCase = true)
            }.thenBy { item ->
                // Among partial matches, prefer shorter terms
                item.term.length
            }
        )
        return sorted
    }


    private companion object {
        const val TAG = "SearchGlossaryService"
    }
}
