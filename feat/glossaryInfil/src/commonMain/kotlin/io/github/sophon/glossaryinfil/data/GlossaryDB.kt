package io.github.sophon.glossaryinfil.data

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.glossaryinfil.GlossaryError
import io.github.sophon.glossaryinfil.domain.GlossaryItem

interface GlossaryDB {
    suspend fun fetchDataFor(query: String): Result<List<GlossaryItem>, GlossaryError>
    suspend fun insertData(term: String, item: GlossaryItem): EmptyResult<GlossaryError>
}