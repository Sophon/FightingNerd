package io.github.sophon.fightingnerd.navigation.domain

import androidx.navigation3.runtime.NavKey
import io.github.sophon.core.wiki.model.Move
import kotlinx.serialization.Serializable

sealed interface Destination : NavKey {
    @Serializable
    data object Home : Destination

    @Serializable
    data object Search : Destination

    @Serializable
    data object Saved : Destination

    @Serializable
    data object QuizOverview : Destination

    @Serializable
    data object More : Destination


    @Serializable
    data class MoveList(val gameId: String, val characterId: String) : Destination

    @Serializable
    data class MoveDetail(val move: Move) : Destination

    @Serializable
    data class CharacterDetail(val gameId: String, val characterId: String) : Destination

    @Serializable
    data class Quiz(val gameId: String) : Destination

    @Serializable
    data object FeatureSettings : Destination
}

internal val rootDestinations: List<Destination> = listOf(
    Destination.Home,
    Destination.QuizOverview,
    Destination.More,
)