package com.example.glossaryinfil.usecase

import com.example.glossaryinfil.GlossaryError
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import com.example.glossaryinfil.data.GlossaryDB
import com.example.glossaryinfil.domain.GlossaryItem

class CacheGlossaryUseCase(
    private val db: GlossaryDB,
) {
    suspend fun invoke(items: List<GlossaryItem>): EmptyResult<GlossaryError> {
        items.forEach { item ->
            db.insertData(term = item.term, item = item).let { result ->
                if (result is Result.Error) return result
            }

            item.altTerm.forEach { alias ->
                db.insertData(term = alias, item = item).let { result ->
                    if (result is Result.Error) return result
                }
            }
        }

        return Result.Success(Unit)
    }
}