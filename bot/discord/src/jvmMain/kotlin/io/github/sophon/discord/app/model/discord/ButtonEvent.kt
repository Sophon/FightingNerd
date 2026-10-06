package io.github.sophon.discord.app.model.discord

import io.github.sophon.discord.app.model.frameData.MoveId

sealed interface ButtonEvent {
    data class Expand(val moveId: MoveId): ButtonEvent

    data class Query(val moveId: MoveId): ButtonEvent

    data class Text(val text: String): ButtonEvent

    data class Forward(
        val sourceChannelId: String,
        val sourceMessageId: String,
        val targetChannelId: String,
    ): ButtonEvent

    data class Command(
        val command: io.github.sophon.discord.app.model.discord.Command,
        val query: String,
    ): ButtonEvent
}