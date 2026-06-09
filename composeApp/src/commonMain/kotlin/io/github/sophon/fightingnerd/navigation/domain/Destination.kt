package io.github.sophon.fightingnerd.navigation.domain

import androidx.navigation3.runtime.NavKey
import io.github.sophon.core.wiki.model.Move
import kotlinx.serialization.Serializable

sealed interface Destination : NavKey {
    sealed interface TopLevelDestination : Destination

    @Serializable
    data object Home : TopLevelDestination

    @Serializable
    data object Search : TopLevelDestination

    @Serializable
    data object Saved : TopLevelDestination

    @Serializable
    data object Quiz : TopLevelDestination


    @Serializable
    data class MoveList(val gameId: String, val characterId: String) : Destination

    @Serializable
    data class MoveDetail(val move: Move) : Destination

    @Serializable
    data class CharacterDetail(val gameId: String, val characterId: String) : Destination
}