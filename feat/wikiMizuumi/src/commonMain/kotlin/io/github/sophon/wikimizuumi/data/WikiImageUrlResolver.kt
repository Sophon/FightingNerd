package io.github.sophon.wikimizuumi.data

import io.github.sophon.core.domain.DataError
import io.github.sophon.core.domain.Result

internal class WikiImageUrlResolver(
    private val source: MizuumiWikiDataSource,
) {
    suspend fun resolveHitboxUrl(
        dto: MoveListResponseDto,
    ): Result<Map<String, String>, DataError.Remote> {
        val imageFileNames = dto.cargoquery.flatMap { moveDto ->
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