package io.github.sophon.fightingnerd.app.service

import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.COUNT_DISTRACTIONS
import io.github.sophon.fightingnerd.app.model.COUNT_QUESTIONS
import io.github.sophon.fightingnerd.app.model.Character
import io.github.sophon.fightingnerd.app.model.Move
import io.github.sophon.fightingnerd.app.model.Question
import io.github.sophon.fightingnerd.app.outPort.CharacterPort
import io.github.sophon.fightingnerd.app.outPort.MovePort
import io.github.sophon.fightingnerd.inPort.GenerateQuestionsUseCase
import kotlinx.coroutines.flow.first

internal class GenerateQuestionsService(
    private val characterPort: CharacterPort,
    private val movePort: MovePort,
): GenerateQuestionsUseCase {
    override suspend fun invoke(
        gameId: String,
        characterId: String?,
    ): Result<List<Question>, AppError> {
        val result = if (characterId == null) {
            generateQuestionsForCharacters(gameId)
        } else {
            generateQuestionsForCharacter(gameId, characterId)
        }
        return result
    }


    private suspend fun generateQuestionsForCharacters(gameId: String): Result<List<Question>, AppError> {
        val moveListByCharacter = characterPort.subscribeToCharacters(gameId)
            .first()
            .associateWith { character -> movePort.subscribeToMoves(gameId, character.id).first() }
            .filterValues { moveList -> moveList.isNotEmpty() }
        if (moveListByCharacter.isEmpty()) return Result.Error(AppError.WikiError("$gameId has no moves"))

        val questionList = List(COUNT_QUESTIONS) {
            val (character, moveList) = moveListByCharacter.entries.random()
            moveList.random().toQuestion(character, moveList)
        }
        val result = Result.Success(questionList)
        return result
    }

    private suspend fun generateQuestionsForCharacter(
        gameId: String,
        characterId: String,
    ): Result<List<Question>, AppError> {
        val character = characterPort.subscribeToCharacters(gameId)
            .first()
            .firstOrNull { it.id == characterId }
            ?: return Result.Error(AppError.WikiError("$characterId not found"))

        val moveList = movePort.subscribeToMoves(gameId, characterId).first()
        if (moveList.isEmpty()) return Result.Error(AppError.WikiError("$characterId has no moves"))

        val questionList = List(COUNT_QUESTIONS) {
            moveList.random().toQuestion(character, moveList)
        }
        val result = Result.Success(questionList)
        return result
    }

    private fun Move.toQuestion(
        character: Character,
        moveList: List<Move>,
    ): Question {
        val distractionList = moveList
            .filter { move -> move != this }
            .shuffled()
            .take(COUNT_DISTRACTIONS)
        val options = (distractionList + this).shuffled()

        val question = Question(
            character = character,
            options = options,
            correctIndex = options.indexOf(this),
        )
        return question
    }
}
