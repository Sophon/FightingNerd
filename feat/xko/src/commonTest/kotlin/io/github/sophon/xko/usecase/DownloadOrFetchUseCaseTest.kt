package io.github.sophon.xko.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.domain.DataError
import io.github.sophon.core.domain.Result
import io.github.sophon.xko.data.MoveDto
import io.github.sophon.xko.data.MoveListResponseDto
import io.github.sophon.xko.data.XkoWikiDataSource
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DownloadOrFetchUseCaseTest {
    //region First Call Downloads Data
    @Test
    fun `invoke downloads data on first call`() = runTest {
        val dto = createSampleDto()
        val dataSource = FakeXkoWikiDataSource().apply {
            result = Result.Success(dto)
        }
        val useCase = DownloadOrFetchUseCase(dataSource)

        val result = useCase.invoke()

        assertThat(dataSource.callCount).isEqualTo(1)
        assertThat(result).isInstanceOf(Result.Success::class)
    }
    //endregion

    //region Subsequent Calls Return Cached Data
    @Test
    fun `invoke returns cached data on subsequent calls`() = runTest {
        val dto = createSampleDto()
        val dataSource = FakeXkoWikiDataSource().apply {
            result = Result.Success(dto)
        }
        val useCase = DownloadOrFetchUseCase(dataSource)

        useCase.invoke()
        val result = useCase.invoke()

        assertThat(dataSource.callCount).isEqualTo(1)
        assertThat(result).isInstanceOf(Result.Success::class)
    }
    //endregion

    //region Download Failure Returns Error
    @Test
    fun `invoke returns error when download fails`() = runTest {
        val dataSource = FakeXkoWikiDataSource().apply {
            result = Result.Error(DataError.Remote.SERVER_ERROR)
        }
        val useCase = DownloadOrFetchUseCase(dataSource)

        val result = useCase.invoke()

        assertThat(result).isInstanceOf(Result.Error::class)
    }
    //endregion

    //region Clear Cache Forces Redownload
    @Test
    fun `clearCache forces redownload on next invoke`() = runTest {
        val dto = createSampleDto()
        val dataSource = FakeXkoWikiDataSource().apply {
            result = Result.Success(dto)
        }
        val useCase = DownloadOrFetchUseCase(dataSource)

        useCase.invoke()
        useCase.clearCache()
        useCase.invoke()

        assertThat(dataSource.callCount).isEqualTo(2)
    }
    //endregion

    //region Cache Not Populated On Error
    @Test
    fun `invoke does not cache data when download fails`() = runTest {
        val dto = createSampleDto()
        val dataSource = FakeXkoWikiDataSource().apply {
            result = Result.Error(DataError.Remote.SERVER_ERROR)
        }
        val useCase = DownloadOrFetchUseCase(dataSource)

        useCase.invoke()
        dataSource.result = Result.Success(dto)
        useCase.invoke()

        assertThat(dataSource.callCount).isEqualTo(2)
    }
    //endregion

    //region Test Helpers
    private fun createSampleDto(): MoveListResponseDto {
        return MoveListResponseDto(
            bucketQuery = "test query",
            bucket = listOf(
                MoveDto(
                    pageName = "Ahri",
                    input = "5L",
                    damage = "30",
                    guard = "LHA",
                    startup = "6",
                    active = "3",
                    recovery = "12",
                    onBlock = "-2",
                    cancel = "N,SP,SU",
                    invuln = null
                ),
                MoveDto(
                    pageName = "Ahri",
                    input = "5M",
                    damage = "50",
                    guard = "LHA",
                    startup = "10",
                    active = "5",
                    recovery = "16",
                    onBlock = "-4",
                    cancel = "N,SP,SU",
                    invuln = null
                )
            )
        )
    }
    //endregion
}

private class FakeXkoWikiDataSource : XkoWikiDataSource {
    var result: Result<MoveListResponseDto, DataError.Remote>? = null
    var callCount = 0

    override suspend fun downloadMoveList(): Result<MoveListResponseDto, DataError.Remote> {
        callCount++
        return result ?: error("Result not set in fake")
    }
}