package io.github.sophon.fightingnerd.feat.quiz.ui.quiz

import io.github.sophon.fightingnerd.feat.quiz.model.Question

internal data class QuizState(
    val enabledCharacterIdList: List<String> = emptyList(),

    val questionList: List<Question> = emptyList(),
    val progress: Progress = Progress(),
) {
    data class Progress(
        val correct: Int = 0,
        val incorrect: Int = 0,
    )
}
