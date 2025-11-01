package com.example.glossaryinfil.data

import com.example.glossaryinfil.GlossaryError
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import com.example.glossaryinfil.domain.GlossaryItem

interface GlossaryDB {
    suspend fun fetchDataFor(query: String): Result<List<GlossaryItem>, GlossaryError>
    suspend fun insertData(term: String, item: GlossaryItem): EmptyResult<GlossaryError>
}