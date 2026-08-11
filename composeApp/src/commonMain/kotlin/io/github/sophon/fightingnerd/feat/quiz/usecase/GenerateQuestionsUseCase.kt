package io.github.sophon.fightingnerd.feat.quiz.usecase

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.FeatureRepo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.CharacterId
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.fightingnerd.core.model.AppError
import io.github.sophon.fightingnerd.feat.quiz.COUNT_DISTRACTIONS
import io.github.sophon.fightingnerd.feat.quiz.COUNT_QUESTIONS
import io.github.sophon.fightingnerd.feat.quiz.model.Question
import kotlinx.coroutines.flow.first

internal class GenerateQuestionsUseCase(
    private val repo: FeatureRepo,
) {
    suspend fun invoke(gameId: String): Result<List<Question>, AppError> {
        val game = Game.fromId(gameId) ?: return Result.Error(AppError.GameNotFound(gameId))
        val wiki = repo.getWikiClientFor(game) ?: return Result.Error(AppError.WikiClientNotFound(gameId))

        val characterList = wiki.subscribeToCharacterList().first()

        val allQuestions = mutableListOf<Question>()
        while (allQuestions.size < COUNT_QUESTIONS) {
            val randomCharacter = characterList.random()
            val moveList = wiki.subscribeToMoveList(CharacterId(randomCharacter.id)).first()
            val randomMove = moveList.random()
            val question = randomMove.generateQuestion(randomCharacter, moveList)
            allQuestions.add(question)
        }

        return Result.Success(allQuestions)
    }


    private fun Move.generateQuestion(
        character: Character,
        moveList: List<Move>
    ): Question {
        val distractions = moveList
            .filter { move -> move.id != this.id }
            .shuffled()
            .take(COUNT_DISTRACTIONS)

        val options = (distractions + this).shuffled()
        val correctIndex = options.indexOfFirst { move -> move.id == this.id }
        val question = Question(
            characterName = character.displayName,
            options = options,
            correctIndex = correctIndex,
        )

        return question
    }
}
