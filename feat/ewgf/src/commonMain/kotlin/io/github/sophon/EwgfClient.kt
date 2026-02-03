package io.github.sophon

import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.onSuccess
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.domain.EwgfError
import io.github.sophon.domain.EwgfFeatureInfo
import io.github.sophon.domain.Player
import io.github.sophon.usecase.DeletePlayerUseCase
import io.github.sophon.usecase.RegisterPlayerUseCase
import io.github.sophon.usecase.UpdatePolarisIdUseCase

interface EwgfClient {
    fun getFeatureInfo(): FeatureInfo

    fun init(apiToken: String): EmptyResult<EwgfError>

    suspend fun registerPlayer(player: Player): EmptyResult<EwgfError>

    suspend fun fetchData(
        discordId: String,
    ): Result<Unit, EwgfError> //TODO: when we get access, replace Unit with proper domain class

    suspend fun updatePolarisId(
        discordId: String,
        polarisId: String,
    ): EmptyResult<EwgfError>

    suspend fun deletePlayer(discordId: String): EmptyResult<EwgfError>
}

internal class EwgfClientImpl(
    private val ewgfFeatureInfo: EwgfFeatureInfo,
    private val registerPlayerUseCase: RegisterPlayerUseCase,
    private val updatePolarisIdUseCase: UpdatePolarisIdUseCase,
    private val deletePlayerUseCase: DeletePlayerUseCase,
): EwgfClient {
    private lateinit var apiToken: String

    override fun getFeatureInfo(): FeatureInfo {
        return ewgfFeatureInfo.featureInfo
    }

    override fun init(apiToken: String): EmptyResult<EwgfError> {
        this.apiToken = apiToken
        return Result.Success(Unit)
    }

    override suspend fun registerPlayer(player: Player): EmptyResult<EwgfError> {
        return registerPlayerUseCase.invoke(player)
            .onSuccess {
                Napier.i(tag = TAG) { "registered: $player" }
            }
    }

    override suspend fun fetchData(discordId: String): Result<Unit, EwgfError> {
        TODO("Not yet implemented")
    }

    override suspend fun updatePolarisId(
        discordId: String,
        polarisId: String,
    ): EmptyResult<EwgfError> {
        return updatePolarisIdUseCase.invoke(
            discordId = discordId,
            polarisId = polarisId,
        ).onSuccess {
            Napier.i(tag = TAG) { "updated: $discordId - $polarisId" }
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