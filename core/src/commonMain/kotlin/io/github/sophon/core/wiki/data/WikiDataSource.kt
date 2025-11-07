package io.github.sophon.core.wiki.data

import io.github.sophon.core.domain.DataError
import io.github.sophon.core.domain.Result

interface WikiDataSource<C, M> {
    suspend fun downloadCharacterList(): Result<C, WikiDataError>
    suspend fun downloadMoveListFor(charName: String): Result<M, WikiDataError>
}