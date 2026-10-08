package io.github.sophon.fightingnerd.app.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.Game
import io.github.sophon.fightingnerd.app.model.RefreshEvent
import io.github.sophon.fightingnerd.app.model.Wiki
import io.github.sophon.fightingnerd.app.outPort.RefreshWikiPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class RefreshGamesServiceTest {
    private val tekken8 = Game(
        id = "Tekken_8",
        displayName = "Tekken 8",
        iconUrl = "https://i.imgur.com/Yl6j809.png",
        wiki = Wiki(name = "Wavu Wiki", url = "https://wavu.wiki/", iconUrl = "https://wavu.wiki/android-chrome-512x512.png"),
    )

    @Test
    fun `only the requested games are refreshed`() = runTest {
        // given
        val refreshWikiPort = FakeRefreshWikiPort(eventList = listOf(RefreshEvent.Finished(game = tekken8, successCount = 37)))
        val service = RefreshGamesService(refreshWikiPort)
        val expected = listOf(setOf("Tekken_8", "GGST"))

        // when
        service(setOf("Tekken_8", "GGST"))

        // then
        assertThat(refreshWikiPort.refreshedList).isEqualTo(expected)
    }

    @Test
    fun `a refresh without failures succeeds`() = runTest {
        // given
        val service = RefreshGamesService(
            FakeRefreshWikiPort(eventList = listOf(RefreshEvent.Finished(game = tekken8, successCount = 37))),
        )
        val expected = Result.Success(Unit)

        // when
        val result = service(setOf("Tekken_8"))

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `a failed character fails the refresh`() = runTest {
        // given
        val error = AppError.WikiError("Yoshimitsu: NO_INTERNET")
        val service = RefreshGamesService(
            FakeRefreshWikiPort(
                eventList = listOf(
                    RefreshEvent.Failure(game = tekken8, error = error, characterId = "yoshimitsu"),
                    RefreshEvent.Finished(game = tekken8, successCount = 36),
                ),
            ),
        )
        val expected = Result.Error(error)

        // when
        val result = service(setOf("Tekken_8"))

        // then
        assertThat(result).isEqualTo(expected)
    }


    private class FakeRefreshWikiPort(
        private val eventList: List<RefreshEvent>,
    ): RefreshWikiPort {
        val refreshedList = mutableListOf<Set<String>>()

        override fun refresh(): Flow<RefreshEvent> {
            error("not used")
        }

        override fun refresh(gameIdSet: Set<String>): Flow<RefreshEvent> {
            refreshedList.add(gameIdSet)
            return eventList.asFlow()
        }
    }
}
