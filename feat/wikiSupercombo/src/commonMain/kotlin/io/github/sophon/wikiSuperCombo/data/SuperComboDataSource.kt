package io.github.sophon.wikiSuperCombo.data

import io.github.sophon.core.domain.DataError
import io.github.sophon.core.domain.Result
import io.github.sophon.core.network.safeCall
import io.github.sophon.wikiSuperCombo.BASE_URL
import io.github.sophon.wikiSuperCombo.LIMIT_CHARACTERS
import io.github.sophon.wikiSuperCombo.TABLE_SF6_CHARACTERS
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

interface SuperComboDataSource {
    suspend fun downloadCharacterList(): Result<CharacterListResponseDto, DataError.Remote>
}

class SuperComboDataSourceImpl(
    private val httpClient: HttpClient,
): SuperComboDataSource {
    override suspend fun downloadCharacterList(): Result<CharacterListResponseDto, DataError.Remote> {
        return safeCall {
            httpClient.get(BASE_URL) {
                parameter("action", "cargoquery")
                parameter("tables", TABLE_SF6_CHARACTERS)
                parameter("fields", "_pageName=${getCharacterFields()}")
                parameter("format", "json")
                parameter("limit", LIMIT_CHARACTERS)
            }
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
}