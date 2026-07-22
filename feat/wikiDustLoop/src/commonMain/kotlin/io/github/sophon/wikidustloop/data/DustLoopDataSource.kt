package io.github.sophon.wikidustloop.data

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.network.safeCall
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.util.getWikiImageUrl
import io.github.sophon.wikidustloop.domain.BASE_URL
import io.github.sophon.wikidustloop.domain.LIMIT_CHARACTERS
import io.github.sophon.wikidustloop.domain.LIMIT_MOVES
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal interface DustLoopDataSource {
    suspend fun downloadCharacterList(table: String): Result<CharacterListResponseDto, DataError.Remote>
    suspend fun downloadMoveList(table: String, character: Character): Result<MoveListResponseDto, DataError.Remote>
    suspend fun resolveCharacterImageUrls(dto: CharacterListResponseDto): Result<Map<String, String>, DataError.Remote>
    suspend fun resolveHitboxUrls(dto: MoveListResponseDto): Result<Map<String, String>, DataError.Remote>
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
        character: Character,
    ): Result<MoveListResponseDto, DataError.Remote> {
        return safeCall {
            httpClient.get(BASE_URL) {
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
        val imageFileNames = dto.cargoQuery.flatMap {
            listOfNotNull(it.title.icon, it.title.portrait)
        }.distinct()
        val result = getImageUrl(imageFileNames)
        return result
    }

    override suspend fun resolveHitboxUrls(
        dto: MoveListResponseDto,
    ): Result<Map<String, String>, DataError.Remote> {
        val imageFileNames = dto.cargoQuery.flatMap { moveDto ->
            listOfNotNull(moveDto.title.hitboxes, moveDto.title.images)
                .takeIf { it.isNotEmpty() }
                ?.joinToString(";")
                ?.split(";", "\\")
                ?.map { it.trim() }
                ?: emptyList()
        }.distinct()
        val result = getImageUrl(imageFileNames)
        return result
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

    private fun getCharacterFields(table: String): String {
        val allFields = when (table) {
            DustLoopTables.TABLE_DBFZ_CHARACTERS -> {
                listOf(
                    "name",
                    "portrait",
                    "icon",
                    "kimod",
                    "umo",
                    "aliases",
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
            DustLoopTables.TABLE_GBVSR_CHARACTERS -> {
                listOf(
                    "name",
                    "health",
                    "prejump",
                    "backdash",
                    "umo",
                    "portrait",
                    "icon",
                    "walk_speed",
                    "backwalk_speed",
                    "dash_initial_speed",
                    "dash_acceleration",
                    "jump_height",
                    "f_jump_distance",
                    "b_jump_distance",
                    "jump_gravity",
                    "superjump_height",
                    "f_superjump_distance",
                    "b_superjump_distance",
                    "superjump_gravity",
                    "close_l_range",
                    "close_m_range",
                    "close_h_range",
                )
            }
            DustLoopTables.TABLE_BBCF_CHARACTERS -> {
                listOf(
                    "name",
                    "health",
                    "prejump",
                    "backdash",
                    "forwarddash",
                    "umo",
                    "portrait",
                    "icon"
                )
            }
            DustLoopTables.TABLE_MTFS_CHARACTERS -> {
                listOf(
                    "name",
                    "prejump",
                    "backdash",
                    "team",
                    "umo",
                    "portrait",
                    "icon"
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
            DustLoopTables.TABLE_GBVSR_MOVE_LIST -> {
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
                    "onCH",
                    "meter",
                    "level",
                    "invuln",
                    "cooldown",
                    "cls",
                    "images",
                    "caption",
                    "hitboxes",
                    "hitboxCaption",
                    "type",
                    "notes"
                )
            }
            DustLoopTables.TABLE_BBCF_MOVE_LIST -> {
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
                    "onODR",
                    "attribute",
                    "invuln",
                    "cancel",
                    "p1",
                    "p2",
                    "starter",
                    "level",
                    "blockstun",
                    "groundHit",
                    "airHit",
                    "groundCH",
                    "airCH",
                    "blockstop",
                    "hitstop",
                    "CHstop",
                    "cancelTiming",
                    "images",
                    "caption",
                    "hitboxes",
                    "hitboxCaption",
                    "type",
                    "notes"
                )
            }
            DustLoopTables.TABLE_MTFS_MOVE_LIST -> {
                listOf(
                    "chara",
                    "name",
                    "input",
                    "simpleInput",
                    "damage",
                    "guard",
                    "startup",
                    "active",
                    "recovery",
                    "onBlock",
                    "onHit",
                    "level",
                    "type",
                    "prorate",
                    "invuln",
                    "untechAmount",
                    "meterGain",
                    "hitboxCaption",
                    "images",
                    "hitboxes",
                    "notes"
                )
            }
            else -> emptyList()
        }

        return allFields.joinToString(",")
    }
}
