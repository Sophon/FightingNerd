package io.github.sophon.wikidustloop.data

import io.github.sophon.core.domain.DataError
import io.github.sophon.core.domain.Result
import io.github.sophon.core.network.safeCall
import io.github.sophon.core.wiki.usecase.DownloadMoveListUseCase
import io.github.sophon.core.wiki.util.getWikiImageUrl
import io.github.sophon.wikidustloop.BASE_URL
import io.github.sophon.wikidustloop.LIMIT_CHARACTERS
import io.github.sophon.wikidustloop.LIMIT_MOVES
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal interface DustLoopDataSource {
    suspend fun downloadCharacterList(table: String): Result<CharacterListResponseDto, DataError.Remote>
    suspend fun downloadMoveList(
        table: String,
        characterData: DownloadMoveListUseCase.CharacterData,
    ): Result<MoveListResponseDto, DataError.Remote>
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
                parameter("fields", getCharacterFields(table))
            }
        }
    }

    override suspend fun downloadMoveList(
        table: String,
        characterData: DownloadMoveListUseCase.CharacterData,
    ): Result<MoveListResponseDto, DataError.Remote> {
        return safeCall {
            httpClient.get(BASE_URL) {
                parameter("action", "cargoquery")
                parameter("tables", table)
                parameter("limit", LIMIT_MOVES)
                parameter("format", "json")
                parameter("fields", getMoveFields(table))
                parameter("where", "chara='${characterData.name}'")
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


    private fun getCharacterFields(table: String): String {
        val allFields = when (table) {
            DustLoopTables.TABLE_DBFZ_CHARACTERS -> {
                listOf(
                    "name",
                    "portrait",
                    "icon",
                    "kimod",
                    "umo"
                )
            }
            DustLoopTables.TABLE_GGST_CHARACTERS -> {
                listOf(
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
            }
            else -> emptyList()
        }

        return allFields.joinToString(",")
    }

    private fun getMoveFields(table: String): String {
        val allFields = when (table) {
            DustLoopTables.TABLE_DBFZ_MOVE_LIST -> {
                listOf(
                    "chara",
                    "name",
                    "input",
                    "damage",
                    "guard",
                    "startup",
                    "active",
                    "recovery",
                    "onBlock",
                    "attribute",
                    "smash",
                    "kigain",
                    "invuln",
                    "prorate",
                    "level",
                    "blockstun",
                    "groundHit",
                    "airHit",
                    "images",
                    "hitboxes",
                    "type",
                    "notes",
                    "caption",
                    "hitboxCaption"
                )
            }
            DustLoopTables.TABLE_GGST_MOVE_LIST -> {
                listOf(
                    "chara",
                    "name",
                    "input",
                    "damage",
                    "guard",
                    "startup",
                    "active",
                    "recovery",
                    "onBlock",
                    "onHit",
                    "level",
                    "counter",
                    "images",
                    "hitboxes",
                    "notes",
                    "type",
                    "riscGain",
                    "riscLoss",
                    "wallDamage",
                    "inputTension",
                    "chipRatio",
                    "OTGType",
                    "prorate",
                    "invuln",
                    "cancel",
                    "caption",
                    "hitboxCaption"
                )
            }
            else -> emptyList()
        }

        return allFields.joinToString(",")
    }
}
