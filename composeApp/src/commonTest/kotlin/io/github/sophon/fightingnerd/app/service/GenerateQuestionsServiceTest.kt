package io.github.sophon.fightingnerd.app.service

import assertk.assertThat
import assertk.assertions.containsOnly
import assertk.assertions.each
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.prop
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.COUNT_DISTRACTIONS
import io.github.sophon.fightingnerd.app.model.COUNT_QUESTIONS
import io.github.sophon.fightingnerd.app.model.Character
import io.github.sophon.fightingnerd.app.model.Move
import io.github.sophon.fightingnerd.app.model.Question
import io.github.sophon.fightingnerd.app.outPort.CharacterPort
import io.github.sophon.fightingnerd.app.outPort.MovePort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class GenerateQuestionsServiceTest {
    private val kazuya = Character(id = "kazuya", displayName = "Kazuya")
    private val jin = Character(id = "jin", displayName = "Jin")
    private val kazuyaMoveList = listOf("1", "1,2", "d/f+1", "f,n,d,d/f+2").map { input ->
        Move(
            input = input,
            urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Kazuya_movelist#Kazuya-$input"),
            groupId = "n",
        )
    }

    @Test
    fun `generates ten questions`() = runTest {
        // given
        val service = generateQuestionsService(
            characterList = listOf(kazuya),
            moveListByCharacterId = mapOf(kazuya.id to kazuyaMoveList),
        )
        val expected = COUNT_QUESTIONS

        // when
        val result = service("Tekken_8", characterId = null)

        // then
        assertThat((result as? Result.Success)?.data).isNotNull().hasSize(expected)
    }

    @Test
    fun `every question has one correct option and three distractions`() = runTest {
        // given
        val service = generateQuestionsService(
            characterList = listOf(kazuya),
            moveListByCharacterId = mapOf(kazuya.id to kazuyaMoveList),
        )
        val expected = (COUNT_DISTRACTIONS + 1)

        // when
        val result = service("Tekken_8", characterId = null)

        // then
        assertThat((result as? Result.Success)?.data).isNotNull().each { question ->
            question.prop(Question::options).hasSize(expected)
        }
    }

    @Test
    fun `characters without moves are skipped`() = runTest {
        // given
        val service = generateQuestionsService(
            characterList = listOf(kazuya, jin),
            moveListByCharacterId = mapOf(kazuya.id to kazuyaMoveList, jin.id to emptyList()),
        )
        val expected = kazuya

        // when
        val result = service("Tekken_8", characterId = null)

        // then
        val characterList = (result as? Result.Success)?.data?.map { question -> question.character }
        assertThat(characterList).isNotNull().containsOnly(expected)
    }

    @Test
    fun `game without moves fails`() = runTest {
        // given
        val service = generateQuestionsService(
            characterList = listOf(kazuya, jin),
            moveListByCharacterId = emptyMap(),
        )
        val expected = Result.Error(AppError.WikiError("Tekken_8 has no moves"))

        // when
        val result = service("Tekken_8", characterId = null)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `chosen character gets ten questions about them`() = runTest {
        // given
        val service = generateQuestionsService(
            characterList = listOf(kazuya, jin),
            moveListByCharacterId = mapOf(kazuya.id to kazuyaMoveList, jin.id to kazuyaMoveList),
        )
        val expected = List(COUNT_QUESTIONS) { kazuya }

        // when
        val result = service("Tekken_8", characterId = kazuya.id)

        // then
        val characterList = (result as? Result.Success)?.data?.map { question -> question.character }
        assertThat(characterList).isEqualTo(expected)
    }

    @Test
    fun `unknown character fails`() = runTest {
        // given
        val service = generateQuestionsService(
            characterList = listOf(kazuya),
            moveListByCharacterId = mapOf(kazuya.id to kazuyaMoveList),
        )
        val expected = Result.Error(AppError.WikiError("jin not found"))

        // when
        val result = service("Tekken_8", characterId = jin.id)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `chosen character without moves fails`() = runTest {
        // given
        val service = generateQuestionsService(
            characterList = listOf(kazuya, jin),
            moveListByCharacterId = mapOf(kazuya.id to kazuyaMoveList),
        )
        val expected = Result.Error(AppError.WikiError("jin has no moves"))

        // when
        val result = service("Tekken_8", characterId = jin.id)

        // then
        assertThat(result).isEqualTo(expected)
    }


    private fun generateQuestionsService(
        characterList: List<Character>,
        moveListByCharacterId: Map<String, List<Move>>,
    ): GenerateQuestionsService {
        val service = GenerateQuestionsService(
            characterPort = FakeCharacterPort(characterList),
            movePort = FakeMovePort(moveListByCharacterId),
        )
        return service
    }

    private class FakeCharacterPort(
        private val characterList: List<Character>,
    ): CharacterPort {
        override fun subscribeToCharacters(gameId: String): Flow<List<Character>> {
            return flowOf(characterList)
        }
    }

    private class FakeMovePort(
        private val moveListByCharacterId: Map<String, List<Move>>,
    ): MovePort {
        override fun subscribeToMoves(gameId: String, characterId: String): Flow<List<Move>> {
            return flowOf(moveListByCharacterId[characterId].orEmpty())
        }
    }
}
