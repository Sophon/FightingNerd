package com.example.wikiwavu.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.example.core.domain.EmptyResult
import com.example.core.domain.Result
import com.example.wikiwavu.WavuError
import com.example.wikiwavu.data.MoveListDB
import com.example.wikiwavu.domain.model.Move
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class FetchMoveDataUseCaseTest {
    private lateinit var mockDb: MockMoveListDB
    private lateinit var useCase: FetchMoveDataUseCase

    @BeforeTest
    fun setup() {
        mockDb = MockMoveListDB()
        useCase = FetchMoveDataUseCase(mockDb)
    }

    //region Success Cases
    @Test
    fun `invoke returns success when move is found in database`() = runTest {
        // Given
        val charName = "Yoshimitsu"
        val moveQuery = "1"
        val expectedMove = createTestMove(
            charName = charName,
            id = "1",
            input = "1",
            name = "Jab"
        )
        mockDb.mockResponse = Result.Success(expectedMove)

        // When
        val result = useCase.invoke(charName, moveQuery)

        // Then
        assertThat(result).isNotNull()
        result as Result.Success
        assertThat(result.data).isEqualTo(expectedMove)
        assertThat(mockDb.lastCharName).isEqualTo(charName)
        assertThat(mockDb.lastMoveQuery).isEqualTo("1")
    }

    @Test
    fun `invoke cleans move input before querying database`() = runTest {
        // Given
        val charName = "Kazuya"
        val moveQuery = "1, 1, 2" // Has spaces and commas
        val expectedMove = createTestMove(
            charName = charName,
            id = "112",
            input = "1,1,2"
        )
        mockDb.mockResponse = Result.Success(expectedMove)

        // When
        val result = useCase.invoke(charName, moveQuery)

        // Then
        result as Result.Success
        // Verify the move query was cleaned: "1, 1, 2" -> "112"
        assertThat(mockDb.lastMoveQuery).isEqualTo("112")
    }

    @Test
    fun `invoke cleans move input with special motion inputs`() = runTest {
        // Given
        val charName = "Jin"
        val moveQuery = "f, f+3" // Should become "ff3"
        val expectedMove = createTestMove(
            charName = charName,
            id = "ff3",
            input = "f,f+3"
        )
        mockDb.mockResponse = Result.Success(expectedMove)

        // When
        val result = useCase.invoke(charName, moveQuery)

        // Then
        result as Result.Success
        assertThat(mockDb.lastMoveQuery).isEqualTo("ff3")
    }

    @Test
    fun `invoke cleans move input with ws notation`() = runTest {
        // Given
        val charName = "Paul"
        val moveQuery = "ws+2" // Should become "ws2"
        val expectedMove = createTestMove(
            charName = charName,
            id = "ws2",
            input = "WS+2"
        )
        mockDb.mockResponse = Result.Success(expectedMove)

        // When
        val result = useCase.invoke(charName, moveQuery)

        // Then
        result as Result.Success
        assertThat(mockDb.lastMoveQuery).isEqualTo("ws2")
    }

    @Test
    fun `invoke cleans move input with d notation`() = runTest {
        // Given
        val charName = "Bryan"
        val moveQuery = "d/f+2" // Should become "df2"
        val expectedMove = createTestMove(
            charName = charName,
            id = "df2",
            input = "d/f+2"
        )
        mockDb.mockResponse = Result.Success(expectedMove)

        // When
        val result = useCase.invoke(charName, moveQuery)

        // Then
        result as Result.Success
        assertThat(mockDb.lastMoveQuery).isEqualTo("df2")
    }

    @Test
    fun `invoke cleans move input converting fff to wr`() = runTest {
        // Given
        val charName = "King"
        val moveQuery = "f, f, f+2" // Should become "wr2"
        val expectedMove = createTestMove(
            charName = charName,
            id = "wr2",
            input = "fff+2"
        )
        mockDb.mockResponse = Result.Success(expectedMove)

        // When
        val result = useCase.invoke(charName, moveQuery)

        // Then
        result as Result.Success
        assertThat(mockDb.lastMoveQuery).isEqualTo("wr2")
    }

    @Test
    fun `invoke cleans move input with crouch dash notation`() = runTest {
        // Given
        val charName = "Kazuya"
        val moveQuery = "f, n, d, d/f+2" // Should become "cd2"
        val expectedMove = createTestMove(
            charName = charName,
            id = "cd2",
            input = "f,n,d,d/f+2"
        )
        mockDb.mockResponse = Result.Success(expectedMove)

        // When
        val result = useCase.invoke(charName, moveQuery)

        // Then
        result as Result.Success
        assertThat(mockDb.lastMoveQuery).isEqualTo("cd2")
    }

    @Test
    fun `invoke cleans move input converting heat notation`() = runTest {
        // Given
        val charName = "Jin"
        val moveQuery = "heat.d/f+2" // Should become "h.df2"
        val expectedMove = createTestMove(
            charName = charName,
            id = "h.df2",
            input = "heat.d/f+2"
        )
        mockDb.mockResponse = Result.Success(expectedMove)

        // When
        val result = useCase.invoke(charName, moveQuery)

        // Then
        result as Result.Success
        assertThat(mockDb.lastMoveQuery).isEqualTo("h.df2")
    }

    @Test
    fun `invoke cleans move input converting rage notation`() = runTest {
        // Given
        val charName = "Paul"
        val moveQuery = "rage.d/f+2" // Should become "r.df2"
        val expectedMove = createTestMove(
            charName = charName,
            id = "r.df2",
            input = "rage.d/f+2"
        )
        mockDb.mockResponse = Result.Success(expectedMove)

        // When
        val result = useCase.invoke(charName, moveQuery)

        // Then
        result as Result.Success
        assertThat(mockDb.lastMoveQuery).isEqualTo("r.df2")
    }

    @Test
    fun `invoke handles complex move input with multiple notations`() = runTest {
        // Given
        val charName = "Yoshimitsu"
        val moveQuery = "KIN.1+2" // Should become "kin1+2"
        val expectedMove = createTestMove(
            charName = charName,
            id = "kin.1+2",
            input = "KIN.1+2"
        )
        mockDb.mockResponse = Result.Success(expectedMove)

        // When
        val result = useCase.invoke(charName, moveQuery)

        // Then
        result as Result.Success
        assertThat(mockDb.lastMoveQuery).isEqualTo("kin.1+2")
    }

    @Test
    fun `invoke handles move input with uppercase letters`() = runTest {
        // Given
        val charName = "Jin"
        val moveQuery = "EWHF" // Should become lowercase "ewhf"
        val expectedMove = createTestMove(
            charName = charName,
            id = "ewhf",
            input = "EWHF"
        )
        mockDb.mockResponse = Result.Success(expectedMove)

        // When
        val result = useCase.invoke(charName, moveQuery)

        // Then
        result as Result.Success
        assertThat(mockDb.lastMoveQuery).isEqualTo("ewhf")
    }

    @Test
    fun `invoke handles move input with mixed case`() = runTest {
        // Given
        val charName = "Yoshimitsu"
        val moveQuery = "IND.u+1+2" // Should become "indu+1+2"
        val expectedMove = createTestMove(
            charName = charName,
            id = "ind.u1+2",
            input = "IND.u+1+2"
        )
        mockDb.mockResponse = Result.Success(expectedMove)

        // When
        val result = useCase.invoke(charName, moveQuery)

        // Then
        result as Result.Success
        assertThat(mockDb.lastMoveQuery).isEqualTo("ind.u1+2")
    }
    //endregion

    //region Error Cases
    @Test
    fun `invoke returns error when move is not found in database`() = runTest {
        // Given
        val charName = "Yoshimitsu"
        val moveQuery = "999"
        mockDb.mockResponse = Result.Error(WavuError.UNKNOWN_MOVE)

        // When
        val result = useCase.invoke(charName, moveQuery)

        // Then
        assertThat(result).isNotNull()
        result as Result.Error
        assertThat(result.error).isEqualTo(WavuError.UNKNOWN_MOVE)
    }

    @Test
    fun `invoke returns error when character is not found`() = runTest {
        // Given
        val charName = "NonExistentCharacter"
        val moveQuery = "1"
        mockDb.mockResponse = Result.Error(WavuError.UNKNOWN_CHARACTER)

        // When
        val result = useCase.invoke(charName, moveQuery)

        // Then
        result as Result.Error
        assertThat(result.error).isEqualTo(WavuError.UNKNOWN_CHARACTER)
    }

    @Test
    fun `invoke returns error when database operation fails`() = runTest {
        // Given
        val charName = "Jin"
        val moveQuery = "df2"
        mockDb.mockResponse = Result.Error(WavuError.DOWNLOAD_ERROR)

        // When
        val result = useCase.invoke(charName, moveQuery)

        // Then
        result as Result.Error
        assertThat(result.error).isEqualTo(WavuError.DOWNLOAD_ERROR)
    }

    @Test
    fun `invoke handles empty move query`() = runTest {
        // Given
        val charName = "Jin"
        val moveQuery = ""
        val expectedMove = createTestMove(
            charName = charName,
            id = "",
            input = ""
        )
        mockDb.mockResponse = Result.Success(expectedMove)

        // When
        val result = useCase.invoke(charName, moveQuery)

        // Then
        result as Result.Success
        assertThat(mockDb.lastMoveQuery).isEqualTo("")
    }

    @Test
    fun `invoke handles move query with only spaces`() = runTest {
        // Given
        val charName = "Jin"
        val moveQuery = "   " // Should become ""
        mockDb.mockResponse = Result.Error(WavuError.UNKNOWN_MOVE)

        // When
        useCase.invoke(charName, moveQuery)

        // Then
        assertThat(mockDb.lastMoveQuery).isEqualTo("")
    }

    @Test
    fun `invoke handles move query with only special characters that get removed`() = runTest {
        // Given
        val charName = "Jin"
        val moveQuery = ", , /" // Should become ""
        mockDb.mockResponse = Result.Error(WavuError.UNKNOWN_MOVE)

        // When
        useCase.invoke(charName, moveQuery)

        // Then
        assertThat(mockDb.lastMoveQuery).isEqualTo("")
    }

    @Test
    fun `invoke preserves character name exactly as passed`() = runTest {
        // Given
        val charName = "YOSHIMITSU" // Uppercase
        val moveQuery = "1"
        val expectedMove = createTestMove(
            charName = charName,
            id = "1",
            input = "1"
        )
        mockDb.mockResponse = Result.Success(expectedMove)

        // When
        val result = useCase.invoke(charName, moveQuery)

        // Then
        result as Result.Success
        assertThat(mockDb.lastCharName).isEqualTo("YOSHIMITSU")
    }
    //endregion

    //region Helper Methods
    private fun createTestMove(
        charName: String,
        id: String,
        input: String,
        name: String? = null,
        level: String? = null,
        damage: String? = null,
        startup: String? = null,
    ) = Move(
        charName = charName,
        id = id,
        input = input,
        level = level,
        name = name,
        parent = null,
        damage = damage,
        startup = startup,
        recoveryOnWhiff = null,
        totalFrames = null,
        crushes = listOf(),
        onBlock = null,
        onHit = null,
        onCH = null,
        notes = listOf(),
        aliases = listOf(),
        image = null,
        videoId = null,
        alt = null,
        isHeat = false,
        isPowerCrush = false,
        isHoming = false,
    )
    //endregion

    //region Mock Database
    private class MockMoveListDB : MoveListDB {
        var mockResponse: Result<Move, WavuError>? = null
        var lastCharName: String? = null
        var lastMoveQuery: String? = null

        override suspend fun fetchMoveListFor(charName: String): Result<Map<String, Move>, WavuError> {
            // Not used in FetchMoveDataUseCase
            return Result.Error(WavuError.UNKNOWN_CHARACTER)
        }

        override suspend fun fetchMoveDataFor(
            charName: String,
            moveQuery: String
        ): Result<Move, WavuError> {
            lastCharName = charName
            lastMoveQuery = moveQuery
            return mockResponse ?: Result.Error(WavuError.UNKNOWN_MOVE)
        }

        override suspend fun insertMoveList(
            charName: String, moveList: List<Move>
        ): EmptyResult<WavuError> {
            // Not used in FetchMoveDataUseCase
            return Result.Success(Unit)
        }

        override suspend fun wipe(): EmptyResult<WavuError> {
            //not used
            return Result.Success(Unit)
        }
    }
    //endregion
}