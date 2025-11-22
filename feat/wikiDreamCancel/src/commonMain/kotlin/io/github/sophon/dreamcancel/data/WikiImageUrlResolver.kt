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
            val files = it.title.hitboxes
                ?.split(", ")
                .orEmpty()
            files.getOrNull(files.size / 2)
        }.distinct()

        return source.getImageUrl(imageFileNames)
    }
}