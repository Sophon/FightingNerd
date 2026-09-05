package io.github.sophon.fightingnerd.feat.quiz.usecase

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.FeatureRepo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.util.stripMarkdownLinks
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.CharacterId
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.fightingnerd.core.model.AppError
import io.github.sophon.fightingnerd.feat.quiz.COUNT_DISTRACTIONS
import io.github.sophon.fightingnerd.feat.quiz.COUNT_QUESTIONS
import io.github.sophon.fightingnerd.feat.quiz.model.Question
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.first

internal class GenerateQuestionsUseCase(
    private val repo: FeatureRepo,
) {
    suspend operator fun invoke(
        gameId: String,
        characterId: String? = null,
    ): Result<List<Question>, AppError> {
        val game = Game.fromId(gameId) ?: return Result.Error(AppError.GameNotFound(gameId))
        val wikiClient = repo.getWikiClientFor(game) ?: return Result.Error(AppError.WikiClientNotFound(gameId))

        val result = if (characterId == null) {
            generateQuestionsForCharacters(wikiClient)
        } else {
            generateQuestionsForCharacter(wikiClient, characterId)
        }

        return result
    }


    private suspend fun generateQuestionsForCharacters(wikiClient: WikiClient): Result<List<Question>, AppError> {
        val characterList = wikiClient.subscribeToCharacterList()
            .first()
            .filter { character ->
                wikiClient.subscribeToMoveList(CharacterId(character.id))
                    .first()
                    .isNotEmpty()
            }

        val allQuestions = mutableListOf<Question>()
        while (allQuestions.size < COUNT_QUESTIONS) {
            val randomCharacter = characterList.random()
            val moveList = wikiClient.subscribeToMoveList(CharacterId(randomCharacter.id)).first()
            val randomMove = moveList.random()
            val question = randomMove.generateQuestion(randomCharacter, moveList)
            allQuestions.add(question)
        }

        return Result.Success(allQuestions)
    }

    private suspend fun generateQuestionsForCharacter(
        wikiClient: WikiClient,
        characterId: String,
    ): Result<List<Question>, AppError> {
        val character = wikiClient.subscribeToCharacterList()
            .first()
            .firstOrNull { it.id == characterId }
            ?: return Result.Error(AppError.Unknown)

        val moveList = wikiClient.subscribeToMoveList(CharacterId(characterId)).first()
        if (moveList.isEmpty()) return Result.Error(AppError.Unknown)

        val allQuestions = mutableListOf<Question>()
        while (allQuestions.size < COUNT_QUESTIONS) {
            val randomMove = moveList.random()
            val question = randomMove.generateQuestion(character, moveList)
            allQuestions.add(question)
        }

        val result = Result.Success(allQuestions)
        return result
    }


    private fun Move.generateQuestion(
        character: Character,
        moveList: List<Move>
    ): Question {
        val distractions = moveList
            .filter { move -> move.id != this.id }
            .shuffled()
            .take(COUNT_DISTRACTIONS)

        val options = (distractions + this)
            .shuffled()
            .map { it.toOption() }
            .toImmutableList()
        val correctIndex = options.indexOfFirst { move -> move.id == this.id }
        val question = Question(
            characterName = character.displayName,
            options = options,
            correctIndex = correctIndex,
        )

        return question
    }

    private fun Move.toOption(): Question.MoveOption {
        val option = Question.MoveOption(
            id = id,
            input = input,
            startup = startup,
            onBlock = onBlock?.stripMarkdownLinks(),
            onHit = onHit?.stripMarkdownLinks(),
            onCH = onCH?.stripMarkdownLinks(),
            urls = Question.MoveOption.Urls(
                videoUrl = urls.videoUrl,
                hitboxImageList = urls.hitboxImageList.toImmutableList(),
                moveImageList = urls.moveImageList.toImmutableList(),
            )
        )
        return option
    }
}
