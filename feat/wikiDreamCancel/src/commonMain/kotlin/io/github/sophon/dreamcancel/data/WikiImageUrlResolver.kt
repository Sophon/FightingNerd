package io.github.sophon.dreamcancel.data

import io.github.sophon.core.domain.DataError
import io.github.sophon.core.domain.Result

internal class WikiImageUrlResolver (
    private val source: DreamCancelWikiDataSource,
) {
    suspend fun resolveHitboxUrl(
        dto: MoveListResponseDto,
    ): Result<Map<String, String>, DataError.Remote> {
        val imageFileNames = dto.cargoQuery.mapNotNull {
            it.title.hitboxes
                ?.split(", ")
                ?.firstOrNull()
        }.distinct()

        return source.getImageUrl(imageFileNames)
    }
}