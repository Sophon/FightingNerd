package io.github.sophon.fightingnerd.feat.quiz.ui.quiz

import io.github.sophon.fightingnerd.feat.quiz.model.Question
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

internal data class QuizState(
    val enabledCharacterIdList: ImmutableList<String> = persistentListOf(),
    val questionList: ImmutableList<Question> = persistentListOf(),
    val currentQuestionIndex: Int = 0,
    val correct: Int = 0,
    val incorrect: Int = 0,
    val displayFinishDialog: Boolean = false,

    val isLoading: Boolean = false,
) {
    val isLastQuestion: Boolean get() = (currentQuestionIndex == questionList.lastIndex)
    val currentQuestion: Question? get() = questionList.getOrNull(currentQuestionIndex)

    companion object {
        private val armorKingMoves = listOf(
            Question.MoveOption(
                id = "armor_king-bad.2,3",
                input = "bad23",
                startup = "i15~i16",
                onBlock = "-7",
                onHit = "+18g",
            ),
            Question.MoveOption(
                id = "armor_king-h.ub1",
                input = "h.ub1",
                startup = "i24~25",
                onBlock = "+8",
                onHit = "+60a",
            ),
            Question.MoveOption(
                id = "armor_king-b1+2",
                input = "b1+2",
                startup = "i16~17",
                onBlock = "+0",
                onHit = "+4",
                onCH = "+15",
            ),
            Question.MoveOption(
                id = "armor_king-1",
                input = "1",
                startup = "i10",
                onBlock = "+1",
                onHit = "+8",
            ),
        )

        val PREVIEW = QuizState(
            enabledCharacterIdList = persistentListOf("Armor King"),
            questionList = persistentListOf(
                Question(characterName = "Armor King", options = armorKingMoves.toImmutableList(), correctIndex = 0, answeredIndex = 0),
                Question(characterName = "Armor King", options = armorKingMoves.toImmutableList(), correctIndex = 1, answeredIndex = 3),
                Question(characterName = "Armor King", options = armorKingMoves.toImmutableList(), correctIndex = 2, answeredIndex = 2),
                Question(characterName = "Armor King", options = armorKingMoves.toImmutableList(), correctIndex = 3, answeredIndex = null),
            ),
            currentQuestionIndex = 3,
            correct = 2,
            incorrect = 1,
        )
    }
}
