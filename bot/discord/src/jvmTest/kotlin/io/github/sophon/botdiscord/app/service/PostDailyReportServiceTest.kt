package io.github.sophon.botdiscord.app.service

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.UsageReport
import io.github.sophon.discord.app.outPort.PostReportPort
import io.github.sophon.discord.app.service.PostDailyReportService
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test

class PostDailyReportServiceTest {
    @Test
    fun `no archived report posts nothing`() = runTest {
        // given
        val postReportPort = FakePostReportPort()
        val service = postDailyReportService(
            statsPort = FakeStatsPort(latestReportResult = Result.Success(null)),
            postReportPort = postReportPort,
        )

        // when
        service()

        // then
        assertThat(postReportPort.postList).isEmpty()
    }

    @Test
    fun `no archived report is a success`() = runTest {
        // given
        val expected = Result.Success(Unit)
        val service = postDailyReportService(statsPort = FakeStatsPort(latestReportResult = Result.Success(null)))

        // when
        val result = service()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `latest report goes to the stats channels`() = runTest {
        // given
        val expected = (statsChannelIdList to usageReport)
        val postReportPort = FakePostReportPort()
        val service = postDailyReportService(postReportPort = postReportPort)

        // when
        service()

        // then
        assertThat(postReportPort.postList).containsExactly(expected)
    }

    @Test
    fun `failed post is returned`() = runTest {
        // given
        val expected = Result.Error(BotError.Kord("Missing access"))
        val service = postDailyReportService(postReportPort = FakePostReportPort(result = expected))

        // when
        val result = service()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `failed report load is returned`() = runTest {
        // given
        val expected = Result.Error(BotError.FileError("2026-10-05.json"))
        val service = postDailyReportService(statsPort = FakeStatsPort(latestReportResult = expected))

        // when
        val result = service()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `failed config load posts nothing`() = runTest {
        // given
        val postReportPort = FakePostReportPort()
        val service = postDailyReportService(
            loadConfigPort = FakeLoadConfigPort(Result.Error(BotError.FileError("discordConfig.json"))),
            postReportPort = postReportPort,
        )

        // when
        service()

        // then
        assertThat(postReportPort.postList).isEmpty()
    }


    private class FakePostReportPort(
        private val result: EmptyResult<BotError> = Result.Success(Unit),
    ): PostReportPort {
        val postList = mutableListOf<Pair<List<String>, UsageReport>>()

        override suspend fun post(channelIdList: List<String>, usageReport: UsageReport): EmptyResult<BotError> {
            postList += (channelIdList to usageReport)
            return result
        }
    }

    private fun postDailyReportService(
        loadConfigPort: FakeLoadConfigPort = FakeLoadConfigPort(
            Result.Success(discordConfigOf(statsChannelIdList = statsChannelIdList)),
        ),
        statsPort: FakeStatsPort = FakeStatsPort(latestReportResult = Result.Success(usageReport)),
        postReportPort: FakePostReportPort = FakePostReportPort(),
    ): PostDailyReportService {
        val service = PostDailyReportService(
            loadConfigPort = loadConfigPort,
            statsPort = statsPort,
            postReportPort = postReportPort,
        )
        return service
    }
}


private val statsChannelIdList = listOf("888888888888888888", "999999999999999999")
private val usageReport = UsageReport(
    date = LocalDate(year = 2026, month = 10, day = 5),
    usageList = listOf(
        UsageReport.Usage(game = "Tekken_8", command = "Fd", count = 42),
        UsageReport.Usage(game = null, command = "Gl", count = 3),
    ),
)
