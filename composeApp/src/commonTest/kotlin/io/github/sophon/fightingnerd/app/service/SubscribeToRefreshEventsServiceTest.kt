package io.github.sophon.fightingnerd.app.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.Game
import io.github.sophon.fightingnerd.app.model.RefreshEvent
import io.github.sophon.fightingnerd.app.model.Wiki
import io.github.sophon.fightingnerd.app.outPort.RefreshEventsPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class SubscribeToRefreshEventsServiceTest {
    private val tekken8 = Game(
        id = "Tekken_8",
        displayName = "Tekken 8",
        iconUrl = "https://i.imgur.com/Yl6j809.png",
        wiki = Wiki(name = "Wavu Wiki", url = "https://wavu.wiki/", iconUrl = "https://wavu.wiki/android-chrome-512x512.png"),
    )

    @Test
    fun `refresh events are emitted in order`() = runTest {
        // given
        val expected = listOf(
            RefreshEvent.Started(game = tekken8),
            RefreshEvent.Progress(game = tekken8, fraction = 0.5f),
            RefreshEvent.Failure(game = tekken8, error = AppError.WikiError("DownloadError(Jin)"), characterId = "jin"),
            RefreshEvent.Finished(game = tekken8, successCount = 37),
        )
        val service = SubscribeToRefreshEventsService(refreshEventsPort = FakeRefreshEventsPort(expected))

        // when
        val result = service().toList()

        // then
        assertThat(result).isEqualTo(expected)
    }


    private class FakeRefreshEventsPort(
        private val eventList: List<RefreshEvent>,
    ): RefreshEventsPort {
        override fun subscribeToRefreshEvents(): Flow<RefreshEvent> {
            val flow = eventList.asFlow()
            return flow
        }
    }
}
