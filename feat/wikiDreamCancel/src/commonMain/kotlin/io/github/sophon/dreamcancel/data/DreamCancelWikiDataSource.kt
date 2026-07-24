package io.github.sophon.dreamcancel.data

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.network.safeCall
import io.github.sophon.core.wiki.util.getWikiImageUrl
import io.github.sophon.dreamcancel.domain.BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList

internal interface DreamCancelWikiDataSource {
    suspend fun downloadData(table: String): Result<MoveListResponseDto, DataError.Remote>
    suspend fun resolveHitboxUrls(dto: MoveListResponseDto): Result<Map<String, String>, DataError.Remote>
    suspend fun resolveCharacterImageUrls(
        gameId: String,
        dto: MoveListResponseDto,
    ): Result<Map<String, String>, DataError.Remote>
}

@OptIn(ExperimentalCoroutinesApi::class)
internal class DreamCancelWikiDataSourceImpl(
    private val httpClient: HttpClient
): DreamCancelWikiDataSource {
    override suspend fun downloadData(
        table: String,
    ): Result<MoveListResponseDto, DataError.Remote> {
        //list of <offset;Result>
        val results = flow {
            repeat(NO_MAX_PAGES) { page ->
                emit(page * NO_MAX_MOVES)
            }
        }
            .flatMapMerge(concurrency = NO_MAX_CONCURRENT) { offset ->
                flow {
                    val result = safeCall<MoveListResponseDto> {
                        httpClient.get(BASE_URL) {
                            parameter("action", "cargoquery")
                            parameter("tables", table)
                            parameter("fields", getDataFields(table))
                            parameter("format", "json")
                            parameter("limit", NO_MAX_MOVES)
                            parameter("offset", offset)
                        }
                    }
                    emit(offset to result)
                }
            }
            .toList()

        val firstError = results.firstOrNull { it.second is Result.Error }
        if (firstError != null) {
            return firstError.second as Result.Error
        }

        val allCargoQueries = mutableListOf<Title>()
        val resultsSortedByOffset = results.sortedBy { it.first }

        for ((_, result) in resultsSortedByOffset) {
            val cargoQueries = (result as Result.Success).data.cargoQuery
            allCargoQueries.addAll(cargoQueries)

            //page with less data than max per page -> finished
            if (cargoQueries.size < NO_MAX_MOVES) break
        }

        return Result.Success(MoveListResponseDto(cargoQuery = allCargoQueries))
    }

    override suspend fun resolveHitboxUrls(
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
        val result = getImageUrl(imageFileNames)
        return result
    }

    override suspend fun resolveCharacterImageUrls(
        gameId: String,
        dto: MoveListResponseDto,
    ): Result<Map<String, String>, DataError.Remote> {
        val game = Game.fromId(gameId)
        val result = when (game) {
            Game.KoFXV -> resolveKof15CharacterImageUrls(dto)
            Game.COTW -> resolveCotwCharacterImageUrls(dto)
            else -> Result.Success(emptyMap())
        }
        return result
    }

    private suspend fun resolveKof15CharacterImageUrls(
        dto: MoveListResponseDto,
    ): Result<Map<String, String>, DataError.Remote> {
        val imageFileNames = dto.cargoQuery
            .flatMap {
                val chara = it.title.chara
                listOf(chara.substringBefore(" "), chara.substringAfterLast(" "))
            }
            .map { buildKof15PortraitFileName(it) }
            .distinct()
        val result = getImageUrl(imageFileNames)
            .map { urlMap ->
                urlMap.mapKeys { (filename, _) -> stripKof15PortraitFileName(filename) }
            }
        return result
    }

    private fun buildKof15PortraitFileName(token: String): String {
        return KOF15_PORTRAIT_PREFIX + token + KOF15_PORTRAIT_SUFFIX
    }

    private fun stripKof15PortraitFileName(filename: String): String {
        return filename.removePrefix(KOF15_PORTRAIT_PREFIX).removeSuffix(KOF15_PORTRAIT_SUFFIX)
    }

    private suspend fun resolveCotwCharacterImageUrls(
        dto: MoveListResponseDto,
    ): Result<Map<String, String>, DataError.Remote> {
        val imageFileNames = dto.cargoQuery
            .flatMap {
                val chara = it.title.chara
                listOf(chara.substringBefore(" "), chara.substringAfterLast(" "))
            }
            .map { buildCotwIconFileName(it) }
            .distinct()
        val result = getImageUrl(imageFileNames)
            .map { urlMap ->
                urlMap.mapKeys { (filename, _) -> stripCotwIconFileName(filename) }
            }
        return result
    }

    private fun buildCotwIconFileName(token: String): String {
        return COTW_ICON_PREFIX + token + COTW_ICON_SUFFIX
    }

    private fun stripCotwIconFileName(filename: String): String {
        return filename.removePrefix(COTW_ICON_PREFIX).removeSuffix(COTW_ICON_SUFFIX)
    }


    private suspend fun getImageUrl(
        fileNames: List<String>,
    ): Result<Map<String, String>, DataError.Remote> {
        val result = getWikiImageUrl(
            httpClient = httpClient,
            fileNames = fileNames,
            url = BASE_URL,
        )
        return result
    }


    private companion object {
        const val NO_MAX_PAGES = 10
        const val NO_MAX_MOVES = 500
        const val NO_MAX_CONCURRENT = 5
        const val KOF15_PORTRAIT_PREFIX = "KOFXV_"
        const val KOF15_PORTRAIT_SUFFIX = "_Portrait.png"
        const val COTW_ICON_PREFIX = "FF_COTW_"
        const val COTW_ICON_SUFFIX = "_Icon.png"
    }
}


private fun getDataFields(table: String): String {
    val allFields = mutableListOf(
        "chara",
        "moveId",
        "name",
        "idle",
        "rank",
        "input",
        "images",
        "hitboxes",
        "damage",
        "guard",
        "cancel",
        "startup",
        "active",
        "recovery",
        "hitadv",
        "blockadv",
        "invul",
        "guardDamage",
    )

    when(table) {
        DreamCancelTables.TABLE_COTW_MOVES -> allFields.add("revdamage")
        DreamCancelTables.TABLE_KOF15_MOVES -> allFields.add("stun")
    }

    return allFields.joinToString(",")
}