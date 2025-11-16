package io.github.sophon.fightingnerd

import kotlinx.serialization.Serializable

@Serializable
sealed class Destination {

    @Serializable
    object Home

    @Serializable
    data class MoveList(
        val charName: String,
        val wikiQualifier: String,
    )

    @Serializable
    object Settings
}