package io.github.sophon.core.wiki.usecase

import io.github.sophon.core.domain.DataError
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.wiki.data.QueryTable
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.data.toDomainError
import io.github.sophon.core.wiki.domain.model.Move

class DownloadMoveListUseCase(
    private val downloadAndMap: suspend (
        queryTable: QueryTable,
        charName: String
    ) -> Result<List<Move>, DataError.Remote>,
) {
    suspend fun invoke(
        queryTable: QueryTable,
        charName: String,
    ): Result<List<Move>, WikiError> {
        return downloadAndMap(queryTable, charName)
            .mapError { it.toDomainError(TAG) }
    }

    private companion object {
        const val TAG = "DownloadMoveListUseCase"
    }
}