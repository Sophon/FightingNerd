package io.github.sophon.discord.feat.core.domain

import dev.kord.common.entity.Snowflake

internal class CommandRegistry {
    private val map = mutableMapOf<String, Snowflake>()

    fun put(name: String, id: Snowflake) {
        map[name.lowercase()] = id
    }

    operator fun get(name: String): Snowflake? = map[name.lowercase()]
}
