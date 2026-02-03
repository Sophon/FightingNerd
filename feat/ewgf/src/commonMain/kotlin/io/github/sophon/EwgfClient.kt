package io.github.sophon

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.domain.EwgfError
import io.github.sophon.domain.EwgfFeatureInfo

interface EwgfClient {
    fun getFeatureInfo(): FeatureInfo

    fun init(apiToken: String): EmptyResult<EwgfError>

    suspend fun registerPlayer(
        discordId: String,
        polarisId: String,
    ): EmptyResult<EwgfError>

    suspend fun fetchData(
        discordId: String,
    ): Result<Unit, EwgfError> //TODO: when we get access, replace Unit with proper domain class

    suspend fun updatePolarisId(polarisId: String): EmptyResult<EwgfError>

    suspend fun deletePlayer(discordId: String): EmptyResult<EwgfError>
}

internal class EwgfClientImpl(
    private val ewgfFeatureInfo: EwgfFeatureInfo,
): EwgfClient {
    private lateinit var apiToken: String

    override fun getFeatureInfo(): FeatureInfo {
        return ewgfFeatureInfo.featureInfo
    }

    override fun init(apiToken: String): EmptyResult<EwgfError> {
        this.apiToken = apiToken
        return Result.Success(Unit)
    }

    override suspend fun registerPlayer(
        discordId: String,
        polarisId: String,
    ): EmptyResult<EwgfError> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchData(discordId: String): Result<Unit, EwgfError> {
        TODO("Not yet implemented")
    }

    override suspend fun updatePolarisId(polarisId: String): EmptyResult<EwgfError> {
        TODO("Not yet implemented")
    }

    override suspend fun deletePlayer(discordId: String): EmptyResult<EwgfError> {
        TODO("Not yet implemented")
    }
}