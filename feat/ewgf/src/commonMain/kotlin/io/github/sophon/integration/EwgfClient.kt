package io.github.sophon.integration

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.integration.model.BattleSet
import io.github.sophon.integration.model.EwgfError
import io.github.sophon.integration.model.Player

interface EwgfClient {
    fun getFeatureInfo(): FeatureInfo

    suspend fun registerPlayer(player: Player): EmptyResult<EwgfError>

    suspend fun downloadBattleData(
        discordId: String,
    ): Result<List<BattleSet>, EwgfError>

    suspend fun updatePolarisId(player: Player): EmptyResult<EwgfError>

    suspend fun deletePlayer(discordId: String): EmptyResult<EwgfError>
}