package io.github.sophon.discord.util

import io.github.sophon.discord.featureRegistry.Command

internal val ADMIN_COMMANDS = listOf(
    Command.BAN,
    Command.UNBAN,
    Command.BANLIST,
    Command.REPLY,
)