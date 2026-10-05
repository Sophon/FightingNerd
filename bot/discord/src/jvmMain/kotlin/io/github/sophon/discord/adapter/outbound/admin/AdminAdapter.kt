package io.github.sophon.discord.adapter.outbound.admin

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.mapError
import io.github.sophon.discord.app.domain.model.BotError
import io.github.sophon.discord.app.domain.model.BotResponse
import io.github.sophon.discord.app.domain.model.DiscordConfig
import io.github.sophon.discord.app.domain.model.ModerationRequest
import io.github.sophon.discord.app.port.outbound.AdminPort
import io.github.sophon.discord.app.port.outbound.BanPort
import io.github.sophon.discord.app.port.outbound.ConfigureAdminPort
import io.github.sophon.inboundPorts.BanUserUseCase
import io.github.sophon.inboundPorts.ConfigureAdminToolUseCase
import io.github.sophon.inboundPorts.IsUserAdminUseCase
import io.github.sophon.inboundPorts.UnbanUserUseCase

internal class AdminAdapter(
    private val configureAdminToolUseCase: ConfigureAdminToolUseCase,
    private val isUserAdminUseCase: IsUserAdminUseCase,
    private val banUserUseCase: BanUserUseCase,
    private val unbanUserUseCase: UnbanUserUseCase,
): ConfigureAdminPort, AdminPort, BanPort {
    override fun configure(discordConfig: DiscordConfig): EmptyResult<BotError> {
        val result = configureAdminToolUseCase(discordConfig.adminConfig.administratorIdList)
            .mapError { it.toDomainError() }
        return result
    }

    override fun isUserAdmin(userId: String): Result<Boolean, BotError> {
        val result = isUserAdminUseCase(userId)
            .mapError { it.toDomainError() }
        return result
    }

    override suspend fun ban(moderationRequest: ModerationRequest): Result<BotResponse.Ban, BotError> {
        val result = banUserUseCase(moderationRequest.toBanRequest())
            .map { it.toDomain() }
            .mapError { it.toDomainError() }
        return result
    }

    override suspend fun unban(moderationRequest: ModerationRequest): EmptyResult<BotError> {
        val result = unbanUserUseCase(moderationRequest.toUnbanRequest())
            .mapError { it.toDomainError() }
        return result
    }

    override suspend fun isBanned(userId: String): Result<Boolean, BotError> {
        TODO("feat/admin has no IsUserBanned use case yet")
    }
}
