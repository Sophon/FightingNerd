package io.github.sophon.dreamcancel.data

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result

internal class WikiImageUrlResolver (
    private val source: DreamCancelWikiDataSource,
) {
    suspend fun resolveHitboxUrl(
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