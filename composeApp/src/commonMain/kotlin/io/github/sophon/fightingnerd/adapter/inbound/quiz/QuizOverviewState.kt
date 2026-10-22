package io.github.sophon.fightingnerd.adapter.inbound.quiz

import io.github.sophon.fightingnerd.adapter.inbound.quiz.model.QuizGameWidget
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

internal data class QuizOverviewState(
    val quizGameWidgetList: ImmutableList<QuizGameWidget> = persistentListOf(),
)
