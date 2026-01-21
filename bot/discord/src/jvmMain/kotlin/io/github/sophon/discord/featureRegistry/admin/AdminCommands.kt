package io.github.sophon.discord.featureRegistry.admin

import io.github.sophon.discord.domain.Command

internal val adminCommands = listOf(
    Command.Reply,
    Command.Ban,
    Command.Unban,
    Command.Banlist
)
