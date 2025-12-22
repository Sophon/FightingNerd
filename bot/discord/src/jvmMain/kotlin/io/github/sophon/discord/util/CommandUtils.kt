package io.github.sophon.discord.util

import io.github.sophon.discord.domain.Command

internal val ADMIN_COMMANDS = listOf(
    Command.BAN,
    Command.UNBAN,
    Command.BANLIST,
    Command.REPLY,
)