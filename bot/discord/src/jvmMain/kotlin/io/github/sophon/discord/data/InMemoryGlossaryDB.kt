package io.github.sophon.discord.data

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.util.removeWhiteSpace
import io.github.sophon.glossaryinfil.GlossaryError
import io.github.sophon.glossaryinfil.data.GlossaryDB
import io.github.sophon.glossaryinfil.domain.GlossaryItem

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