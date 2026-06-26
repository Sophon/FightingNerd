package io.github.sophon.fightingnerd.feat.quiz.ui.overview

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.fightingnerd.feat.home.ui.HomeViewState

internal data class QuizOverviewState(
    val gameWidgetList: List<HomeViewState.GameWidget> = emptyList(),
)
