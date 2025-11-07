package io.github.sophon.core.wiki.domain.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.WikiDataSource
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.data.toDomain
import io.github.sophon.core.wiki.domain.model.Move

class DownloadMoveListUseCase<M>(
    private val source: WikiDataSource<*, M>,
    private val toDomain: (M, String) -> List<Move>,
) {
    suspend fun invoke(charName: String): Result<List<Move>, WikiError> {
        return when (val result = source.downloadMoveListFor(charName)) {
            is Result.Success -> {
                val moves = toDomain(result.data, charName)
                Result.Success(moves)
            }
            is Result.Error -> {
                Result.Error(result.error.toDomain(TAG))
            }
        }
    }

    private companion object {
        const val TAG = "DownloadMoveListUseCase"
    }
}