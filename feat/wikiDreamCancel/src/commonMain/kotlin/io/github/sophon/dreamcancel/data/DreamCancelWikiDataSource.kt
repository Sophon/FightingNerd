package io.github.sophon.dreamcancel.data

import io.github.sophon.core.domain.DataError
import io.github.sophon.core.domain.Result
import io.github.sophon.core.network.safeCall
import io.github.sophon.core.wiki.util.getWikiImageUrl
import io.github.sophon.dreamcancel.BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

interface DreamCancelWikiDataSource {
    suspend fun downloadData(table: String): Result<MoveListResponseDto, DataError.Remote>
    suspend fun getImageUrl(fileNames: List<String>): Result<Map<String, String>, DataError.Remote>
}

internal class DreamCancelWikiDataSourceImpl(
    private val httpClient: HttpClient
): DreamCancelWikiDataSource {

    override suspend fun downloadData(
        table: String,
    ): Result<MoveListResponseDto, DataError.Remote> {
        val allTitles = mutableListOf<Title>()
        var offset = 0
        val limit = 500
        val maxPages = 10
        var pageCount = 0

        do {
            if (pageCount >= maxPages) break

            val result = safeCall<MoveListResponseDto> {
                httpClient.get(BASE_URL) {
                    parameter("action", "cargoquery")
                    parameter("tables", table)
                    parameter("fields", getDataFields())
                    parameter("format", "json")
                    parameter("limit", limit)
                    parameter("offset", offset)
                }
            }

            when (result) {
                is Result.Success -> {
                    val moves = result.data.cargoQuery
                    allTitles.addAll(moves)

                    if (moves.size < limit) break

                    offset += limit
                    pageCount++
                }
                is Result.Error -> return result
            }
        } while (true)

        return Result.Success(MoveListResponseDto(cargoQuery = allTitles))
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
}


private fun getDataFields(): String {
    val allFields = listOf(
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
        "stun",
        "guardDamage"
    )
    return allFields.joinToString(",")
}