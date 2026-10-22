package io.github.sophon.fightingnerd.adapter.inbound.quiz

import io.github.sophon.core.util.stripMarkdownLinks
import io.github.sophon.fightingnerd.adapter.inbound.quiz.model.QuizQuestion
import io.github.sophon.fightingnerd.app.model.Move
import io.github.sophon.fightingnerd.app.model.Question
import kotlinx.collections.immutable.toImmutableList

internal fun Question.toQuizQuestion(): QuizQuestion {
    val quizQuestion = QuizQuestion(
        characterName = character.displayName,
        options = options.map { move -> move.toMoveOption() }.toImmutableList(),
        correctIndex = correctIndex,
    )
    return quizQuestion
}

private fun Move.toMoveOption(): QuizQuestion.MoveOption {
    val option = QuizQuestion.MoveOption(
        id = input,
        input = input,
        startup = startup,
        onBlock = onBlock?.stripMarkdownLinks(),
        onHit = onHit?.stripMarkdownLinks(),
        onCH = onCH?.stripMarkdownLinks(),
        urls = QuizQuestion.MoveOption.Urls(
            videoUrl = urls.videoUrl,
            hitboxImageList = urls.hitboxImageList.toImmutableList(),
            moveImageList = urls.moveImageList.toImmutableList(),
        ),
    )
    return option
}
