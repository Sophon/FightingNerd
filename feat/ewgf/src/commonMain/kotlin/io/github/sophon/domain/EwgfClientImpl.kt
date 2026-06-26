package io.github.sophon.domain

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.integration.EwgfClient
import io.github.sophon.integration.EwgfFeatureInfo
import io.github.sophon.integration.model.BattleSet
import io.github.sophon.integration.model.EwgfError
import io.github.sophon.integration.model.Player
import io.github.sophon.usecase.DeletePlayerUseCase
import io.github.sophon.usecase.DownloadPlayerBattlesUseCase
import io.github.sophon.usecase.GroupBySetUseCase
import io.github.sophon.usecase.RegisterPlayerUseCase
import io.github.sophon.usecase.UpdatePolarisIdUseCase

internal class EwgfClientImpl(
    private val ewgfFeatureInfo: EwgfFeatureInfo,
    private val registerPlayerUseCase: RegisterPlayerUseCase,
    private val downloadPlayerBattlesUseCase: DownloadPlayerBattlesUseCase,
    private val updatePolarisIdUseCase: UpdatePolarisIdUseCase,
    private val deletePlayerUseCase: DeletePlayerUseCase,
    private val groupBySetUseCase: GroupBySetUseCase,
): EwgfClient {
    override fun getFeatureInfo(): FeatureInfo {
        return ewgfFeatureInfo.featureInfo
    }

    override suspend fun registerPlayer(player: Player): EmptyResult<EwgfError> {
        return registerPlayerUseCase.invoke(player)
            .onSuccess {
                Napier.i(tag = TAG) { "registered: $player" }
            }
    }

    override suspend fun downloadBattleData(
        discordId: String,
    ): Result<List<BattleSet>, EwgfError> {
        return downloadPlayerBattlesUseCase.invoke(discordId)
            .flatMap { battleList ->
                groupBySetUseCase.invoke(battleList)
            }
            .onSuccess { setList ->
                val setAmount = setList.size
                val battleAmount = setList.sumOf { it.battleList.size }
                Napier.d(tag = TAG) { "$discordId: $setAmount sets, $battleAmount battles downloaded" }
            }
    }

    override suspend fun updatePolarisId(player: Player): EmptyResult<EwgfError> {
        if (player.discordId == null) {
            return Result.Error(EwgfError.LogicError("discord: null"))
        }

        return updatePolarisIdUseCase.invoke(
            discordId = player.discordId,
            polarisId = player.polarisId,
        ).onSuccess {
            Napier.i(tag = TAG) { "updated: ${player.discordId} - ${player.polarisId}" }
        }
    }

    override suspend fun deletePlayer(discordId: String): EmptyResult<EwgfError> {
        return deletePlayerUseCase.invoke(discordId)
            .onSuccess {
                Napier.d(tag = TAG) { "deleted: $discordId" }
            }
    }


    private companion object {
        const val TAG = "EWGF client"
    }
}