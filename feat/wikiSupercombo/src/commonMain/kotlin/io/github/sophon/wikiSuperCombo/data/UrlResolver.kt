package io.github.sophon.wikiSuperCombo.data

import io.github.sophon.core.domain.DataError
import io.github.sophon.core.domain.Result

internal class UrlResolver(
    private val source: SuperComboDataSource
) {
    suspend fun resolveImageUrls(
        dto: CharacterListResponseDto
    ): Result<Map<String, String>, DataError.Remote> {
        val imageFileNames = dto.cargoquery.flatMap {
            listOfNotNull(it.title.icon, it.title.portrait)
        }.distinct()

        return source.getImageUrl(imageFileNames)
    }

    suspend fun resolveHitboxUrl(
        dto: MoveListResponseDto,
    ): Result<Map<String, String>, DataError.Remote> {
        val imageFileNames = dto.cargoQuery.flatMap {
            listOfNotNull(it.title.hitboxes)
        }.distinct()

        return source.getImageUrl(imageFileNames)
    }
}