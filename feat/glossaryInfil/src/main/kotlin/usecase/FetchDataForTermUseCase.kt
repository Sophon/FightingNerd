package usecase

import GlossaryError
import com.example.core.domain.Result
import com.example.core.domain.map
import com.example.core.util.removeWhiteSpace
import data.GlossaryDB
import domain.GlossaryItem

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