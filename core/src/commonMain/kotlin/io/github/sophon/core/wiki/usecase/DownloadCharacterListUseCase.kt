package io.github.sophon.core.wiki.usecase

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.wiki.data.QueryTable
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.data.toDomainError
import io.github.sophon.core.wiki.model.Character

class DownloadCharacterListUseCase(
    private val downloadAndMap: suspend (queryTable: QueryTable) -> Result<List<Character>, DataError.Remote>
) {
    suspend fun invoke(
        queryTable: QueryTable,
    ): Result<List<Character>, WikiError> {
        return downloadAndMap(queryTable)
            .mapError { it.toDomainError(TAG) }
    }

    private companion object {
        const val TAG = "DownloadCharacterListUseCase"
    }
}