package io.github.sophon.glossaryinfil.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.mapError
import io.github.sophon.glossaryinfil.data.InfilGlossaryDataSource
import io.github.sophon.glossaryinfil.data.toDomain
import io.github.sophon.glossaryinfil.integration.model.GlossaryError
import io.github.sophon.glossaryinfil.integration.model.GlossaryItem

internal class DownloadGlossaryUseCase(
    private val dataSource: InfilGlossaryDataSource,
) {
    suspend fun invoke(): Result<List<GlossaryItem>, GlossaryError> {
        return dataSource.getGlossary()
            .map { dto ->
                dto.map { it.toDomain() }
            }
            .mapError { GlossaryError.ERROR_DOWNLOADING_DATA }
    }
}