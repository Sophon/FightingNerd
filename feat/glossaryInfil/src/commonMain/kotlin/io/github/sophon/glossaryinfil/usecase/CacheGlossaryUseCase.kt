package io.github.sophon.glossaryinfil.usecase

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.asEmptyDataResult
import io.github.sophon.core.domain.flatMap
import io.github.sophon.glossaryinfil.integration.data.GlossaryDB
import io.github.sophon.glossaryinfil.integration.model.GlossaryError
import io.github.sophon.glossaryinfil.integration.model.GlossaryItem

internal class CacheGlossaryUseCase(
    private val db: GlossaryDB,
) {
    suspend fun invoke(items: List<GlossaryItem>): EmptyResult<GlossaryError> {
        return items.fold(
            initial = Result.Success(Unit) as EmptyResult<GlossaryError>
        ) { acc, item ->
            acc.flatMap { insertItem(item) }
        }
    }

    private suspend fun insertItem(item: GlossaryItem): EmptyResult<GlossaryError> {
        return db.insertData(term = item.term, item = item)
            .asEmptyDataResult()
            .flatMap {
                item.altTerm.fold(
                    initial = Result.Success(Unit) as EmptyResult<GlossaryError>
                ) { acc, altTerm ->
                    acc.flatMap {
                        db.insertData(term = altTerm, item = item).asEmptyDataResult()
                    }
                }
            }
    }
}