package io.github.sophon.discord.app.model

sealed interface ButtonEvent {
    data class Expand(val moveId: MoveId): ButtonEvent

    data class Query(val moveId: MoveId): ButtonEvent

    data class Text(val text: String): ButtonEvent

    data class Forward(val channelId: String): ButtonEvent

    data class Command(
        val command: io.github.sophon.discord.app.model.Command,
        val query: String,
    ): ButtonEvent
}
