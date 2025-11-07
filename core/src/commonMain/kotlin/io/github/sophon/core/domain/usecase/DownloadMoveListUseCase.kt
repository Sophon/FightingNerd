package io.github.sophon.core.domain.usecase

import io.github.sophon.core.data.WikiDataSource
import io.github.sophon.core.domain.DataError
import io.github.sophon.core.domain.Error
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.model.Move

class DownloadMoveListUseCase<M, E: Error>(
    private val source: WikiDataSource<*, M>,
    private val toDomain: (M, String) -> List<Move>,
    private val toDomainError: DataError.Remote.() -> E,
) {
    suspend fun invoke(charName: String): Result<List<Move>, E> {
        return when (val result = source.downloadMoveListFor(charName)) {
            is Result.Success -> {
                val moves = toDomain(result.data, charName)
                Result.Success(moves)
            }
            is Result.Error -> {
                Result.Error(result.error.toDomainError())
            }
        }
    }
}