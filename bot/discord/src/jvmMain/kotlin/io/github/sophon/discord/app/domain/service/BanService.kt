package io.github.sophon.discord.app.domain.service

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.discord.app.domain.model.BotError
import io.github.sophon.discord.app.domain.model.BotResponse
import io.github.sophon.discord.app.domain.model.Command
import io.github.sophon.discord.app.domain.model.ModerationRequest
import io.github.sophon.discord.app.domain.model.UserRequest
import io.github.sophon.discord.app.port.outbound.BanPort

internal interface BanService {
    suspend fun ban(
        query: String,
        source: UserRequest.Source?,
    ): Result<BotResponse.Ban, BotError>

    suspend fun unban(
        query: String,
        source: UserRequest.Source?,
    ): Result<BotResponse.Unban, BotError>
}

internal class BanServiceImpl(
    private val banPort: BanPort,
): BanService {
    override suspend fun ban(
        query: String,
        source: UserRequest.Source?,
    ): Result<BotResponse.Ban, BotError> {
        val result = createModerationRequest(command = Command.Ban, query = query, source = source)
            .flatMap { moderationRequest -> banPort.ban(moderationRequest) }
        return result
    }

    override suspend fun unban(
        query: String,
        source: UserRequest.Source?,
    ): Result<BotResponse.Unban, BotError> {
        val result = createModerationRequest(command = Command.Unban, query = query, source = source)
            .flatMap { moderationRequest ->
                val unbanResult = banPort.unban(moderationRequest)
                    .map { BotResponse.Unban(offenderId = moderationRequest.offenderId) }
                unbanResult
            }
        return result
    }

    /**
     * Query is `[username]-[id]-[channelId]` of the offender.
     * Button paths have no [source] to act as the issuer.
     */
    private fun createModerationRequest(
        command: Command,
        query: String,
        source: UserRequest.Source?,
    ): Result<ModerationRequest, BotError> {
        val offender = UserRequest.Source.parse(query)

        val result = when {
            (source == null) -> Result.Error(BotError.BotLogicError(command.name, query))
            (offender == null) -> Result.Error(BotError.InvalidQuery(query))
            else -> Result.Success(ModerationRequest(authorId = source.id, offenderId = offender.id))
        }
        return result
    }
}
