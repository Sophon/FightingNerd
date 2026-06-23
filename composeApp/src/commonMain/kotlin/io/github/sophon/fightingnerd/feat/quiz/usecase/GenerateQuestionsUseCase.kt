package io.github.sophon.fightingnerd.feat.quiz.usecase

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.CoreFeatureRepo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.fightingnerd.core.model.AppError
import io.github.sophon.fightingnerd.feat.quiz.COUNT_DISTRACTIONS
import io.github.sophon.fightingnerd.feat.quiz.COUNT_QUESTIONS
import io.github.sophon.fightingnerd.feat.quiz.model.Question

internal class GenerateQuestionsUseCase(
    private val repo: CoreFeatureRepo,
) {
    suspend fun invoke(gameId: String): Result<List<Question>, AppError> {
        val game = Game.fromId(gameId) ?: return Result.Error(AppError.GameNotFound(gameId))
        val wiki = repo.getWikiClientFor(game) ?: return Result.Error(AppError.WikiClientNotFound(gameId))

        val characterList = when (val characterListResult = wiki.fetchCharacterList()) {
            is Result.Success -> characterListResult.data
            is Result.Error -> return Result.Error(AppError.WikiError(characterListResult.error.inputs.joinToString(";")))
        }

        val allQuestions = mutableListOf<Question>()
        for (character in characterList) {
            val moveList = when (val moveListResult = wiki.fetchMoveList(characterQuery = character.id)) {
                is Result.Success -> moveListResult.data
                is Result.Error -> return Result.Error(AppError.WikiError(moveListResult.error.inputs.joinToString(";")))
            }
            allQuestions += moveList.generateQuestions()
        }

        return Result.Success(allQuestions)
    }


    private fun List<Move>.generateQuestions(): List<Question> {
        require(this.size >= COUNT_QUESTIONS) { "Need at least 10 moves in the Move pool" }
        val questionIndices = this.indices
            .shuffled()
            .take(COUNT_QUESTIONS)

        val questionList: List<Question> = questionIndices.map { index ->
            val move = this[index]
            val question = move.generateQuestion(this)
            question
        }

        return questionList
    }

    private fun Move.generateQuestion(moveList: List<Move>): Question {
        val distractions = moveList
            .filter { move -> move.id != this.id }
            .shuffled()
            .take(COUNT_DISTRACTIONS)

        val options = (distractions + this).shuffled()
        val correctIndex = options.indexOfFirst { move -> move.id == this.id }
        val question = Question(
            options = options,
            correctIndex = correctIndex,
        )

        return question
    }
}
