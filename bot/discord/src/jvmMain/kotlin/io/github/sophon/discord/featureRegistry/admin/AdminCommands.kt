package io.github.sophon.discord.featureRegistry.admin

import io.github.sophon.discord.domain.Command
import io.github.sophon.discord.domain.SupportedCommand

internal val adminCommands = listOf(
    SupportedCommand(
        command = Command.REPLY,
        description = "Answer feedback",
        arguments = listOf(
            SupportedCommand.Argument(
                name = KEY_REPLY_RECIPIENT,
                description = "username-id-serverId",
            ),
            SupportedCommand.Argument(
                name = KEY_REPLY_MESSAGE,
                description = "Reply",
            )
        )
    ),
    SupportedCommand(
        command = Command.BAN,
        description = "Ban user",
        arguments = listOf(
            SupportedCommand.Argument(
                name = KEY_REPLY_BAN,
                description = "User ID",
            )
        ),
    ),
    SupportedCommand(
        command = Command.UNBAN,
        description = "Unban user",
        arguments = listOf(
            SupportedCommand.Argument(
                name = KEY_REPLY_UNBAN,
                description = "User ID",
            )
        ),
    ),
    SupportedCommand(
        command = Command.BANLIST,
        description = "List of banned users",
        arguments = listOf(),
    )
)

private const val KEY_REPLY_RECIPIENT = "recipient"
private const val KEY_REPLY_MESSAGE = "message"
private const val KEY_REPLY_BAN = "ban"
private const val KEY_REPLY_UNBAN = "unban"