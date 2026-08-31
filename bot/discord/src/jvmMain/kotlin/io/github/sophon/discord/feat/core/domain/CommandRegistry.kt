package io.github.sophon.discord.feat.core.domain

import dev.kord.common.entity.Snowflake
import io.github.sophon.discord.feat.core.domain.model.Command

internal class CommandRegistry {
    private val map = mutableMapOf<String, Snowflake>()

    fun put(name: String, id: Snowflake) {
        map[name.lowercase()] = id
    }

    fun mention(command: Command): String {
        val name = command.name.lowercase()
        val commandId = map[name]
        val rendered = if (commandId != null) "</$name:${commandId.value}>" else "/$name"
        return rendered
    }
}
