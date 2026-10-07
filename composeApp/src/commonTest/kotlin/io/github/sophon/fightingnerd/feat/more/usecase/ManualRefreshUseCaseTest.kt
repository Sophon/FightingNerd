package io.github.sophon.fightingnerd.feat.more.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.model.RefreshEvent
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.feat.FakeFeatureRepo
import io.github.sophon.fightingnerd.feat.FakeWikiClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class ManualRefreshUseCaseTest {

    @Test
    fun `refreshWiki dedupes clients so a shared client refreshes only once`() = runTest {
        // given
        val sharedClient = FakeWikiClient(name = "SuperCombo Wiki")
        val repo = FakeFeatureRepo(
            gameClients = mapOf(
                Game.StreetFighter6 to sharedClient,
                Game.MK1 to sharedClient,
            ),
        )
        val usecase = ManualRefreshUseCase(repo)
        val expected = Result.Success(Unit)
        val expectedRefreshCalled = true

        // when
        val result = usecase.refreshWiki(listOf(Game.StreetFighter6.id, Game.MK1.id))
        val refreshCalled = sharedClient.refreshCalled

        // then
        assertThat(result).isEqualTo(expected)
        assertThat(refreshCalled).isEqualTo(expectedRefreshCalled)
    }

    @Test
    fun `refreshWiki refreshes every distinct client and returns Success`() = runTest {
        // given
        val wavuClient = FakeWikiClient(name = "Wavu Wiki")
        val superComboClient = FakeWikiClient(name = "SuperCombo Wiki")
        val repo = FakeFeatureRepo(
            gameClients = mapOf(
                Game.Tekken8 to wavuClient,
                Game.StreetFighter6 to superComboClient,
            ),
        )
        val usecase = ManualRefreshUseCase(repo)
        val expected = Result.Success(Unit)

        // when
        val result = usecase.refreshWiki(listOf(Game.Tekken8.id, Game.StreetFighter6.id))

        // then
        assertThat(result).isEqualTo(expected)
        assertThat(wavuClient.refreshCalled).isTrue()
        assertThat(superComboClient.refreshCalled).isTrue()
    }

    @Test
    fun `refreshWiki returns WikiClientNotFound when no id maps to a known game`() = runTest {
        // given
        val repo = FakeFeatureRepo()
        val usecase = ManualRefreshUseCase(repo)
        val gameIdList = listOf("Not_A_Game", "Also_Bogus")
        val expected = Result.Error(AppError.WikiClientNotFound(gameIdList.toString()))

        // when
        val result = usecase.refreshWiki(gameIdList)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `refreshWiki returns WikiClientNotFound when known games have no client`() = runTest {
        // given
        val repo = FakeFeatureRepo(gameClients = emptyMap())
        val usecase = ManualRefreshUseCase(repo)
        val gameIdList = listOf(Game.Tekken8.id)
        val expected = Result.Error(AppError.WikiClientNotFound(gameIdList.toString()))

        // when
        val result = usecase.refreshWiki(gameIdList)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `refreshWiki short-circuits on the first failing client`() = runTest {
        // given
        val wikiError = WikiError.UnknownCharacter("bad")
        val failingClient = FakeWikiClient(
            name = "Wavu Wiki",
            refreshEvents = listOf(RefreshEvent.Failed(wikiError)),
        )
        val laterClient = FakeWikiClient(name = "SuperCombo Wiki")
        val repo = FakeFeatureRepo(
            gameClients = mapOf(
                Game.Tekken8 to failingClient,
                Game.StreetFighter6 to laterClient,
            ),
        )
        val usecase = ManualRefreshUseCase(repo)
        val expected = Result.Error(AppError.WikiError(wikiError.toString()))

        // when
        val result = usecase.refreshWiki(listOf(Game.Tekken8.id, Game.StreetFighter6.id))

        // then
        assertThat(result).isEqualTo(expected)
        assertThat(laterClient.refreshCalled).isFalse()
    }

    @Test
    fun `refreshGame returns GameNotFound for an unknown id`() = runTest {
        // given
        val repo = FakeFeatureRepo()
        val usecase = ManualRefreshUseCase(repo)
        val gameId = "Not_A_Game"
        val expected = Result.Error(AppError.GameNotFound(gameId))

        // when
        val result = usecase.refreshGame(gameId)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `refreshGame returns WikiClientNotFound when the game has no client`() = runTest {
        // given
        val repo = FakeFeatureRepo(gameClients = emptyMap())
        val usecase = ManualRefreshUseCase(repo)
        val gameId = Game.Tekken8.id
        val expected = Result.Error(AppError.WikiClientNotFound(gameId))

        // when
        val result = usecase.refreshGame(gameId)

        // then
        assertThat(result).isEqualTo(expected)
    }
}
