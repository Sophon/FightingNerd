package io.github.sophon.wikiSuperCombo.data

import io.github.sophon.core.domain.DataError
import io.github.sophon.core.domain.Result

internal class UrlResolver(
    private val source: SuperComboDataSource
) {
    suspend fun resolveHitboxUrl(
        dto: MoveListResponseDto,
    ): Result<Map<String, String>, DataError.Remote> {
        val imageFileNames = dto.cargoQuery.flatMap {
            listOfNotNull(it.title.hitboxes)
        }.distinct()

        return source.getImageUrl(imageFileNames)
    }
}