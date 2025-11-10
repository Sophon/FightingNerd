package io.github.sophon.wikiSuperCombo.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.data.toDomainError
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.wikiSuperCombo.data.SuperComboDataSource
import io.github.sophon.wikiSuperCombo.data.toDomain

internal class DownloadMoveListUseCase(
    private val source: SuperComboDataSource,
) {
    suspend fun invoke(charName: String): Result<List<Move>, WikiError> {
        return source.downloadMoveListFor(charName)
            .map { it.toDomain() }
            .mapError { it.toDomainError(TAG) }
    }

    private companion object {
        const val TAG = "DownloadMoveListUseCase"
    }
}