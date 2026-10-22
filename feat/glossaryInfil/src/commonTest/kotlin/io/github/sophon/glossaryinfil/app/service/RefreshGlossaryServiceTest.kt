package io.github.sophon.glossaryinfil.app.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.glossaryinfil.app.outPort.FetchGlossaryPort
import io.github.sophon.glossaryinfil.app.outPort.ReplaceGlossaryPort
import io.github.sophon.glossaryinfil.model.GlossaryError
import io.github.sophon.glossaryinfil.model.GlossaryItem
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class RefreshGlossaryServiceTest {
    @Test
    fun `downloaded glossary replaces the stored one`() = runTest {
        // given
        val expected = listOf(antiAir, okizeme)
        val replacePort = FakeReplaceGlossaryPort(storedItemList = listOf(whiffPunish))
        val service = RefreshGlossaryService(
            fetchGlossaryPort = FakeFetchGlossaryPort(Result.Success(expected)),
            replaceGlossaryPort = replacePort,
        )

        // when
        val result = service.invoke()

        // then
        assertThat(result).isEqualTo(Result.Success(Unit))
        assertThat(replacePort.storedItemList).isEqualTo(expected)
    }

    @Test
    fun `failed download keeps the stored glossary`() = runTest {
        // given
        val expected = listOf(whiffPunish)
        val replacePort = FakeReplaceGlossaryPort(storedItemList = expected)
        val service = RefreshGlossaryService(
            fetchGlossaryPort = FakeFetchGlossaryPort(Result.Error(DataError.Remote.SERVER_ERROR)),
            replaceGlossaryPort = replacePort,
        )

        // when
        val result = service.invoke()

        // then
        assertThat(result).isEqualTo(Result.Error(GlossaryError.Download(DataError.Remote.SERVER_ERROR)))
        assertThat(replacePort.storedItemList).isEqualTo(expected)
    }

    @Test
    fun `empty download keeps the stored glossary`() = runTest {
        // given
        val expected = listOf(whiffPunish)
        val replacePort = FakeReplaceGlossaryPort(storedItemList = expected)
        val service = RefreshGlossaryService(
            fetchGlossaryPort = FakeFetchGlossaryPort(Result.Success(emptyList())),
            replaceGlossaryPort = replacePort,
        )

        // when
        val result = service.invoke()

        // then
        assertThat(result).isEqualTo(Result.Error(GlossaryError.EmptyGlossary))
        assertThat(replacePort.storedItemList).isEqualTo(expected)
    }

    @Test
    fun `failed save is a database error`() = runTest {
        // given
        val service = RefreshGlossaryService(
            fetchGlossaryPort = FakeFetchGlossaryPort(Result.Success(listOf(antiAir))),
            replaceGlossaryPort = FakeReplaceGlossaryPort(error = DataError.Local.UNKNOWN),
        )

        // when
        val result = service.invoke()

        // then
        assertThat(result).isEqualTo(Result.Error(GlossaryError.Database(DataError.Local.UNKNOWN)))
    }


    private class FakeFetchGlossaryPort(
        private val result: Result<List<GlossaryItem>, DataError.Remote>,
    ): FetchGlossaryPort {
        override suspend fun fetch(): Result<List<GlossaryItem>, DataError.Remote> = result
    }

    private class FakeReplaceGlossaryPort(
        var storedItemList: List<GlossaryItem> = emptyList(),
        private val error: DataError.Local? = null,
    ): ReplaceGlossaryPort {
        override suspend fun replace(itemList: List<GlossaryItem>): EmptyResult<DataError.Local> {
            if (error != null) return Result.Error(error)

            storedItemList = itemList
            return Result.Success(Unit)
        }
    }
}


private val antiAir = GlossaryItem(
    term = "Anti-air",
    definition = "An attack that hits a jumping opponent",
    altTerm = listOf("AA"),
    url = GlossaryItem.Url(term = "https://glossary.infil.net/?t=Anti-air"),
)
private val okizeme = GlossaryItem(
    term = "Okizeme",
    definition = "Offense against a waking up opponent",
    altTerm = listOf("Oki"),
    url = GlossaryItem.Url(term = "https://glossary.infil.net/?t=Okizeme"),
)
private val whiffPunish = GlossaryItem(
    term = "Whiff Punish",
    definition = "Hitting an opponent during the recovery of a missed attack",
    url = GlossaryItem.Url(term = "https://glossary.infil.net/?t=Whiff%20Punish"),
)
