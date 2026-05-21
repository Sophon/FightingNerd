package io.github.sophon.fightingnerd.feat.home.ui

import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.feature.Game
import io.github.sophon.core.wiki.domain.model.Character

internal data class HomeViewState(
    val gameWidgetList: List<WikiWidget> = emptyList(),

    val error: String? = null,
) {
    data class WikiWidget(
        val game: Game,
        val featureInfo: FeatureInfo,
        val characterList: List<Character> = emptyList(),
        val isExpanded: Boolean = false,
        val isLoading: Boolean = true,
    )
}
