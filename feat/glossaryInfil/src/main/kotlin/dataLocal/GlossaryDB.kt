package dataLocal

import GlossaryError
import com.example.core.domain.EmptyResult
import com.example.core.domain.Result
import model.GlossaryItem

interface GlossaryDB {
    suspend fun fetchDataFor(query: String): Result<List<GlossaryItem>, GlossaryError>
    suspend fun insertData(term: String, item: GlossaryItem): EmptyResult<GlossaryError>
}