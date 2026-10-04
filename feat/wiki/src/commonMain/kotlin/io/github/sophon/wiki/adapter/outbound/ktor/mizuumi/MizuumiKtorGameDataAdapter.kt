package io.github.sophon.wiki.adapter.outbound.ktor.mizuumi

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.core.network.safeCall
import io.github.sophon.wiki.application.domain.model.Character
import io.github.sophon.wiki.application.domain.model.Move
import io.github.sophon.core.wiki.util.getWikiImageUrl
import io.github.sophon.wiki.adapter.outbound.ktor.CargoTable
import io.github.sophon.wiki.application.domain.model.wiki.Game
import io.github.sophon.wiki.application.port.outbound.FetchGameDataPort
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList

/**
 * Mizuumi has both remote shapes - [Game.separateCharMoveDownload] decides which one a game uses.
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal class MizuumiKtorGameDataAdapter(
    private val httpClient: HttpClient,
) : FetchGameDataPort {

    override fun fetch(
        game: Game,
    ): Flow<Result<Pair<Character, List<Move>>, DataError.Remote>> {
        val flow = if (game.separateCharMoveDownload) {
            fetchSeparate(game)
        } else {
            fetchBulk(game)
        }
        return flow
    }

    private fun fetchSeparate(
        game: Game,
    ): Flow<Result<Pair<Character, List<Move>>, DataError.Remote>> {
        val flow = flow {
            fetchCharacterList(game)
                .onSuccess { characterList ->
                    for (character in characterList) {
                        val characterWithMovesResult = fetchMoveList(game, character)
                            .map { moveList -> character to moveList }
                        emit(characterWithMovesResult)
                    }
                }
                .onError { error -> emit(Result.Error(error)) }
        }
        return flow
    }

    private fun fetchBulk(
        game: Game,
    ): Flow<Result<Pair<Character, List<Move>>, DataError.Remote>> {
        val flow = flow {
            fetchGameData(game)
                .onSuccess { gameData ->
                    for (characterWithMoves in gameData) {
                        emit(Result.Success(characterWithMoves))
                    }
                }
                .onError { error -> emit(Result.Error(error)) }
        }
        return flow
    }

    private suspend fun fetchCharacterList(
        game: Game,
    ): Result<List<Character>, DataError.Remote> {
        val table = MizuumiTables.characterTableByGame[game]
            ?: return Result.Error(DataError.Remote.PAGE_NOT_FOUND)

        val characterListResult = safeCall<MizuumiCharacterListResponseDto> {
            httpClient.get(BASE_URL) {
                parameter("action", "cargoquery")
                parameter("tables", table.name)
                parameter("fields", table.fieldList.joinToString(","))
                parameter("format", "json")
                parameter("limit", LIMIT_CHARACTERS)
            }
        }
            .flatMap { dto ->
                val charaList = dto.cargoquery.map { characterTitle -> characterTitle.title.chara }
                resolveCharacterIconUrls(game, charaList)
                    .map { iconUrlMap -> dto.toDomain(game, iconUrlMap) }
            }
        return characterListResult
    }

    private suspend fun fetchMoveList(
        game: Game,
        character: Character,
    ): Result<List<Move>, DataError.Remote> {
        val table = MizuumiTables.moveTableByGame[game]
            ?: return Result.Error(DataError.Remote.PAGE_NOT_FOUND)

        val moveListResult = safeCall<MizuumiMoveListResponseDto> {
            httpClient.get(BASE_URL) {
                parameter("action", "cargoquery")
                parameter("tables", table.name)
                parameter("fields", table.fieldList.joinToString(","))
                parameter("where", "chara=\"${character.remoteQueryId}\"")
                parameter("format", "json")
                parameter("limit", LIMIT_MOVES)
            }
        }
            .flatMap { dto ->
                resolveHitboxUrls(dto)
                    .map { hitboxUrlMap -> dto.toDomain(game, character, hitboxUrlMap) }
            }
        return moveListResult
    }

    private suspend fun fetchGameData(
        game: Game,
    ): Result<List<Pair<Character, List<Move>>>, DataError.Remote> {
        val table = MizuumiTables.moveTableByGame[game]
            ?: return Result.Error(DataError.Remote.PAGE_NOT_FOUND)

        val gameDataResult = downloadMoveTable(table)
            .flatMap { dto ->
                resolveHitboxUrls(dto).flatMap { hitboxUrlMap ->
                    val charaList = dto.cargoquery.map { moveTitle -> moveTitle.title.chara }
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
    ): Result<MizuumiMoveListResponseDto, DataError.Remote> {
        val pageResultList = (0 until MAX_PAGES)
            .asFlow()
            .flatMapMerge(concurrency = MAX_CONCURRENT_PAGES) { page ->
                flow {
                    val offset = (page * LIMIT_MOVES)
                    val pageResult = safeCall<MizuumiMoveListResponseDto> {
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
                    val pageMoveTitleList = pageResult.data.cargoquery
                    moveTitleList.addAll(pageMoveTitleList)
                    if (pageMoveTitleList.size < LIMIT_MOVES) break
                }
            }
        }

        val moveTable = MizuumiMoveListResponseDto(cargoquery = moveTitleList)
        return Result.Success(moveTable)
    }

    private suspend fun resolveHitboxUrls(
        dto: MizuumiMoveListResponseDto,
    ): Result<Map<String, String>, DataError.Remote> {
        val fileNameList = dto.cargoquery
            .asSequence()
            .flatMap { moveTitle -> listOfNotNull(moveTitle.title.hitboxes, moveTitle.title.images) }
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
     * Returns icon URLs keyed the way the character mappers look them up.
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
        val fileNameByKey = when (game) {
            Game.MBTL -> this
                .flatMap { chara -> listOf(chara.substringBefore(" "), chara.substringAfterLast(" ")) }
                .associate { token -> token.lowercase() to "MBTL_${token.lowercase()}_icon.png" }

            Game.Uni2 -> this.associateWith { chara -> "UNI2_${chara}_CSel.png" }

            Game.VSAV -> this.associate { chara -> chara.lowercase() to "Vsav-nav-portrait-${chara.lowercase()}.gif" }

            else -> emptyMap()
        }
        return fileNameByKey
    }


    private companion object {
        const val BASE_URL = "https://mizuumi.wiki/api.php"
        const val LIMIT_CHARACTERS = 50
        const val LIMIT_MOVES = 500
        const val MAX_PAGES = 10
        const val MAX_CONCURRENT_PAGES = 5
    }
}
