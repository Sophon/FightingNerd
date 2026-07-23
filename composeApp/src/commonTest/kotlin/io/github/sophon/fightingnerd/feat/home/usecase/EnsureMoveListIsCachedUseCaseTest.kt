package io.github.sophon.fightingnerd.feat.home.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.fightingnerd.core.model.AppError
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class EnsureMoveListIsCachedUseCaseTest {
    private val testCharacter = Character(
        id = "kazuya",
        displayName = "Kazuya",
        remoteQueryId = "Kazuya",
        wikiUrl = "https://wavu.wiki/t/Kazuya",
    )
    private val testMoveList = listOf(
        Move(
            characterId = "kazuya",
            id = "1",
            input = "1",
            urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Kazuya"),
        ),
    )

    @Test
    fun `usecase does not refresh data when move list is cached`() = runTest {
        // given
        val wikiClient = FakeWikiClient(
            fetchCharacterResult = Result.Success(testCharacter),
            checkHasCachedMovesResult = Result.Success(true),
        )
        val repo = FakeFeatureRepo(gameClients = mapOf(Game.Tekken8 to wikiClient))
        val usecase = EnsureMoveListIsCachedUseCase(repo)

        // when
        val result = usecase.invoke(game = Game.Tekken8, characterId = testCharacter.id)

        // then
        assertThat(result).isInstanceOf(Result.Success::class)
        assertThat(wikiClient.downloadMoveListForCalled).isFalse()
        assertThat(wikiClient.cacheMoveListCalled).isFalse()
    }

    @Test
    fun `usecase refreshes data when move list is not cached`() = runTest {
        // given
        val wikiClient = FakeWikiClient(
            fetchCharacterResult = Result.Success(testCharacter),
            checkHasCachedMovesResult = Result.Success(false),
            downloadMoveListResult = Result.Success(testMoveList),
        )
        val repo = FakeFeatureRepo(gameClients = mapOf(Game.Tekken8 to wikiClient))
        val usecase = EnsureMoveListIsCachedUseCase(repo)

        // when
        val result = usecase.invoke(game = Game.Tekken8, characterId = testCharacter.id)

        // then
        assertThat(result).isInstanceOf(Result.Success::class)
        assertThat(wikiClient.downloadMoveListForCalled).isTrue()
        assertThat(wikiClient.cacheMoveListCalled).isTrue()
        assertThat(wikiClient.cachedCharacter).isEqualTo(testCharacter)
        assertThat(wikiClient.cachedMoveList).isEqualTo(testMoveList)
    }

    @Test
    fun `usecase returns error when game does not exist`() = runTest {
        // given
        val repo = FakeFeatureRepo(gameClients = emptyMap())
        val usecase = EnsureMoveListIsCachedUseCase(repo)

        // when
        val result = usecase.invoke(game = Game.Tekken8, characterId = testCharacter.id)

        // then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat((result as Result.Error).error).isEqualTo(AppError.WikiError(Game.Tekken8.id))
    }

    @Test
    fun `usecase returns error when character does not exist`() = runTest {
        // given
        val fetchCharacterError = WikiError.UnknownCharacter(testCharacter.id)
        val wikiClient = FakeWikiClient(
            fetchCharacterResult = Result.Error(fetchCharacterError),
        )
        val repo = FakeFeatureRepo(gameClients = mapOf(Game.Tekken8 to wikiClient))
        val usecase = EnsureMoveListIsCachedUseCase(repo)

        // when
        val result = usecase.invoke(game = Game.Tekken8, characterId = testCharacter.id)

        // then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat((result as Result.Error).error).isEqualTo(AppError.WikiError(fetchCharacterError.toString()))
    }
}
