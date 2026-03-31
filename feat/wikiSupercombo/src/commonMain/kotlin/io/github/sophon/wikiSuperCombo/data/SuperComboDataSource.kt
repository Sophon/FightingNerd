package io.github.sophon.wikiSuperCombo.data

import io.github.sophon.core.domain.DataError
import io.github.sophon.core.domain.Result
import io.github.sophon.core.network.safeCall
import io.github.sophon.core.wiki.usecase.DownloadMoveListUseCase
import io.github.sophon.core.wiki.util.getWikiImageUrl
import io.github.sophon.wikiSuperCombo.BASE_URL
import io.github.sophon.wikiSuperCombo.LIMIT_CHARACTERS
import io.github.sophon.wikiSuperCombo.LIMIT_MOVES
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal interface SuperComboDataSource {
    suspend fun downloadCharacterList(table: String): Result<CharacterListResponseDto, DataError.Remote>
    suspend fun downloadMoveList(
        table: String,
        characterData: DownloadMoveListUseCase.CharacterData,
    ): Result<MoveListResponseDto, DataError.Remote>
    suspend fun getImageUrl(fileNames: List<String>): Result<Map<String, String>, DataError.Remote>
}

internal class SuperComboDataSourceImpl(
    private val httpClient: HttpClient,
): SuperComboDataSource {
    override suspend fun downloadCharacterList(
        table: String,
    ): Result<CharacterListResponseDto, DataError.Remote> {
        return safeCall {
            httpClient.get(BASE_URL) {
                parameter("action", "cargoquery")
                parameter("tables", table)
                parameter("limit", LIMIT_CHARACTERS)
                parameter("format", "json")
                parameter("fields", "_pageName=${getCharacterFields(table)}")
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
            SuperComboTables.TABLE_MK1_CHARACTERS -> {
                listOf(
                    "Character",
                    "chara",
                    "name",
                    "portrait",
                    "icon",
                    "hp",
                    "hpmod",
                    "throwdmg"
                )
            }
            SuperComboTables.TABLE_SF6_CHARACTERS -> {
                listOf(
                    "Character",
                    "chara",
                    "name",
                    "portrait",
                    "icon",
                    "hp",
                    "throwRange",
                    "throwHurtbox",
                    "fwdWalkSpd",
                    "bwdWalkSpd",
                    "fwdDashSpd",
                    "bwdDashSpd",
                    "fwdDashDist",
                    "bwdDashDist",
                    "jumpSpd",
                    "jumpApex",
                    "fwdJumpDist",
                    "bwdJumpDist",
                    "dRushMin",
                    "dRushBlock",
                    "dRushMax"
                )
            }
            SuperComboTables.TABLE_AVL_CHARACTERS -> {
                listOf(
                    "Character",
                    "chara",
                    "portrait",
                    "icon",
                    "hp"
                )
            }
            else -> emptyList()
        }

        return allFields.joinToString(",")
    }

    private fun getMoveFields(table: String): String {
        val allFields = when (table) {
            SuperComboTables.TABLE_MK1_MOVE_LIST -> {
                listOf(
                    "moveId",
                    "moveType",
                    "chara",
                    "input",
                    "name",
                    "images",
                    "hitboxes",
                    "cost",
                    "damage",
                    "chip",
                    "startup",
                    "active",
                    "recovery",
                    "invuln",
                    "hitAdv",
                    "blockAdv",
                    "flawlessBlockAdv",
                    "hitCancelAdv",
                    "blockCancelAdv",
                    "guard",
                    "cancel",
                    "punish",
                    "notes"
                )
            }
            SuperComboTables.TABLE_SF6_MOVE_LIST -> {
                listOf(
                    "moveId",
                    "moveType",
                    "chara",
                    "input",
                    "name",
                    "images",
                    "hitboxes",
                    "damage",
                    "chip",
                    "dmgScaling",
                    "startup",
                    "active",
                    "recovery",
                    "total",
                    "guard",
                    "cancel",
                    "hitconfirm",
                    "hitAdv",
                    "blockAdv",
                    "punishAdv",
                    "perfParryAdv",
                    "DRcancelHit",
                    "DRcancelBlk",
                    "afterDRHit",
                    "afterDRBlk",
                    "hitstun",
                    "blockstun",
                    "hitstop",
                    "driveDmgBlk",
                    "driveDmgHit",
                    "driveGain",
                    "superGainHit",
                    "superGainBlk",
                    "invuln",
                    "armor",
                    "airborne",
                    "jugStart",
                    "jugIncrease",
                    "jugLimit",
                    "projSpeed",
                    "atkRange",
                    "notes"
                )
            }
            SuperComboTables.TABLE_AVL_MOVE_LIST -> {
                listOf(
                    "moveId",
                    "chara",
                    "input",
                    "moveType",
                    "name",
                    "images",
                    "hitboxes",
                    "damage",
                    "chiDamage",
                    "startup",
                    "active",
                    "recovery",
                    "onBlock",
                    "onHit",
                    "guard",
                    "flow",
                    "invuln",
                    "cancel",
                    "properties"
                )
            }
            else -> listOf()
        }

        return allFields.joinToString(",")
    }
}