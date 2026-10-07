package io.github.sophon.fightingnerd.adapter.inbound.quiz

import io.github.sophon.fightingnerd.adapter.inbound.quiz.model.QuizQuestion
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

internal data class QuizGameState(
    val enabledCharacterIdList: ImmutableList<String> = persistentListOf(),
    val questionList: ImmutableList<QuizQuestion> = persistentListOf(),
    val currentQuestionIndex: Int = 0,
    val correct: Int = 0,
    val incorrect: Int = 0,
    val displayFinishDialog: Boolean = false,

    val isLoading: Boolean = false,
) {
    val isLastQuestion: Boolean get() = (currentQuestionIndex == questionList.lastIndex)
    val currentQuestion: QuizQuestion? get() = questionList.getOrNull(currentQuestionIndex)
    val correctAnswerPct: Int get() {
        val total = correct + incorrect
        val pct = if (total > 0) {
            ((correct.toFloat() / total) * 100).toInt()
        } else {
            0
        }
        return pct
    }

    companion object {
        private val armorKingMoves = listOf(
            QuizQuestion.MoveOption(
                id = "armor_king-bad.2,3",
                input = "bad23",
                startup = "i15~i16",
                onBlock = "-7",
                onHit = "+18g",
            ),
            QuizQuestion.MoveOption(
                id = "armor_king-h.ub1",
                input = "h.ub1",
                startup = "i24~25",
                onBlock = "+8",
                onHit = "+60a",
            ),
            QuizQuestion.MoveOption(
                id = "armor_king-b1+2",
                input = "b1+2",
                startup = "i16~17",
                onBlock = "+0",
                onHit = "+4",
                onCH = "+15",
            ),
            QuizQuestion.MoveOption(
                id = "armor_king-1",
                input = "1",
                startup = "i10",
                onBlock = "+1",
                onHit = "+8",
            ),
        )

        val PREVIEW = QuizGameState(
            enabledCharacterIdList = persistentListOf("Armor King"),
            questionList = persistentListOf(
                QuizQuestion(
                    characterName = "Armor King",
                    options = armorKingMoves.toImmutableList(),
                    correctIndex = 0,
                    answeredIndex = 0
                ),
                QuizQuestion(
                    characterName = "Armor King",
                    options = armorKingMoves.toImmutableList(),
                    correctIndex = 1,
                    answeredIndex = 3
                ),
                QuizQuestion(
                    characterName = "Armor King",
                    options = armorKingMoves.toImmutableList(),
                    correctIndex = 2,
                    answeredIndex = 2
                ),
                QuizQuestion(
                    characterName = "Armor King",
                    options = armorKingMoves.toImmutableList(),
                    correctIndex = 3,
                    answeredIndex = null
                ),
            ),
            currentQuestionIndex = 3,
            correct = 2,
            incorrect = 1,
        )
    }
}
