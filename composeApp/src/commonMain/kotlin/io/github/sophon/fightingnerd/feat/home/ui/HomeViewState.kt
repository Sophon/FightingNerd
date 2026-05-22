package io.github.sophon.fightingnerd.feat.home.ui

import io.github.sophon.core.feature.Game

internal data class HomeViewState(
    val gameWidgetList: List<GameWidget> = emptyList(),

    val error: String? = null,
) {
    data class GameWidget(
        val game: Game,
        val featureName: String,
        val characterList: List<Character> = emptyList(),
        val isExpanded: Boolean = false,
        val isLoading: Boolean = true,
    ) {
        internal data class Character(
            val id: String,
            val displayName: String,
            val queryName: String,
            val iconUrl: String? = null,
        )
    }
}
