package io.github.sophon.wikiSuperCombo.data

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result

internal class WikiImageUrlResolver(
    private val source: SuperComboDataSource
) {
    suspend fun resolveCharImageUrls(
        dto: CharacterListResponseDto
    ): Result<Map<String, String>, DataError.Remote> {
        val imageFileNames = dto.cargoquery.flatMap {
            listOfNotNull(it.title.icon, it.title.portrait)
        }.distinct()

        return source.getImageUrl(imageFileNames)
    }

    suspend fun resolveMoveUrl(
        dto: MoveListResponseDto,
    ): Result<Map<String, String>, DataError.Remote> {
        val imageFileNames = dto.cargoQuery.flatMap { moveDto ->
            listOfNotNull(moveDto.title.hitboxes, moveDto.title.images)
                .takeIf { it.isNotEmpty() }
                ?.joinToString(",")
                ?.split(",")
                ?.map { it.trim() }
                ?: emptyList()
        }.distinct()

        return source.getImageUrl(imageFileNames)
    }
}