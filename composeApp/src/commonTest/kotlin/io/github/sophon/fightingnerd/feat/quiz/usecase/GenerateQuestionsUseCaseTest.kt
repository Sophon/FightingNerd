package io.github.sophon.fightingnerd.feat.quiz.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.fightingnerd.core.model.AppError
import io.github.sophon.fightingnerd.feat.FakeFeatureRepo
import io.github.sophon.fightingnerd.feat.FakeWikiClient
import io.github.sophon.fightingnerd.feat.quiz.COUNT_DISTRACTIONS
import io.github.sophon.fightingnerd.feat.quiz.COUNT_QUESTIONS
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class GenerateQuestionsUseCaseTest {
    private val testCharacterList = listOf(
        Character(id = "kazuya", displayName = "Kazuya", remoteQueryId = "kazuya", wikiUrl = ""),
        Character(id = "jin", displayName = "Jin", remoteQueryId = "jin", wikiUrl = ""),
    )
    private val testMoveList = listOf(
        Move(characterId = "kazuya", id = "1", input = "1", urls = Move.Urls(wikiUrl = "")),
        Move(characterId = "kazuya", id = "2", input = "2", urls = Move.Urls(wikiUrl = "")),
        Move(characterId = "kazuya", id = "3", input = "3", urls = Move.Urls(wikiUrl = "")),
        Move(characterId = "kazuya", id = "4", input = "4", urls = Move.Urls(wikiUrl = "")),
    )

    @Test
    fun `usecase generates exactly ten questions`() = runTest {
        // given
        val game = Game.Tekken8
        val wiki = FakeWikiClient(
            subscribeToCharacterListResult = testCharacterList,
            subscribeToMoveListResult = testMoveList,
        )
        val repo = FakeFeatureRepo(mapOf(game to wiki))
        val usecase = GenerateQuestionsUseCase(repo)
        val expectedCount = COUNT_QUESTIONS

        // when
        val result = usecase.invoke(game.id)

        //then
        assertThat(result).isInstanceOf(Result.Success::class)
        val actualCount = (result as? Result.Success)?.data?.size
        assertThat(actualCount).isEqualTo(expectedCount)
    }

    @Test
    fun `usecase generates questions with one correct and four distraction options`() = runTest {
        // given
        val game = Game.Tekken8
        val wiki = FakeWikiClient(
            subscribeToCharacterListResult = testCharacterList,
            subscribeToMoveListResult = testMoveList,
        )
        val repo = FakeFeatureRepo(mapOf(game to wiki))
        val usecase = GenerateQuestionsUseCase(repo)
        val expectedCount = (COUNT_DISTRACTIONS + 1)

        // when
        val result = usecase.invoke(game.id)

        //then
        assertThat(result).isInstanceOf(Result.Success::class)
        (result as? Result.Success)?.data?.forEach { question ->
            assertThat(question.options.size).isEqualTo(expectedCount)
        }
    }

    @Test
    fun `usecase returns error when game not found`() = runTest {
        // given
        val game = Game.Tekken8
        val repo = FakeFeatureRepo(emptyMap())
        val usecase = GenerateQuestionsUseCase(repo)
        val expected = AppError.WikiClientNotFound(game.id)

        // when
        val result = usecase.invoke(game.id)

        //then
        assertThat(result).isInstanceOf(Result.Error::class)
        val actualError = (result as? Result.Error)?.error
        assertThat(actualError).isEqualTo(expected)
    }
}
