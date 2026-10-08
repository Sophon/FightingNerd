package io.github.sophon.fightingnerd.app.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.fightingnerd.app.model.Game
import io.github.sophon.fightingnerd.app.model.RefreshEvent
import io.github.sophon.fightingnerd.app.model.Wiki
import io.github.sophon.fightingnerd.app.outPort.RefreshWikiPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class StartRefreshServiceTest {
    private val tekken8 = Game(
        id = "Tekken_8",
        displayName = "Tekken 8",
        iconUrl = "https://i.imgur.com/Yl6j809.png",
        wiki = Wiki(name = "Wavu Wiki", url = "https://wavu.wiki/", iconUrl = "https://wavu.wiki/android-chrome-512x512.png"),
    )

    @Test
    fun `refreshing everything runs the wiki refresh in the app scope`() = runTest {
        // given
        val refreshWikiPort = FakeRefreshWikiPort(tekken8)
        val service = StartRefreshService(refreshWikiPort = refreshWikiPort, appScope = backgroundScope)
        val expected = 1

        // when
        service()
        runCurrent()

        // then
        assertThat(refreshWikiPort.refreshAllCount).isEqualTo(expected)
    }

    @Test
    fun `refreshing games runs the wiki refresh for only those games`() = runTest {
        // given
        val refreshWikiPort = FakeRefreshWikiPort(tekken8)
        val service = StartRefreshService(refreshWikiPort = refreshWikiPort, appScope = backgroundScope)
        val expected = listOf(setOf("Tekken_8", "GGST"))

        // when
        service(setOf("Tekken_8", "GGST"))
        runCurrent()

        // then
        assertThat(refreshWikiPort.refreshedList).isEqualTo(expected)
    }


    /**
     * Counts collections rather than calls - an uncollected refresh flow does nothing.
     */
    private class FakeRefreshWikiPort(
        private val game: Game,
    ): RefreshWikiPort {
        var refreshAllCount = 0
            private set
        val refreshedList = mutableListOf<Set<String>>()

        override fun refresh(): Flow<RefreshEvent> {
            val flow = flow {
                refreshAllCount++
                emit(RefreshEvent.Finished(game = game, successCount = 37))
            }
            return flow
        }

        override fun refresh(gameIdSet: Set<String>): Flow<RefreshEvent> {
            val flow = flow {
                refreshedList.add(gameIdSet)
                emit(RefreshEvent.Finished(game = game, successCount = 37))
            }
            return flow
        }
    }
}
