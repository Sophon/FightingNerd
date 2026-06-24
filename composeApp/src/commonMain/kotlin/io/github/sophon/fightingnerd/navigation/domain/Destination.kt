package io.github.sophon.fightingnerd.navigation.domain

import androidx.navigation3.runtime.NavKey
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.bottom_bar_item_character_list
import fightingnerd.composeapp.generated.resources.bottom_bar_item_more
import fightingnerd.composeapp.generated.resources.bottom_bar_item_quiz
import fightingnerd.composeapp.generated.resources.bottom_bar_item_saved
import fightingnerd.composeapp.generated.resources.bottom_bar_item_search
import io.github.sophon.core.wiki.model.Move
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource

sealed interface Destination : NavKey {
    sealed interface TopLevelDestination : Destination {
        val label: StringResource
    }

    @Serializable
    data object Home : TopLevelDestination {
        override val label: StringResource = Res.string.bottom_bar_item_character_list
    }

    @Serializable
    data object Search : TopLevelDestination {
        override val label: StringResource = Res.string.bottom_bar_item_search
    }

    @Serializable
    data object Saved : TopLevelDestination {
        override val label: StringResource = Res.string.bottom_bar_item_saved
    }

    @Serializable
    data object QuizOverview : TopLevelDestination {
        override val label: StringResource = Res.string.bottom_bar_item_quiz
    }

    @Serializable
    data object More : TopLevelDestination {
        override val label: StringResource = Res.string.bottom_bar_item_more
    }


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