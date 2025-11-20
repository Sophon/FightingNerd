package io.github.sophon.wikiSuperCombo.usecase

import io.github.sophon.core.domain.DataError
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.flatMap
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.domain.onSuccess
import io.github.sophon.core.wiki.data.QueryTable
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.data.toDomainError
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.wikiSuperCombo.data.MoveListResponseDto
import io.github.sophon.wikiSuperCombo.data.SuperComboDataSource
import io.github.sophon.wikiSuperCombo.data.toDomain

internal class DownloadMoveListUseCase(
    private val source: SuperComboDataSource,
) {
    suspend fun invoke(
        queryTable: QueryTable,
        charName: String
    ): Result<List<Move>, WikiError> {
        return source.downloadMoveListFor(queryTable.moves, charName)
            .flatMap { dto -> resolveHitboxUrl(dto) }
            .mapError { it.toDomainError(TAG) }
    }

    private suspend fun resolveHitboxUrl(
        dto: MoveListResponseDto,
    ): Result<List<Move>, DataError.Remote> {
        val imageFileNames = dto.cargoQuery.flatMap {
            listOfNotNull(it.title.hitboxes)
        }.distinct()

        return source.getImageUrl(imageFileNames)
            .map { imageUrlMap -> dto.toDomain(imageUrlMap) }
    }

    private companion object {
        const val TAG = "DownloadMoveListUseCase"
    }
}