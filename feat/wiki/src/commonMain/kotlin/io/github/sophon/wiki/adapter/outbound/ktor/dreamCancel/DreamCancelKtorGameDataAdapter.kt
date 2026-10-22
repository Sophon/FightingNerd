package io.github.sophon.wiki.adapter.outbound.ktor.dreamCancel

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.core.network.safeCall
import io.github.sophon.wiki.adapter.outbound.ktor.CargoTable
import io.github.sophon.wiki.adapter.outbound.ktor.dreamCancel.DreamCancelKtorGameDataAdapter.Companion.MAX_PAGES
import io.github.sophon.wiki.adapter.outbound.ktor.getWikiImageUrl
import io.github.sophon.wiki.app.outPort.FetchGameDataPort
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.Move
import io.github.sophon.wiki.model.wiki.Game
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList

@OptIn(ExperimentalCoroutinesApi::class)
internal class DreamCancelKtorGameDataAdapter(
    private val httpClient: HttpClient,
) : FetchGameDataPort {

    override suspend fun fetchCharacterList(
        game: Game,
    ): Result<List<Character>, DataError.Remote> {
        return Result.Error(DataError.Remote.PAGE_NOT_FOUND)
    }

    override suspend fun fetchMoveList(
        character: Character,
    ): Result<List<Move>, DataError.Remote> {
        return Result.Error(DataError.Remote.PAGE_NOT_FOUND)
    }

    override suspend fun fetchGameData(
        game: Game,
    ): Result<List<Pair<Character, List<Move>>>, DataError.Remote> {
        val table = DreamCancelTables.moveTableByGame[game]
            ?: return Result.Error(DataError.Remote.PAGE_NOT_FOUND)

        val gameDataResult = downloadMoveTable(table)
            .flatMap { dto ->
                resolveHitboxUrls(dto).flatMap { hitboxUrlMap ->
                    val charaList = dto.cargoQuery.map { query -> query.title.chara }
                    resolveCharacterIconUrls(game, charaList)
                        .map { iconUrlMap -> dto.toDomainAll(game, iconUrlMap, hitboxUrlMap) }
                }
            }
        return gameDataResult
    }

    /**
     * Downloads [MAX_PAGES] pages in parallel, then joins them in order up to the first page that isn't full.
     */
    private suspend fun downloadMoveTable(
        table: CargoTable,
    ): Result<DreamCancelMoveListResponseDto, DataError.Remote> {
        val pageResultList = (0 until MAX_PAGES)
            .asFlow()
            .flatMapMerge(concurrency = MAX_CONCURRENT_PAGES) { page ->
                flow {
                    val offset = (page * LIMIT_MOVES)
                    val pageResult = safeCall<DreamCancelMoveListResponseDto> {
                        httpClient.get(BASE_URL) {
                            parameter("action", "cargoquery")
                            parameter("tables", table.name)
                            parameter("fields", table.fieldList.joinToString(","))
                            parameter("format", "json")
                            parameter("limit", LIMIT_MOVES)
                            parameter("offset", offset)
                        }
                    }
                    emit(offset to pageResult)
                }
            }
            .toList()
            .sortedBy { (offset, _) -> offset }

        val moveTitleList = mutableListOf<Title>()
        for ((_, pageResult) in pageResultList) {
            when (pageResult) {
                is Result.Error -> return pageResult
                is Result.Success -> {
                    val pageMoveTitleList = pageResult.data.cargoQuery
                    moveTitleList.addAll(pageMoveTitleList)
                    if (pageMoveTitleList.size < LIMIT_MOVES) break
                }
            }
        }

        val moveTable = DreamCancelMoveListResponseDto(cargoQuery = moveTitleList)
        return Result.Success(moveTable)
    }

    private suspend fun resolveHitboxUrls(
        dto: DreamCancelMoveListResponseDto,
    ): Result<Map<String, String>, DataError.Remote> {
        val fileNameList = dto.cargoQuery
            .asSequence()
            .flatMap { query -> listOfNotNull(query.title.hitboxes, query.title.images) }
            .flatMap { fileNames -> fileNames.split(",") }
            .map { fileName -> fileName.trim() }
            .filter { fileName -> fileName.isNotEmpty() }
            .distinct()
            .toList()

        val hitboxUrlResult = getWikiImageUrl(
            httpClient = httpClient,
            url = BASE_URL,
            fileNames = fileNameList,
        )
        return hitboxUrlResult
    }

    /**
     * Returns icon URLs keyed the way the character mapper looks them up - by the first and the last word of `chara`.
     */
    private suspend fun resolveCharacterIconUrls(
        game: Game,
        charaList: List<String>,
    ): Result<Map<String, String>, DataError.Remote> {
        val fileNameByKey = charaList.distinct().toIconFileNameByKey(game)

        val iconUrlResult = getWikiImageUrl(
            httpClient = httpClient,
            url = BASE_URL,
            fileNames = fileNameByKey.values.distinct(),
        )
            .map { urlByFileName ->
                fileNameByKey
                    .mapNotNull { (key, fileName) -> urlByFileName[fileName]?.let { url -> key to url } }
                    .toMap()
            }
        return iconUrlResult
    }

    private fun List<String>.toIconFileNameByKey(game: Game): Map<String, String> {
        val keyList = flatMap { chara -> listOf(chara.substringBefore(" "), chara.substringAfterLast(" ")) }
        val fileNameByKey = when (game) {
            Game.KoFXV -> keyList.associateWith { key -> "KOFXV_${key}_Portrait.png" }
            Game.COTW -> keyList.associateWith { key -> "FF_COTW_${key}_Icon.png" }
            else -> emptyMap()
        }
        return fileNameByKey
    }


    private companion object {
        const val BASE_URL = "https://dreamcancel.com/w/api.php"
        const val LIMIT_MOVES = 500
        const val MAX_PAGES = 10
        const val MAX_CONCURRENT_PAGES = 5
    }
}
