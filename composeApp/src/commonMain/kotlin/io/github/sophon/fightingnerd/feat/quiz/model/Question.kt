package io.github.sophon.fightingnerd.feat.quiz.model

import io.github.sophon.core.wiki.model.Move

data class Question(
    val options: List<Move>,
    val correctIndex: Int,
    val answeredIndex: Int? = null,
) {
    init {
        require(options.size == 4)
        require(correctIndex in options.indices)
    }


    val correct: Move get() {
        return options[correctIndex]
    }

    val answered: Move? get() {
        return answeredIndex?.let { options[it] }
    }

    val isCorrect: Boolean? get() {
        return answeredIndex?.let { it == correctIndex }
    }
}
