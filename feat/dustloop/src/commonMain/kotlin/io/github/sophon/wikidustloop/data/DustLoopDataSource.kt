package io.github.sophon.wikidustloop.data

import io.github.sophon.core.domain.DataError
import io.github.sophon.core.domain.Result
import io.github.sophon.core.network.safeCall
import io.github.sophon.core.wiki.util.getWikiImageUrl
import io.github.sophon.wikidustloop.BASE_URL
import io.github.sophon.wikidustloop.LIMIT_CHARACTERS
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal interface DustLoopDataSource {
    suspend fun downloadCharacterList(table: String): Result<CharacterListResponseDto, DataError.Remote>
//    suspend fun downloadMoveList(table: String, charName: String): Result<, DataError.Remote>
    suspend fun getImageUrl(fileNames: List<String>): Result<Map<String, String>, DataError.Remote>
}

internal class DustLoopDataSourceImpl(
    private val httpClient: HttpClient,
): DustLoopDataSource {
    override suspend fun downloadCharacterList(table: String): Result<CharacterListResponseDto, DataError.Remote> {
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

    override suspend fun getImageUrl(
        fileNames: List<String>
    ): Result<Map<String, String>, DataError.Remote> {
        return getWikiImageUrl(
            httpClient = httpClient,
            fileNames = fileNames,
            url = BASE_URL,
        )
    }


    private fun getCharacterFields(): String {
        val allFields = listOf(
            "name",
            "defense",
            "guts",
            "guardBalance",
            "prejump",
            "backdash",
            "backdashDuration",
            "backdashInvuln",
            "backdashAirborne",
            "backdashDistance",
            "forwarddash",
            "umo",
            "jump_duration",
            "high_jump_duration",
            "jump_height",
            "high_jump_height",
            "earliest_iad",
            "ad_duration",
            "abd_duration",
            "ad_distance",
            "abd_distance",
            "movement_tension",
            "jump_tension",
            "airdash_tension",
            "walk_speed",
            "back_walk_speed",
            "dash_initial_speed",
            "dash_acceleration",
            "dash_friction",
            "jump_gravity",
            "high_jump_gravity",
            "boost_attack",
            "boost_defense",
            "portrait",
            "icon",
            "nav_image"
        )

        return allFields.joinToString(",")
    }
}