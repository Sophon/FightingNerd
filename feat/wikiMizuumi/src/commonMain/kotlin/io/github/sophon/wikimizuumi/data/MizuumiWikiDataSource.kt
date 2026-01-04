package io.github.sophon.wikimizuumi.data

import io.github.sophon.core.domain.DataError
import io.github.sophon.core.domain.Result
import io.github.sophon.core.network.safeCall
import io.github.sophon.core.wiki.util.getWikiImageUrl
import io.github.sophon.wikimizuumi.BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList

interface MizuumiWikiDataSource {
    suspend fun downloadData(table: String): Result<MoveListResponseDto, DataError.Remote>
    suspend fun getImageUrl(fileNames: List<String>): Result<Map<String, String>, DataError.Remote>
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
            val cargoQueries = (result as Result.Success).data.cargoquery
            allCargoQueries.addAll(cargoQueries)

            //page with less data than max per page -> finished
            if (cargoQueries.size < NO_MAX_MOVES) break
        }

        return Result.Success(MoveListResponseDto(cargoquery = allCargoQueries))
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


    private fun getDataFields(table: String): String {
        val allFields = listOf(
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
        
        return allFields.joinToString(",")
    }

    private companion object {
        const val NO_MAX_PAGES = 10
        const val NO_MAX_MOVES = 500
        const val NO_MAX_CONCURRENT = 5
    }
}