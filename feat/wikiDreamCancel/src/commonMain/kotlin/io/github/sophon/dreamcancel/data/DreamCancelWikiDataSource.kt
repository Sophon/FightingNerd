package io.github.sophon.dreamcancel.data

import io.github.sophon.core.domain.DataError
import io.github.sophon.core.domain.Result
import io.github.sophon.core.network.safeCall
import io.github.sophon.core.wiki.util.getWikiImageUrl
import io.github.sophon.dreamcancel.BASE_URL
import io.github.sophon.dreamcancel.LIMIT_MOVES
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
        return safeCall {
            httpClient.get(BASE_URL) {
                parameter("action", "cargoquery")
                parameter("tables", table)
                parameter("limit", LIMIT_MOVES)
                parameter("format", "json")
                parameter("fields", getDataFields())
            }
        }
    }

    override suspend fun getImageUrl(fileNames: List<String>): Result<Map<String, String>, DataError.Remote> {
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