package io.github.sophon.fightingnerd.feat.quiz.ui.quiz

import io.github.sophon.core.wiki.model.Move
import io.github.sophon.fightingnerd.feat.quiz.model.Question

internal data class QuizState(
    val enabledCharacterIdList: List<String> = emptyList(),
    val questionList: List<Question> = emptyList(),
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
            Move(
                characterId = "Armor King",
                id = "armor_king-bad.2,3",
                input = "bad23",
                startup = "i15~i16",
                onBlock = "-7",
                onHit = "+18g",
                urls = Move.Urls(
                    videoId = "T8-p2-armor_king-bad.3.mp4",
                ),
            ),
            Move(
                characterId = "Armor King",
                id = "armor_king-h.ub1",
                name = "Neck Hunter: Villain",
                input = "h.ub1",
                damage = "25",
                startup = "i24~25",
                onBlock = "+8",
                onHit = "+60a",
                recovery = "r25",
                guard = "h",
                notes = listOf(
                    "Strong Aerial Tailspin",
                    "Homing",
                    "Transition to r26 BAD with F (+8/+61a)",
                    "Consumes 150F of remaining Heat time",
                    "7 chip damage on block",
                ),
                urls = Move.Urls(
                    wikiUrl = "https://wavu.wiki/t/Armor_King_movelist#Armor_King-H.ub+1",
                ),
                t8Properties = Move.T8Properties(isHeat = true, isHoming = true),
            ),
            Move(
                characterId = "Armor King",
                id = "armor_king-b1+2",
                name = "Blindside",
                input = "b1+2",
                damage = "0",
                startup = "i16~17",
                onBlock = "+0",
                onHit = "+4",
                onCH = "+15",
                recovery = "r27",
                guard = "m",
                urls = Move.Urls(
                    wikiUrl = "https://wavu.wiki/t/Armor_King_movelist#Armor_King-b+1+2",
                ),
                t8Properties = Move.T8Properties(),
            ),
            Move(
                characterId = "Armor King",
                id = "armor_king-1",
                name = "Jab",
                input = "1",
                damage = "5",
                startup = "i10",
                onBlock = "+1",
                onHit = "+8",
                recovery = "r19",
                guard = "h",
                notes = listOf("Recovers 2f faster on hit or block (t27 r17)"),
                urls = Move.Urls(
                    wikiUrl = "https://wavu.wiki/t/Armor_King_movelist#Armor_King-1",
                ),
                t8Properties = Move.T8Properties(),
            ),
        )

        val PREVIEW = QuizState(
            enabledCharacterIdList = listOf("Armor King"),
            questionList = listOf(
                Question(characterName = "Armor King", options = armorKingMoves, correctIndex = 0, answeredIndex = 0),
                Question(characterName = "Armor King", options = armorKingMoves, correctIndex = 1, answeredIndex = 3),
                Question(characterName = "Armor King", options = armorKingMoves, correctIndex = 2, answeredIndex = 2),
                Question(characterName = "Armor King", options = armorKingMoves, correctIndex = 3, answeredIndex = null),
            ),
            currentQuestionIndex = 3,
            correct = 2,
            incorrect = 1,
        )
    }
}
