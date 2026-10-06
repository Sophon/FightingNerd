package io.github.sophon.discord.app.model

import io.github.sophon.discord.app.model.discord.Command

internal val adminCommands = listOf(
    Command.Reply,
    Command.Ban,
    Command.Unban,
    Command.Banlist,
    Command.Refresh,
)
