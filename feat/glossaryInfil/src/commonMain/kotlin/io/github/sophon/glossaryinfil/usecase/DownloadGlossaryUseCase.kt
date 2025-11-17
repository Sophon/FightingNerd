package io.github.sophon.glossaryinfil.usecase

import io.github.sophon.glossaryinfil.GlossaryError
import io.github.sophon.core.domain.Result
import io.github.sophon.glossaryinfil.data.InfilGlossaryDataSource
import io.github.sophon.glossaryinfil.domain.GlossaryItem

internal class DownloadGlossaryUseCase(
    private val dataSource: InfilGlossaryDataSource,
) {
    suspend fun invoke(): Result<List<GlossaryItem>, GlossaryError> {
        return when (val result = dataSource.getGlossary()) {
            is Result.Success -> {
                val data = result.data.map { item ->
                    GlossaryItem(
                        term = item.term,
                        definition = item.def,
                        altTerm = item.altterm.orEmpty(),
                        video = item.video ?: listOf(),
                        games = item.games ?: listOf(),
                        jpTranslation = item.jp
                            ?.split("<br>")
                            ?: listOf()
                    )
                }

                Result.Success(data)
            }
            is Result.Error -> {
                Result.Error(GlossaryError.ERROR_DOWNLOADING_DATA)
            }
        }
    }
}