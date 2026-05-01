package io.github.sophon.dreamcancel.data

import io.github.sophon.core.domain.DataError
import io.github.sophon.core.domain.Result
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

interface DreamCancelWikiDataSource {
    suspend fun downloadData(table: String): Result<MoveListResponseDto, DataError.Remote>
    suspend fun getImageUrl(fileNames: List<String>): Result<Map<String, String>, DataError.Remote>
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

    override suspend fun getImageUrl(
        fileNames: List<String>
    ): Result<Map<String, String>, DataError.Remote> {
        return getWikiImageUrl(
            httpClient = httpClient,
            fileNames = fileNames,
            url = BASE_URL,
        )
    }


    private companion object {
        const val NO_MAX_PAGES = 10
        const val NO_MAX_MOVES = 500
        const val NO_MAX_CONCURRENT = 5
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