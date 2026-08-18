package io.github.sophon.fightingnerd.feat.quiz.ui.overview

import io.github.sophon.fightingnerd.feat.quiz.model.QuizGameWidget

internal data class QuizOverviewState(
    val quizGameWidgetList: List<QuizGameWidget> = emptyList(),
)
