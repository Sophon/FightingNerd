package data

import GlossaryError
import com.example.core.domain.EmptyResult
import com.example.core.domain.Result
import com.example.core.util.removeWhiteSpace
import dataLocal.GlossaryDB
import model.GlossaryItem

class InMemoryGlossaryDB: GlossaryDB {
    private var glossary = mutableMapOf<String, GlossaryItem>()

    override suspend fun fetchDataFor(query: String): Result<List<GlossaryItem>, GlossaryError> {
        if (glossary.isEmpty()) return Result.Error(GlossaryError.EMPTY_GLOSSARY)

        val result = glossary.entries
            .filter {
                it.key.contains(query, ignoreCase = true)
                        || it.key.contains(query.removeWhiteSpace(), ignoreCase = true)
            }
            .map { it.value }

        return Result.Success(result)
    }

    override suspend fun insertData(term: String, item: GlossaryItem): EmptyResult<GlossaryError> {
        glossary.put(key = term, value = item)
        return Result.Success(Unit)
    }
}