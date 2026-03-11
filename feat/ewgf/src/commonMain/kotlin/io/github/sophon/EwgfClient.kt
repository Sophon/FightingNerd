package io.github.sophon

import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.onSuccess
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.domain.Battle
import io.github.sophon.domain.EwgfError
import io.github.sophon.domain.EwgfFeatureInfo
import io.github.sophon.domain.Player
import io.github.sophon.usecase.DeletePlayerUseCase
import io.github.sophon.usecase.DownloadPlayerBattlesUseCase
import io.github.sophon.usecase.RegisterPlayerUseCase
import io.github.sophon.usecase.UpdatePolarisIdUseCase

interface EwgfClient {
    fun getFeatureInfo(): FeatureInfo

    suspend fun registerPlayer(player: Player): EmptyResult<EwgfError>

    suspend fun downloadBattleData(
        discordId: String,
    ): Result<List<Battle>, EwgfError> //TODO: when we get access, replace Unit with proper domain class

    suspend fun updatePolarisId(player: Player): EmptyResult<EwgfError>

    suspend fun deletePlayer(discordId: String): EmptyResult<EwgfError>
}

internal class EwgfClientImpl(
    private val ewgfFeatureInfo: EwgfFeatureInfo,
    private val registerPlayerUseCase: RegisterPlayerUseCase,
    private val downloadPlayerBattlesUseCase: DownloadPlayerBattlesUseCase,
    private val updatePolarisIdUseCase: UpdatePolarisIdUseCase,
    private val deletePlayerUseCase: DeletePlayerUseCase,
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
    ): Result<List<Battle>, EwgfError> {
        return downloadPlayerBattlesUseCase.invoke(discordId)
            .onSuccess { battleList ->
                Napier.d(tag = TAG) {
                    "$discordId: ${battleList.size} downloaded"
                }
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