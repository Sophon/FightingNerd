package io.github.sophon.fightingnerd.app.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.RefreshEvent
import io.github.sophon.fightingnerd.app.outPort.RefreshWikiPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class RefreshDataServiceTest {
    @Test
    fun `wiki events are emitted in order`() = runTest {
        // given
        val expected = listOf(
            RefreshEvent.Failed(AppError.WikiError("DownloadError(Jin)")),
            RefreshEvent.Finished(successCount = 37),
        )
        val service = RefreshDataService(refreshWikiPort = FakeRefreshWikiPort(expected))

        // when
        val result = service().toList()

        // then
        assertThat(result).isEqualTo(expected)
    }


    private class FakeRefreshWikiPort(
        private val eventList: List<RefreshEvent>,
    ): RefreshWikiPort {
        override fun refresh(): Flow<RefreshEvent> {
            val flow = eventList.asFlow()
            return flow
        }
    }
}
