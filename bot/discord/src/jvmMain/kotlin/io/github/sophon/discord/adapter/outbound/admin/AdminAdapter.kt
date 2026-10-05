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
import io.github.sophon.inPort.BanUserUseCase
import io.github.sophon.inPort.ConfigureAdminToolUseCase
import io.github.sophon.inPort.IsUserAdminUseCase
import io.github.sophon.inPort.IsUserBannedUseCase
import io.github.sophon.inPort.UnbanUserUseCase

internal class AdminAdapter(
    private val configureAdminToolUseCase: ConfigureAdminToolUseCase,
    private val isUserAdminUseCase: IsUserAdminUseCase,
    private val banUserUseCase: BanUserUseCase,
    private val unbanUserUseCase: UnbanUserUseCase,
    private val isUserBannedUseCase: IsUserBannedUseCase,
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
            .map { ban -> ban.toDomain(offender = moderationRequest.offender) }
            .mapError { it.toDomainError() }
        return result
    }

    override suspend fun unban(moderationRequest: ModerationRequest): EmptyResult<BotError> {
        val result = unbanUserUseCase(moderationRequest.toUnbanRequest())
            .mapError { it.toDomainError() }
        return result
    }

    override suspend fun isBanned(userId: String): Result<Boolean, BotError> {
        val result = isUserBannedUseCase(userId)
            .mapError { it.toDomainError() }
        return result
    }
}
