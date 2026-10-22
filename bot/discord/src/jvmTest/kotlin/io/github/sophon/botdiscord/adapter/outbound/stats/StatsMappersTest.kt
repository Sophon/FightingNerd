package io.github.sophon.botdiscord.adapter.outbound.stats

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.discord.adapter.outbound.stats.failedStatsCommand
import io.github.sophon.discord.adapter.outbound.stats.toDomain
import io.github.sophon.discord.adapter.outbound.stats.toDomainError
import io.github.sophon.discord.adapter.outbound.stats.toStatsCommand
import io.github.sophon.discord.app.model.UsageReport
import io.github.sophon.discord.app.model.discord.Command
import io.github.sophon.model.DailyReport
import io.github.sophon.model.StatsError
import io.github.sophon.model.Usage
import io.github.sophon.wiki.model.wiki.Game
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import io.github.sophon.model.Command as StatsCommand

class StatsMappersTest {
    //region toStatsCommand
    @Test
    fun `command is recorded with its game id`() {
        // given
        val expected = StatsCommand(game = "Tekken_8", name = "Fd")

        // when
        val result = Command.Fd.toStatsCommand(Game.Tekken8)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `game-less command has no game`() {
        // given
        val expected = StatsCommand(game = null, name = "Gl")

        // when
        val result = Command.Gl.toStatsCommand(null)

        // then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region failedStatsCommand
    @Test
    fun `failure is a game-less failed command`() {
        // given
        val expected = StatsCommand(game = null, name = "failed")

        // when
        val result = failedStatsCommand()

        // then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region toDomain
    @Test
    fun `daily report becomes a usage report`() {
        // given
        val dailyReport = DailyReport(
            date = LocalDate(year = 2026, month = 10, day = 5),
            usageList = listOf(
                Usage(game = "Tekken_8", command = "Fd", count = 42),
                Usage(game = null, command = "failed", count = 3),
            ),
        )
        val expected = UsageReport(
            date = LocalDate(year = 2026, month = 10, day = 5),
            usageList = listOf(
                UsageReport.Usage(game = "Tekken_8", command = "Fd", count = 42),
                UsageReport.Usage(game = null, command = "failed", count = 3),
            ),
        )

        // when
        val result = dailyReport.toDomain()

        // then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region toDomainError
    @Test
    fun `file and serialization errors become file errors`() {
        // given
        val expected = listOf("FileError(2026-10-05.json)", "FileError(2026-10-05.json, Unexpected JSON token)")

        // when
        val result = listOf(
            StatsError.FileError("2026-10-05.json").toDomainError(),
            StatsError.SerializationError("2026-10-05.json", "Unexpected JSON token").toDomainError(),
        )

        // then
        assertThat(result.map { it.toString() }).isEqualTo(expected)
    }

    @Test
    fun `unknown error joins its causes`() {
        // given
        val expected = "Unknown(disk, permissions)"

        // when
        val result = StatsError.Unknown("disk", "permissions").toDomainError()

        // then
        assertThat(result.toString()).isEqualTo(expected)
    }
    //endregion
}
