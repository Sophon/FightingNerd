package io.github.sophon.discord.adapter.outbound.ewgf

import io.github.sophon.EwgfFeatureInfo
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.mapError
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.response.BotResponse
import io.github.sophon.discord.app.model.response.EwgfResponse
import io.github.sophon.discord.app.outPort.EwgfPort
import io.github.sophon.inPort.GetRecentSetsUseCase
import io.github.sophon.inPort.RegisterPlayerUseCase
import io.github.sophon.inPort.UnregisterPlayerUseCase
import io.github.sophon.inPort.UpdatePolarisIdUseCase
import io.github.sophon.model.Player

internal class EwgfAdapter(
    private val ewgfFeatureInfo: EwgfFeatureInfo,
    private val getRecentSetsUseCase: GetRecentSetsUseCase,
    private val registerPlayerUseCase: RegisterPlayerUseCase,
    private val updatePolarisIdUseCase: UpdatePolarisIdUseCase,
    private val unregisterPlayerUseCase: UnregisterPlayerUseCase,
): EwgfPort {
    override val dataSource: BotResponse.DataSource = ewgfFeatureInfo.featureInfo.toDataSource()

    override suspend fun getRecentSets(discordId: String): Result<EwgfResponse.RecentSets, BotError> {
        val result = getRecentSetsUseCase(discordId)
            .map { setList ->
                setList.toDomain(
                    dataSource = dataSource,
                    featureUrl = ewgfFeatureInfo.featureInfo.url,
                )
            }
            .mapError { it.toDomainError() }
        return result
    }

    override suspend fun register(discordId: String, polarisId: String): EmptyResult<BotError> {
        val result = registerPlayerUseCase(Player(polarisId = polarisId, discordId = discordId))
            .mapError { it.toDomainError() }
        return result
    }

    override suspend fun updatePolarisId(discordId: String, polarisId: String): EmptyResult<BotError> {
        val result = updatePolarisIdUseCase(Player(polarisId = polarisId, discordId = discordId))
            .mapError { it.toDomainError() }
        return result
    }

    override suspend fun unregister(discordId: String): EmptyResult<BotError> {
        val result = unregisterPlayerUseCase(discordId)
            .mapError { it.toDomainError() }
        return result
    }
}
