package io.github.sophon.wikiSuperCombo.data

import io.github.sophon.core.domain.DataError
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.network.safeCall
import io.github.sophon.wikiSuperCombo.BASE_URL
import io.github.sophon.wikiSuperCombo.LIMIT_CHARACTERS
import io.github.sophon.wikiSuperCombo.LIMIT_MOVES
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal interface SuperComboDataSource {
    suspend fun downloadCharacterList(table: String): Result<CharacterListResponseDto, DataError.Remote>
    suspend fun downloadMoveListFor(table: String, charName: String): Result<MoveListResponseDto, DataError.Remote>
    suspend fun getImageUrl(fileName: String): Result<String, DataError.Remote>
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
                parameter("fields", "_pageName=${getCharacterFields()}")
            }
        }
    }

    override suspend fun downloadMoveListFor(
        table: String,
        charName: String
    ): Result<MoveListResponseDto, DataError.Remote> {
        return safeCall {
            httpClient.get(BASE_URL) {
                parameter("action", "cargoquery")
                parameter("tables", table)
                parameter("limit", LIMIT_MOVES)
                parameter("format", "json")
                parameter("fields", getMoveFields())
                parameter("where", "chara='$charName'")
            }
        }
    }

    override suspend fun getImageUrl(fileName: String): Result<String, DataError.Remote> {
        return safeCall<ImageUrlResponseDto> {
            httpClient.get(BASE_URL) {
                parameter("action", "query")
                parameter("titles", "File:$fileName")
                parameter("prop", "imageinfo")
                parameter("iiprop", "url")
                parameter("format", "json")
            }
        }.map { response ->
            response.query.pages.values.firstOrNull()
                ?.imageinfo?.firstOrNull()
                ?.url
                ?: ""
        }
    }

    private fun getCharacterFields(): String {
        val allFields = listOf(
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

        return allFields.joinToString(",")
    }

    private fun getMoveFields(): String {
        val allFields = listOf(
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

        return allFields.joinToString(",")
    }
}