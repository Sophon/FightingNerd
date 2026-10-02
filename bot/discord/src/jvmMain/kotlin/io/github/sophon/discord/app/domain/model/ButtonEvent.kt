package io.github.sophon.discord.app.domain.model

sealed interface ButtonEvent {
    data class Expand(val moveId: MoveId): ButtonEvent

    data class Text(val text: String): ButtonEvent
}
