package io.github.sophon.discord.app.outPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.response.BotResponse
import io.github.sophon.discord.app.model.response.EwgfResponse

internal interface EwgfPort {
    val dataSource: BotResponse.DataSource

    suspend fun getRecentSets(discordId: String): Result<EwgfResponse.RecentSets, BotError>
    suspend fun register(discordId: String, polarisId: String): EmptyResult<BotError>
    suspend fun updatePolarisId(discordId: String, polarisId: String): EmptyResult<BotError>
    suspend fun unregister(discordId: String): EmptyResult<BotError>
}
