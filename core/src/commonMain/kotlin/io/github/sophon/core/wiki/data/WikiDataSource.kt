package io.github.sophon.core.wiki.data

import io.github.sophon.core.domain.DataError
import io.github.sophon.core.domain.Result

interface WikiDataSource<C, M> {
    suspend fun downloadCharacterList(): Result<C, DataError.Remote>
    suspend fun downloadMoveListFor(charName: String): Result<M, DataError.Remote>
}