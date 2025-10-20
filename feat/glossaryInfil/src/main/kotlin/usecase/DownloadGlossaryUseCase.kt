package usecase

import GlossaryError
import com.example.core.domain.Result
import dataRemote.InfilGlossaryDataSource
import model.GlossaryItem

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
                        altTerm = item.altterm,
                        video = item.video,
                        games = item.games,
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