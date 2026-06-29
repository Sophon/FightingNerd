package io.github.sophon.fightingnerd.feat.quiz.usecase

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.CoreFeatureRepo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
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
        while (allQuestions.size < COUNT_QUESTIONS) {
            val randomCharacter = characterList.random()
            when (val moveListResult = wiki.fetchMoveList(characterQuery = randomCharacter.id)) {
                is Result.Success -> {
                    val randomMove = moveListResult.data.random()
                    val question = randomMove.generateQuestion(randomCharacter, moveListResult.data)
                    allQuestions.add(question)
                }
                is Result.Error -> continue
            }
        }

        return Result.Success(allQuestions)
    }


    private fun Move.generateQuestion(
        character: Character,
        moveList: List<Move>
    ): Question {
        val correct = this.copy(startup = this.startup?.trimFollowUps())
        val distractions = moveList
            .filter { move -> move.id != this.id }
            .map { it.copy(startup = it.startup?.trimFollowUps()) }
            .shuffled()
            .take(COUNT_DISTRACTIONS)

        val options = (distractions + correct).shuffled()
        val correctIndex = options.indexOfFirst { move -> move.id == this.id }
        val question = Question(
            characterName = character.displayName,
            options = options,
            correctIndex = correctIndex,
        )

        return question
    }

    private fun String.trimFollowUps(): String {
        val cutIndex = indexOfAny(charArrayOf('(', ','))
        val trimmed = if (cutIndex == -1) this else substring(0, cutIndex)
        return trimmed.trimEnd()
    }
}
