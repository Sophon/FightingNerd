package io.github.sophon.wikimizuumi.data

import io.github.sophon.core.domain.DataError
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map

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

    /**
     * MBTL has impossible to decipher names
     * so for now, only Uni has char images
     */
    suspend fun resolveImageUrls(
        dto: CharacterListResponseDto,
    ): Result<Map<String, String>, DataError.Remote> {
        val imageFileNames = dto.cargoquery.flatMap {
            listOfNotNull("UNI2_${it.title.chara}_CSel.png")
        }.distinct()

        val result = source.getImageUrl(imageFileNames)
            .map { urlMap ->
                urlMap.mapKeys { (filename, _) ->
                    filename
                        .removePrefix("UNI2_")
                        .removeSuffix("_CSel.png")
                }
            }

        return result
    }
}