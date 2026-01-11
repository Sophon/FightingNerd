package io.github.sophon.wikimizuumi.data

import io.github.sophon.core.domain.DataError
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.feature.Game

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

    suspend fun resolveImageUrls(
        dto: CharacterListResponseDto,
    ): Result<Map<String, String>, DataError.Remote> {
        val prefix = "UNI2_"
        val suffix = "_CSel.png"

        val imageFileNames = dto.cargoquery.flatMap {
            listOfNotNull(prefix + it.title.chara + suffix)
        }.distinct()

        val result = source.getImageUrl(imageFileNames)
            .map { urlMap ->
                urlMap.mapKeys { (filename, _) ->
                    filename
                        .removePrefix(prefix)
                        .removeSuffix(suffix)
                }
            }

        return result
    }

    suspend fun resolveImageUrls(
        gameId: String,
        dto: MoveListResponseDto,
    ): Result<Map<String, String>, DataError.Remote> {
        val game = Game.fromId(gameId)

        //MBTL has impossible to decipher filenames, ignore
        if (game == Game.MBTL) return Result.Success(emptyMap())

        val prefix = "Vsav-nav-portrait-"
        val suffix = ".gif"

        val imageFileNames = dto.cargoquery.flatMap {
            listOfNotNull(prefix + it.title.chara.lowercase() + suffix)
        }.distinct()

        val result = source.getImageUrl(imageFileNames)
            .map { urlMap ->
                urlMap.mapKeys { (filename, _) ->
                    filename
                        .removePrefix(prefix)
                        .removeSuffix(suffix)
                }
            }

        return result
    }
}