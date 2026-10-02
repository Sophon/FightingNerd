package io.github.sophon.discord.feat.admin

import io.github.sophon.discord.app.domain.model.Command

internal val adminCommands = listOf(
    Command.Reply,
    Command.Ban,
    Command.Unban,
    Command.Banlist,
    Command.Refresh,
)
