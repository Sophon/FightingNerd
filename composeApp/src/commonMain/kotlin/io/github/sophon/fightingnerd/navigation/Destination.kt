package io.github.sophon.fightingnerd.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Destination: NavKey {
    @Serializable
    data object Home: Destination

    @Serializable
    data class MoveList(
        val gameId: String,
        val characterId: String,
    ): Destination
}
