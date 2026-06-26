package io.github.sophon.wikimizuumi.data

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.network.safeCall
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.util.getWikiImageUrl
import io.github.sophon.wikimizuumi.domain.BASE_URL
import io.github.sophon.wikimizuumi.domain.LIMIT_CHARACTERS
import io.github.sophon.wikimizuumi.domain.LIMIT_MOVES
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList

internal interface MizuumiWikiDataSource {
    suspend fun downloadData(table: String): Result<MoveListResponseDto, DataError.Remote>
    suspend fun downloadCharacterList(table: String): Result<CharacterListResponseDto, DataError.Remote>
    suspend fun downloadMoveList(
        table: String,
        character: Character,
    ): Result<MoveListResponseDto, DataError.Remote>
    suspend fun resolveCharacterImageUrls(dto: CharacterListResponseDto): Result<Map<String, String>, DataError.Remote>
    suspend fun resolveHitboxUrls(dto: MoveListResponseDto): Result<Map<String, String>, DataError.Remote>
    suspend fun resolveCharacterImageUrlsFromMoveList(
        gameId: String,
        dto: MoveListResponseDto,
    ): Result<Map<String, String>, DataError.Remote>
}

@OptIn(ExperimentalCoroutinesApi::class)
internal class MizuumiWikiDataSourceImpl(
    private val httpClient: HttpClient,
): MizuumiWikiDataSource {
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
                            parameter("fields", getMoveFields(table))
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
            val cargoQueries = (result as Result.Success).data.cargoquery
            allCargoQueries.addAll(cargoQueries)

            //page with less data than max per page -> finished
            if (cargoQueries.size < NO_MAX_MOVES) break
        }

        return Result.Success(MoveListResponseDto(cargoquery = allCargoQueries))
    }

    override suspend fun downloadCharacterList(
        table: String
    ): Result<CharacterListResponseDto, DataError.Remote> {
        return safeCall {
            httpClient.get(urlString = BASE_URL) {
                parameter("action", "cargoquery")
                parameter("tables", table)
                parameter("limit", LIMIT_CHARACTERS)
                parameter("format", "json")
                parameter("fields", getCharacterFields())
            }
        }
    }

    override suspend fun downloadMoveList(
        table: String,
        character: Character,
    ): Result<MoveListResponseDto, DataError.Remote> {
        return safeCall {
            httpClient.get(urlString = BASE_URL) {
                parameter("action", "cargoquery")
                parameter("tables", table)
                parameter("limit", LIMIT_MOVES)
                parameter("format", "json")
                parameter("fields", getMoveFields(table))
                parameter("where", "chara=\"${character.remoteQueryId}\"")
            }
        }
    }

    override suspend fun resolveCharacterImageUrls(
        dto: CharacterListResponseDto,
    ): Result<Map<String, String>, DataError.Remote> {
        val prefix = "UNI2_"
        val suffix = "_CSel.png"

        val imageFileNames = dto.cargoquery.flatMap {
            listOfNotNull(prefix + it.title.chara + suffix)
        }.distinct()

        val result = getImageUrl(imageFileNames)
            .map { urlMap ->
                urlMap.mapKeys { (filename, _) ->
                    filename
                        .removePrefix(prefix)
                        .removeSuffix(suffix)
                }
            }
        return result
    }

    override suspend fun resolveHitboxUrls(
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
        val result = getImageUrl(imageFileNames)
        return result
    }

    override suspend fun resolveCharacterImageUrlsFromMoveList(
        gameId: String,
        dto: MoveListResponseDto,
    ): Result<Map<String, String>, DataError.Remote> {
        val game = Game.fromId(gameId)

        // MBTL has impossible to decipher filenames, ignore
        if (game == Game.MBTL) {
            return Result.Success(emptyMap())
        }

        val prefix = "Vsav-nav-portrait-"
        val suffix = ".gif"

        val imageFileNames = dto.cargoquery.flatMap {
            listOfNotNull(prefix + it.title.chara.lowercase() + suffix)
        }.distinct()

        val result = getImageUrl(imageFileNames)
            .map { urlMap ->
                urlMap.mapKeys { (filename, _) ->
                    filename
                        .removePrefix(prefix)
                        .removeSuffix(suffix)
                }
            }
        return result
    }


    private fun getCharacterFields(): String {
        val allFields = listOf(
            "chara",
            "smartSteer",
            "health",
            "fWalkSpeed",
            "fWalkSpeedNote",
            "bWalkSpeed",
            "bWalkSpeedNote",
            "jumpStartup",
            "jumpDuration",
            "jumpDurationNote",
            "dashStartup",
            "iDashSpeed",
            "iDashSpeedNote",
            "dashAccel",
            "dashAccelNote",
            "maxDashSpeed",
            "bDashStartup",
            "bDashDuration",
            "bDashDurationNote",
            "bDashDistance",
            "bDashDistanceNote",
            "bDashFullInvulStart",
            "bDashFullInvulEnd",
            "bDashThrowInvulStart",
            "bDashThrowInvulEnd",
            "throwWidth",
            "throwRange",
            "trait",
            "vorpalTrait"
        )

        return allFields.joinToString(",")
    }

    private fun getMoveFields(table: String): String {
        val allFields = when (table) {
            MizuumiTables.TABLE_MBTL_MOVES -> {
                listOf(
                    "moveId",
                    "chara",
                    "input",
                    "inputInfo",
                    "name",
                    "subtitle",
                    "images",
                    "hitboxes",
                    "damage",
                    "minDamage",
                    "guard",
                    "cancel",
                    "property",
                    "cost",
                    "attribute",
                    "startup",
                    "active",
                    "recovery",
                    "landing",
                    "overall",
                    "frameAdv",
                    "invul"
                )
            }

            MizuumiTables.TABLE_UNI2_MOVES -> {
                listOf(
                    "moveId",
                    "chara",
                    "input",
                    "inputInfo",
                    "name",
                    "subtitle",
                    "images",
                    "hitboxes",
                    "damage",
                    "minDamage",
                    "type",
                    "guard",
                    "cancel",
                    "cancelWindow",
                    "property",
                    "cost",
                    "attribute",
                    "startup",
                    "active",
                    "recovery",
                    "landing",
                    "overall",
                    "frameAdv",
                    "onHit",
                    "assaultAdv",
                    "blockstun",
                    "groundHit",
                    "airHit",
                    "groundCH",
                    "airCH",
                    "hitstop",
                    "CHstop",
                    "invul",
                    "proration",
                    "comboP1",
                    "comboP2"
                )
            }

            MizuumiTables.TABLE_VSAV_MOVES -> {
                listOf(
                    "moveId",
                    "chara",
                    "input",
                    "inputInfo",
                    "name",
                    "subtitle",
                    "images",
                    "hitboxes",
                    "totaldmg",
                    "whitedmg",
                    "guard",
                    "startup",
                    "active",
                    "recovery",
                    "advHit",
                    "advBlock",
                    "invul",
                    "cancel",
                    "renda",
                    "meter",
                    "reaction",
                    "cursetime"
                )
            }

            else -> listOf()
        }

        return allFields.joinToString(",")
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
    }
}