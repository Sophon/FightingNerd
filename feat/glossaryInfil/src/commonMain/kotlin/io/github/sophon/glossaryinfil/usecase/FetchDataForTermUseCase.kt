package io.github.sophon.glossaryinfil.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.util.removeWhiteSpace
import io.github.sophon.glossaryinfil.data.GlossaryDB
import io.github.sophon.glossaryinfil.integration.GlossaryError
import io.github.sophon.glossaryinfil.integration.GlossaryItem

class FetchDataForTermUseCase(
    private val db: GlossaryDB,
) {
    suspend fun invoke(query: String): Result<List<GlossaryItem>, GlossaryError> {
        val normalizedQuery = query.removeWhiteSpace()
        return db.fetchDataFor(query)
            .map { items ->
                items
                    .distinctBy { it.term }
                    .sortedWith(
                        compareByDescending<GlossaryItem> { item ->
                            // Exact match (case insensitive)
                            item.term.equals(query, ignoreCase = true)
                        }.thenByDescending { item ->
                            // Exact match without whitespace
                            item.term.removeWhiteSpace().equals(normalizedQuery, ignoreCase = true)
                        }.thenBy { item ->
                            // Among partial matches, prefer shorter terms
                            item.term.length
                        }
                    )
            }
    }
}