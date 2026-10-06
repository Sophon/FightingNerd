package io.github.sophon.botdiscord.app.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.response.BotResponse
import io.github.sophon.discord.app.model.response.GlossaryResponse
import io.github.sophon.discord.app.outPort.GlossaryPort
import io.github.sophon.discord.app.outPort.RefreshGlossaryPort
import io.github.sophon.discord.app.service.GlossaryServiceImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class GlossaryServiceTest {
    @Test
    fun `best match is the first search result`() = runTest {
        // given
        val expected = Result.Success(plusFrames)
        val service = glossaryService(
            coroutineScope = backgroundScope,
            searchResult = Result.Success(listOf(plusFrames, minusFrames)),
        )

        // when
        val result = service.findTerm("frames")

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `no search result is an unknown term`() = runTest {
        // given
        val service = glossaryService(coroutineScope = backgroundScope, searchResult = Result.Success(emptyList()))

        // when
        val result = service.findTerm("okizeem")

        // then
        val error = (result as Result.Error).error
        assertThat(error).isInstanceOf(BotError.GlossaryTermNotFound::class)
    }

    @Test
    fun `failed search is returned`() = runTest {
        // given
        val expected = Result.Error(BotError.EmptyGlossary())
        val service = glossaryService(coroutineScope = backgroundScope, searchResult = expected)

        // when
        val result = service.findTerm("okizeme")

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `empty glossary starts a refresh`() = runTest {
        // given
        val refreshGlossaryPort = FakeRefreshGlossaryPort()
        val service = glossaryService(
            coroutineScope = backgroundScope,
            searchResult = Result.Error(BotError.EmptyGlossary()),
            refreshGlossaryPort = refreshGlossaryPort,
        )

        // when
        service.findTerm("okizeme")
        runCurrent()

        // then
        assertThat(refreshGlossaryPort.refreshCount).isEqualTo(1)
    }

    @Test
    fun `other errors don't start a refresh`() = runTest {
        // given
        val refreshGlossaryPort = FakeRefreshGlossaryPort()
        val service = glossaryService(
            coroutineScope = backgroundScope,
            searchResult = Result.Error(BotError.DatabaseError()),
            refreshGlossaryPort = refreshGlossaryPort,
        )

        // when
        service.findTerm("okizeme")
        runCurrent()

        // then
        assertThat(refreshGlossaryPort.refreshCount).isEqualTo(0)
    }

    @Test
    fun `unknown term doesn't start a refresh`() = runTest {
        // given
        val refreshGlossaryPort = FakeRefreshGlossaryPort()
        val service = glossaryService(
            coroutineScope = backgroundScope,
            searchResult = Result.Success(emptyList()),
            refreshGlossaryPort = refreshGlossaryPort,
        )

        // when
        service.findTerm("okizeem")
        runCurrent()

        // then
        assertThat(refreshGlossaryPort.refreshCount).isEqualTo(0)
    }


    private class FakeGlossaryPort(
        private val result: Result<List<GlossaryResponse>, BotError>,
    ): GlossaryPort {
        override suspend fun search(query: String): Result<List<GlossaryResponse>, BotError> = result
    }

    private class FakeRefreshGlossaryPort: RefreshGlossaryPort {
        var refreshCount = 0
            private set

        override suspend fun refresh(): EmptyResult<BotError> {
            refreshCount++
            return Result.Success(Unit)
        }
    }

    private fun glossaryService(
        coroutineScope: CoroutineScope,
        searchResult: Result<List<GlossaryResponse>, BotError>,
        refreshGlossaryPort: FakeRefreshGlossaryPort = FakeRefreshGlossaryPort(),
    ): GlossaryServiceImpl {
        val service = GlossaryServiceImpl(
            glossaryPort = FakeGlossaryPort(searchResult),
            refreshGlossaryPort = refreshGlossaryPort,
            coroutineScope = coroutineScope,
        )
        return service
    }
}


private val glossaryDataSource = BotResponse.DataSource(
    name = "Infil's Fighting Game Glossary",
    iconUrl = "https://glossary.infil.net/favicon.ico",
    color = 0xDAA06D,
)
private val plusFrames = GlossaryResponse(
    dataSource = glossaryDataSource,
    term = "Plus Frames",
    definition = "Having frame advantage after a move.",
    jpTranslationList = listOf("有利フレーム"),
    termUrl = "https://glossary.infil.net/?t=Plus%20Frames",
    videoUrl = null,
    imageUrl = null,
)
private val minusFrames = plusFrames.copy(
    term = "Minus Frames",
    definition = "Having frame disadvantage after a move.",
    jpTranslationList = listOf("不利フレーム"),
    termUrl = "https://glossary.infil.net/?t=Minus%20Frames",
)
