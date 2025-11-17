package io.github.sophon.wikiSuperCombo.usecase

import io.github.sophon.core.domain.DataError
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.flatMap
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.domain.onSuccess
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.data.toDomainError
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.wikiSuperCombo.data.CharacterListResponseDto
import io.github.sophon.wikiSuperCombo.data.SuperComboDataSource
import io.github.sophon.wikiSuperCombo.data.toDomain

internal class DownloadCharacterListUseCase(
    private val source: SuperComboDataSource
) {
    suspend fun invoke(): Result<List<Character>, WikiError> {
        return source.downloadCharacterList()
            .flatMap { dto ->
                resolveImageUrls(dto)
            }
            .mapError { it.toDomainError(TAG) }
    }

    private suspend fun resolveImageUrls(
        dto: CharacterListResponseDto
    ): Result<List<Character>, DataError.Remote> {
        // Extract all unique image filenames from both icon and portrait fields
        val imageFileNames = dto.cargoquery.flatMap {
            listOfNotNull(it.title.icon, it.title.portrait)
        }.distinct()

        // Fetch all image URLs
        val imageUrlMap = mutableMapOf<String, String>()
        imageFileNames.forEach { fileName ->
            source.getImageUrl(fileName)
                .onSuccess { url -> imageUrlMap[fileName] = url }
        }

        // Map to domain with resolved image URLs
        return Result.Success(dto.toDomain(imageUrlMap))
    }

    private companion object {
        const val TAG = "DownloadCharacterListUseCase"
    }
}