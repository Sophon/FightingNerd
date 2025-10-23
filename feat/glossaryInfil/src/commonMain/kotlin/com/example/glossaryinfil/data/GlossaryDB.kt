package com.example.glossaryinfil.data

import com.example.glossaryinfil.GlossaryError
import com.example.core.domain.EmptyResult
import com.example.core.domain.Result
import com.example.glossaryinfil.domain.GlossaryItem

interface GlossaryDB {
    suspend fun fetchDataFor(query: String): Result<List<GlossaryItem>, GlossaryError>
    suspend fun insertData(term: String, item: GlossaryItem): EmptyResult<GlossaryError>
}