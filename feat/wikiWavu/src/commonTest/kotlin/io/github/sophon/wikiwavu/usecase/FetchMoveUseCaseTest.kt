package io.github.sophon.wikiwavu.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.model.Move
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.Test

class FetchMoveUseCaseTest {

    //region Test Setup
    private class FakeMoveListDB : MoveListDB {
        var resultToReturn: Result<Move, WikiError>? = null
        var capturedCharName: String? = null
        var capturedMoveQuery: String? = null

        override suspend fun fetchMoveListFor(charName: String): Result<List<Move>, WikiError> {
            throw NotImplementedError()
        }

        override suspend fun fetchMoveDataFor(charName: String, moveQuery: String): Result<Move, WikiError> {
            capturedCharName = charName
            capturedMoveQuery = moveQuery
            return resultToReturn ?: throw IllegalStateException("resultToReturn not set")
        }

        override suspend fun insertMoveList(charName: String, moveList: List<Move>): Result<Unit, WikiError> {
            throw NotImplementedError()
        }

        override suspend fun wipe(): Result<Unit, WikiError> {
            throw NotImplementedError()
        }

        override suspend fun getLastInsertTimeStamp(): Result<Instant?, WikiError> {
            throw NotImplementedError()
        }
    }

    private fun createTestMove(
        charName: String = "Jin",
        input: String = "1,2"
    ) = Move(
        charName = charName,
        id = "${charName}_$input",
        input = input,
        damage = "10",
        startup = "i10",
        onBlock = "-1",
        onHit = "+5",
        onCH = "+8",
        name = "Test Move",
        recovery = "r20",
        notes = emptyList(),
        aliases = emptyList(),
        videoId = null,
        t8Properties = null,
        sf6Properties = null
    )
    //endregion

    //region Tests
    @Test
    fun `invoke - given move exists - when fetching - then returns success with move`() = runTest {
        // Given
        val fakeDb = FakeMoveListDB()
        val expectedMove = createTestMove("Kazuya", "df2")
        fakeDb.resultToReturn = Result.Success(expectedMove)
        val useCase = FetchMoveUseCase(fakeDb)

        // When
        val result = useCase.invoke("Kazuya", "df2")

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        assertThat((result as Result.Success).data).isEqualTo(expectedMove)
    }

    @Test
    fun `invoke - given move not found - when fetching - then returns error`() = runTest {
        // Given
        val fakeDb = FakeMoveListDB()
        val expectedError = WikiError.UnknownMove("Move not found")
        fakeDb.resultToReturn = Result.Error(expectedError)
        val useCase = FetchMoveUseCase(fakeDb)

        // When
        val result = useCase.invoke("Jin", "unknown")

        // Then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat((result as Result.Error).error).isEqualTo(expectedError)
    }

    @Test
    fun `invoke - given database error - when fetching - then returns error`() = runTest {
        // Given
        val fakeDb = FakeMoveListDB()
        val expectedError = WikiError.DatabaseError("Database connection failed")
        fakeDb.resultToReturn = Result.Error(expectedError)
        val useCase = FetchMoveUseCase(fakeDb)

        // When
        val result = useCase.invoke("Jin", "df2")

        // Then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat((result as Result.Error).error).isEqualTo(expectedError)
    }

    @Test
    fun `invoke - given move query with spaces and uppercase - when fetching - then passes cleaned input to db`() = runTest {
        // Given
        val fakeDb = FakeMoveListDB()
        val expectedMove = createTestMove("Jin", "df2")
        fakeDb.resultToReturn = Result.Success(expectedMove)
        val useCase = FetchMoveUseCase(fakeDb)

        // When
        useCase.invoke("Jin", "  DF, 2  ")

        // Then
        assertThat(fakeDb.capturedCharName).isEqualTo("Jin")
        assertThat(fakeDb.capturedMoveQuery).isEqualTo("df2")
    }

    @Test
    fun `invoke - given move query with plus notation - when fetching - then passes cleaned input to db`() = runTest {
        // Given
        val fakeDb = FakeMoveListDB()
        val expectedMove = createTestMove("Kazuya", "wsdf2")
        fakeDb.resultToReturn = Result.Success(expectedMove)
        val useCase = FetchMoveUseCase(fakeDb)

        // When
        useCase.invoke("Kazuya", "WS+DF+2")

        // Then
        assertThat(fakeDb.capturedCharName).isEqualTo("Kazuya")
        assertThat(fakeDb.capturedMoveQuery).isEqualTo("wsdf2")
    }
    //endregion
}