package io.github.sophon.fightingnerd.feat.quiz.ui.overview

import io.github.sophon.fightingnerd.feat.quiz.model.QuizGameWidget
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

internal data class QuizOverviewState(
    val quizGameWidgetList: ImmutableList<QuizGameWidget> = persistentListOf(),
)
