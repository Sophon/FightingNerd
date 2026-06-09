package io.github.sophon.fightingnerd.navigation

import androidx.navigation3.runtime.NavKey
import io.github.sophon.core.wiki.model.Move
import kotlinx.serialization.Serializable

sealed interface Destination: NavKey {
    @Serializable
    data object Home: Destination

    @Serializable
    data object Search: Destination

    @Serializable
    data object Saved: Destination

    @Serializable
    data object Quiz: Destination

    @Serializable
    data class MoveList(val gameId: String, val characterId: String): Destination

    @Serializable
    data class MoveDetail(val move: Move): Destination

    @Serializable
    data class CharacterDetail(val gameId: String, val characterId: String): Destination
}
