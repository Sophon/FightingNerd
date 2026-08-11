package io.github.sophon.fightingnerd.feat.quiz.ui.overview

import io.github.sophon.core.featureConfig.model.Game

internal data class QuizOverviewState(
    val gameWidgetList: List<GameWidget> = emptyList(),
) {
    data class GameWidget(
        val game: Game,
        val featureName: String,
    )
}
