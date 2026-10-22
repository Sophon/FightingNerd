package io.github.sophon.botdiscord.adapter.outbound.wiki

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.adapter.outbound.wiki.toDomainError
import io.github.sophon.discord.adapter.outbound.wiki.toFilter
import io.github.sophon.discord.adapter.outbound.wiki.toWikiConfig
import io.github.sophon.discord.app.model.FrameRange
import io.github.sophon.discord.app.model.discord.DiscordConfig
import io.github.sophon.wiki.model.WikiError
import io.github.sophon.wiki.model.wiki.CoreFilters
import io.github.sophon.wiki.model.wiki.Game
import kotlin.test.Test

class WikiMappersTest {
    //region toWikiConfig
    @Test
    fun `enabled features' known games are both available and enabled`() {
        // given
        val discordConfig = discordConfig(
            featureList = listOf(
                DiscordConfig.Feature(name = "Wavu Wiki", isEnabled = true, supportedGames = listOf("Tekken_8")),
                DiscordConfig.Feature(name = "DustLoop Wiki", isEnabled = true, supportedGames = listOf("ggst", "Tekken_9")),
                DiscordConfig.Feature(name = "SuperCombo Wiki", isEnabled = false, supportedGames = listOf("Street_Fighter_6")),
            ),
        )
        val expected = setOf(Game.Tekken8, Game.GGST)

        // when
        val result = discordConfig.toWikiConfig()

        // then
        val wikiConfig = (result as Result.Success).data
        assertThat(wikiConfig.availableGameSet to wikiConfig.enabledGameSet).isEqualTo(expected to expected)
    }
    //endregion

    //region toDomainError
    @Test
    fun `unknown character keeps its query`() {
        // given
        val error = WikiError.UnknownCharacter("jni")
        val expected = "UnknownCharacter(jni)"

        // when
        val result = error.toDomainError()

        // then
        assertThat(result.toString()).isEqualTo(expected)
    }

    @Test
    fun `unknown move keeps its queries`() {
        // given
        val error = WikiError.UnknownMove("jin", "d+5")
        val expected = "UnknownMove(jin, d+5)"

        // when
        val result = error.toDomainError()

        // then
        assertThat(result.toString()).isEqualTo(expected)
    }

    @Test
    fun `other errors become wiki errors carrying the cause`() {
        // given
        val errorList = listOf(
            WikiError.DownloadError("Jin"),
            WikiError.PageNotFound("Jin_movelist"),
            WikiError.DatabaseError("wiki.db"),
            WikiError.InvalidConfig("Tekken_9"),
        )
        val expected = listOf(
            "WikiError(DownloadError(Jin))",
            "WikiError(PageNotFound(Jin_movelist))",
            "WikiError(DatabaseError(wiki.db))",
            "WikiError(InvalidConfig(Tekken_9))",
        )

        // when
        val result = errorList.map { it.toDomainError().toString() }

        // then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region toFilter
    @Test
    fun `each range type becomes its core filter`() {
        // given
        val frameRangeList = FrameRange.Type.entries.map { type -> FrameRange(type = type, from = -12, to = Int.MAX_VALUE) }
        val expected = listOf(
            CoreFilters.Startup(from = -12, to = Int.MAX_VALUE),
            CoreFilters.OnHit(from = -12, to = Int.MAX_VALUE),
            CoreFilters.OnBlock(from = -12, to = Int.MAX_VALUE),
            CoreFilters.OnCounter(from = -12, to = Int.MAX_VALUE),
        )

        // when
        val result = frameRangeList.map { it.toFilter() }

        // then
        assertThat(result).isEqualTo(expected)
    }
    //endregion
}

private fun discordConfig(featureList: List<DiscordConfig.Feature>): DiscordConfig {
    val discordConfig = DiscordConfig(
        featureList = featureList,
        adminConfig = DiscordConfig.AdminConfig(
            administratorIdList = emptyList(),
            feedbackChannelIdList = emptyList(),
            adminServerId = "777777777777777777",
        ),
        statsConfig = DiscordConfig.StatsConfig(isEnabled = false, statsChannelIdList = emptyList()),
    )
    return discordConfig
}
