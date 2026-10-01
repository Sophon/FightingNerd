package io.github.sophon.discord.app.domain.service

import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.URL_STEAM_LOBBY
import io.github.sophon.discord.app.domain.model.BotResponse
import io.github.sophon.discord.app.domain.model.UserRequest
import io.github.sophon.discord.app.port.outbound.FrameDataPort
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.Command

internal class CommandRouterService(
    private val frameDataPort: FrameDataPort,
) {
    suspend operator fun invoke(userRequest: UserRequest): Result<BotResponse, BotError> {
        val result = when (val command = resolveCommand(userRequest)) {
            Command.Fd -> frameDataPort.getFrameData(userRequest.query)

            Command.Tip,
            Command.Donate,
            Command.Repo,
            Command.Invite,
            Command.Help,
            Command.Modules,
            Command.Commands,
            Command.Join,
            Command.Feedback,
            Command.Reply,
            Command.Ban,
            Command.Unban,
            Command.Banlist,
            Command.Refresh,
            Command.Char,
            Command.Alias,
            Command.Startup,
            Command.OnHit,
            Command.OnBlock,
            Command.OnCounter,
            Command.Gl,
            Command.Pc,
            Command.Heat,
            Command.Homing,
            Command.Stance,
            Command.ThrowTK,
            Command.Strings,
            Command.SpecialROA,
            Command.Ewgf -> Result.Error(BotError.NotImplemented(command.name))
        }

        return result
    }

    private fun resolveCommand(userRequest: UserRequest): Command {
        val command = if (userRequest.query.startsWith(URL_STEAM_LOBBY, ignoreCase = true)) {
            Command.Join
        } else {
            userRequest.command ?: Command.Fd
        }

        return command
    }
}
