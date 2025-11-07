package io.github.sophon.core.usecase

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.data.WikiDataSource
import io.github.sophon.core.domain.DataError
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.model.Move
import io.github.sophon.core.domain.usecase.DownloadMoveListUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DownloadMoveListUseCaseTest {
    // region Success Cases
    @Test
    fun `invoke returns success with mapped moves when data source succeeds`() = runTest {
        // Given
        val mockResponse = createTestMoveListDto()
        val mockDataSource = MockWikiDataSource<Unit, TestMoveListDto>()
        mockDataSource.moveListResponse = Result.Success(mockResponse)

        val useCase = DownloadMoveListUseCase(
            source = mockDataSource,
            toDomain = { dto, charName -> dto.toDomain(charName) },
            toDomainError = { this }
        )

        // When
        val result = useCase.invoke("Jin")

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val moves = (result as Result.Success).data
        assertThat(moves).hasSize(3)
        assertThat(moves[0].input).isEqualTo("1")
        assertThat(moves[1].input).isEqualTo("d/f+2")
    }

    @Test
    fun `invoke passes character name to mapper correctly`() = runTest {
        // Given
        val mockResponse = createTestMoveListDto()
        val mockDataSource = MockWikiDataSource<Unit, TestMoveListDto>()
        mockDataSource.moveListResponse = Result.Success(mockResponse)

        val useCase = DownloadMoveListUseCase(
            source = mockDataSource,
            toDomain = { dto, charName -> dto.toDomain(charName) },
            toDomainError = { this }
        )

        // When
        val result = useCase.invoke("Kazuya")

        // Then
        result as Result.Success
        val moves = result.data
        assertThat(moves[0].charName).isEqualTo("Kazuya")
        assertThat(moves[1].charName).isEqualTo("Kazuya")
        assertThat(moves[2].charName).isEqualTo("Kazuya")
    }

    @Test
    fun `invoke maps move properties correctly`() = runTest {
        // Given
        val mockResponse = TestMoveListDto(
            moves = listOf(
                TestMoveDto(
                    id = "Jin-df2",
                    input = "d/f+2",
                    damage = "15",
                    startup = "i15",
                    recovery = "r25",
                    block = "-12",
                    hit = "Launch",
                    ch = null
                )
            )
        )
        val mockDataSource = MockWikiDataSource<Unit, TestMoveListDto>()
        mockDataSource.moveListResponse = Result.Success(mockResponse)

        val useCase = DownloadMoveListUseCase(
            source = mockDataSource,
            toDomain = { dto, charName -> dto.toDomain(charName) },
            toDomainError = { this }
        )

        // When
        val result = useCase.invoke("Jin")

        // Then
        result as Result.Success
        val move = result.data.first()
        assertThat(move.charName).isEqualTo("Jin")
        assertThat(move.id).isEqualTo("jin-df2")
        assertThat(move.damage).isEqualTo("15")
        assertThat(move.startup).isEqualTo("i15")
        assertThat(move.recovery).isEqualTo("r25")
        assertThat(move.onBlock).isEqualTo("-12")
        assertThat(move.onHit).isEqualTo("Launch")
    }

    @Test
    fun `invoke returns empty list when response has no moves`() = runTest {
        // Given
        val mockResponse = TestMoveListDto(moves = emptyList())
        val mockDataSource = MockWikiDataSource<Unit, TestMoveListDto>()
        mockDataSource.moveListResponse = Result.Success(mockResponse)

        val useCase = DownloadMoveListUseCase(
            source = mockDataSource,
            toDomain = { dto, charName -> dto.toDomain(charName) },
            toDomainError = { this }
        )

        // When
        val result = useCase.invoke("Jin")

        // Then
        result as Result.Success
        assertThat(result.data).hasSize(0)
    }
    // endregion

    // region Error Cases
    @Test
    fun `invoke returns error when data source fails with UNKNOWN error`() = runTest {
        // Given
        val mockDataSource = MockWikiDataSource<Unit, TestMoveListDto>()
        mockDataSource.moveListResponse = Result.Error(DataError.Remote.UNKNOWN)

        val useCase = DownloadMoveListUseCase(
            source = mockDataSource,
            toDomain = { dto, charName -> dto.toDomain(charName) },
            toDomainError = { this }
        )

        // When
        val result = useCase.invoke("Jin")

        // Then
        assertThat(result).isInstanceOf(Result.Error::class)
        val error = (result as Result.Error).error
        assertThat(error).isEqualTo(DataError.Remote.UNKNOWN)
    }

    @Test
    fun `invoke returns error when data source fails with NO_INTERNET error`() = runTest {
        // Given
        val mockDataSource = MockWikiDataSource<Unit, TestMoveListDto>()
        mockDataSource.moveListResponse = Result.Error(DataError.Remote.NO_INTERNET)

        val useCase = DownloadMoveListUseCase(
            source = mockDataSource,
            toDomain = { dto, charName -> dto.toDomain(charName) },
            toDomainError = { this }
        )

        // When
        val result = useCase.invoke("Jin")

        // Then
        assertThat(result).isInstanceOf(Result.Error::class)
        val error = (result as Result.Error).error
        assertThat(error).isEqualTo(DataError.Remote.NO_INTERNET)
    }

    @Test
    fun `invoke returns error when data source fails with REQUEST_TIMEOUT error`() = runTest {
        // Given
        val mockDataSource = MockWikiDataSource<Unit, TestMoveListDto>()
        mockDataSource.moveListResponse = Result.Error(DataError.Remote.REQUEST_TIMEOUT)

        val useCase = DownloadMoveListUseCase(
            source = mockDataSource,
            toDomain = { dto, charName -> dto.toDomain(charName) },
            toDomainError = { this }
        )

        // When
        val result = useCase.invoke("Jin")

        // Then
        assertThat(result).isInstanceOf(Result.Error::class)
        val error = (result as Result.Error).error
        assertThat(error).isEqualTo(DataError.Remote.REQUEST_TIMEOUT)
    }

    @Test
    fun `invoke returns error when data source fails with SERVER_ERROR`() = runTest {
        // Given
        val mockDataSource = MockWikiDataSource<Unit, TestMoveListDto>()
        mockDataSource.moveListResponse = Result.Error(DataError.Remote.SERVER_ERROR)

        val useCase = DownloadMoveListUseCase(
            source = mockDataSource,
            toDomain = { dto, charName -> dto.toDomain(charName) },
            toDomainError = { this }
        )

        // When
        val result = useCase.invoke("Jin")

        // Then
        assertThat(result).isInstanceOf(Result.Error::class)
        val error = (result as Result.Error).error
        assertThat(error).isEqualTo(DataError.Remote.SERVER_ERROR)
    }
    // endregion

    // region Test Data
    private fun createTestMoveListDto() = TestMoveListDto(
        moves = listOf(
            TestMoveDto(
                id = "Jin-1",
                input = "1",
                damage = "5",
                startup = "i10",
                recovery = "r19",
                block = "+1",
                hit = "+8",
                ch = null
            ),
            TestMoveDto(
                id = "Jin-df2",
                input = "d/f+2",
                damage = "15",
                startup = "i15",
                recovery = "r25",
                block = "-12",
                hit = "Launch",
                ch = null
            ),
            TestMoveDto(
                id = "Jin-f,n,d,df+2",
                input = "f,n,d/f+2",
                damage = "25",
                startup = "i13",
                recovery = null,
                block = "+5",
                hit = "Launch",
                ch = null
            )
        )
    )
    // endregion

    // region Test DTOs
    data class TestMoveListDto(
        val moves: List<TestMoveDto>
    )

    data class TestMoveDto(
        val id: String,
        val input: String,
        val damage: String,
        val startup: String,
        val recovery: String?,
        val block: String,
        val hit: String,
        val ch: String?
    )
    // endregion

    // region Test Mapper
    private fun TestMoveListDto.toDomain(charName: String): List<Move> {
        return moves.map { dto ->
            Move(
                charName = charName,
                id = dto.id.lowercase(),
                input = dto.input,
                name = null,
                damage = dto.damage,
                startup = dto.startup,
                recovery = dto.recovery,
                onBlock = dto.block,
                onHit = dto.hit,
                onCH = dto.ch,
                notes = emptyList(),
                aliases = emptyList(),
                videoId = null,
                t8Properties = null
            )
        }
    }
    // endregion

    // region Mock Data Source
    private class MockWikiDataSource<C, M> : WikiDataSource<C, M> {
        var moveListResponse: Result<M, DataError.Remote>? = null

        override suspend fun downloadCharacterList(): Result<C, DataError.Remote> {
            throw NotImplementedError("Not needed for these tests")
        }

        override suspend fun downloadMoveListFor(charName: String): Result<M, DataError.Remote> {
            return moveListResponse ?: Result.Error(DataError.Remote.UNKNOWN)
        }
    }
    // endregion
}