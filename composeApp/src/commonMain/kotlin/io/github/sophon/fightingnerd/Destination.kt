package io.github.sophon.fightingnerd

import kotlinx.serialization.Serializable

@Serializable
internal sealed class Destination {

    @Serializable
    object Home

    @Serializable
    data class MoveList(
        val gameId: String,
        val charName: String,
    )

    @Serializable
    object Settings
}