package io.github.sophon.core.wiki.usecase

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.wiki.data.QueryTable
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.data.toDomainError
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move

class DownloadMoveListUseCase(
    private val downloadAndMap: suspend (
        queryTable: QueryTable,
        character: Character,
    ) -> Result<List<Move>, DataError.Remote>,
) {
    suspend fun invoke(
        queryTable: QueryTable,
        character: Character,
    ): Result<List<Move>, WikiError> {
        return downloadAndMap(queryTable, character)
            .mapError { it.toDomainError(TAG) }
    }


    private companion object {
        const val TAG = "DownloadMoveListUseCase"
    }
}