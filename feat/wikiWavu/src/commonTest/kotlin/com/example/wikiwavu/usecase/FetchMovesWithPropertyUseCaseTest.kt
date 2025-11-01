package com.example.wikiwavu.usecase

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.example.core.domain.EmptyResult
import com.example.core.domain.Result
import com.example.wikiwavu.WavuError
import com.example.wikiwavu.data.MoveListDB
import com.example.wikiwavu.domain.model.Move
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FetchMovesWithPropertyUseCaseTest {
    private lateinit var mockDb: MockMoveListDB
    private lateinit var useCase: FetchMovesWithPropertyUseCase

    @BeforeTest
    fun setup() {
        mockDb = MockMoveListDB()
        useCase = FetchMovesWithPropertyUseCase(mockDb)
    }

    //region Success Cases - Heat Moves
    @Test
    fun `invoke returns only heat moves when filtering by isHeat`() = runTest {
        // Given
        val charName = "Jin"
        val moves = listOf(
            createTestMove(id = "df2", isHeat = true),
            createTestMove(id = "bf23", isHeat = true),
            createTestMove(id = "1", isHeat = false),
            createTestMove(id = "2", isHeat = false)
        )
        mockDb.mockResponse = Result.Success(moves)

        // When
        val result = useCase.invoke(charName) { it.properties.isHeat == true }

        // Then
        assertThat(result).isNotNull()
        result as Result.Success
        assertThat(result.data).hasSize(2)
        assertThat(result.data.all { it.properties.isHeat == true }).isEqualTo(true)
        assertThat(result.data.map { it.id }).isEqualTo(listOf("df2", "bf23"))
    }

    @Test
    fun `invoke returns empty list when no heat moves exist`() = runTest {
        // Given
        val charName = "Jin"
        val moves = listOf(
            createTestMove(id = "1", isHeat = false),
            createTestMove(id = "2", isHeat = false),
            createTestMove(id = "3", isHeat = false)
        )
        mockDb.mockResponse = Result.Success(moves)

        // When
        val result = useCase.invoke(charName) { it.properties.isHeat == true }

        // Then
        result as Result.Success
        assertThat(result.data).isEmpty()
    }

    @Test
    fun `invoke returns all moves when all are heat moves`() = runTest {
        // Given
        val charName = "Jin"
        val moves = listOf(
            createTestMove(id = "df2", isHeat = true),
            createTestMove(id = "bf23", isHeat = true),
            createTestMove(id = "f4", isHeat = true)
        )
        mockDb.mockResponse = Result.Success(moves)

        // When
        val result = useCase.invoke(charName) { it.properties.isHeat == true }

        // Then
        result as Result.Success
        assertThat(result.data).hasSize(3)
        assertThat(result.data.all { it.properties.isHeat == true }).isEqualTo(true)
    }
    //endregion

    //region Success Cases - Power Crush Moves
    @Test
    fun `invoke returns only power crush moves when filtering by isPowerCrush`() = runTest {
        // Given
        val charName = "Paul"
        val moves = listOf(
            createTestMove(id = "f23", isPowerCrush = true),
            createTestMove(id = "df2", isPowerCrush = false),
            createTestMove(id = "qcf2", isPowerCrush = true),
            createTestMove(id = "1", isPowerCrush = false)
        )
        mockDb.mockResponse = Result.Success(moves)

        // When
        val result = useCase.invoke(charName) { it.properties.isPowerCrush == true }

        // Then
        result as Result.Success
        assertThat(result.data).hasSize(2)
        assertThat(result.data.all { it.properties.isPowerCrush == true }).isEqualTo(true)
        assertThat(result.data.map { it.id }).isEqualTo(listOf("f23", "qcf2"))
    }

    @Test
    fun `invoke returns empty list when no power crush moves exist`() = runTest {
        // Given
        val charName = "Paul"
        val moves = listOf(
            createTestMove(id = "1", isPowerCrush = false),
            createTestMove(id = "df2", isPowerCrush = false)
        )
        mockDb.mockResponse = Result.Success(moves)

        // When
        val result = useCase.invoke(charName) { it.properties.isPowerCrush == true }

        // Then
        result as Result.Success
        assertThat(result.data).isEmpty()
    }
    //endregion

    //region Success Cases - Homing Moves
    @Test
    fun `invoke returns only homing moves when filtering by isHoming`() = runTest {
        // Given
        val charName = "Yoshimitsu"
        val moves = listOf(
            createTestMove(id = "1+3", isHoming = true),
            createTestMove(id = "uf3", isHoming = true),
            createTestMove(id = "1", isHoming = false),
            createTestMove(id = "df2", isHoming = false),
            createTestMove(id = "ff3", isHoming = false)
        )
        mockDb.mockResponse = Result.Success(moves)

        // When
        val result = useCase.invoke(charName) { it.properties.isHoming == true }

        // Then
        result as Result.Success
        assertThat(result.data).hasSize(2)
        assertThat(result.data.all { it.properties.isHoming == true }).isEqualTo(true)
        assertThat(result.data.map { it.id }).isEqualTo(listOf("1+3", "uf3"))
    }

    @Test
    fun `invoke returns empty list when no homing moves exist`() = runTest {
        // Given
        val charName = "Yoshimitsu"
        val moves = listOf(
            createTestMove(id = "1", isHoming = false),
            createTestMove(id = "2", isHoming = false)
        )
        mockDb.mockResponse = Result.Success(moves)

        // When
        val result = useCase.invoke(charName) { it.properties.isHoming == true }

        // Then
        result as Result.Success
        assertThat(result.data).isEmpty()
    }
    //endregion

    //region Success Cases - Combined Properties
    @Test
    fun `invoke can filter moves with multiple properties`() = runTest {
        // Given
        val charName = "Jin"
        val moves = listOf(
            createTestMove(id = "df2", isHeat = true, isPowerCrush = false),
            createTestMove(id = "f23", isHeat = true, isPowerCrush = true),
            createTestMove(id = "1", isHeat = false, isPowerCrush = false)
        )
        mockDb.mockResponse = Result.Success(moves)

        // When - Filter for moves that are both heat AND power crush
        val result = useCase.invoke(charName) { it.properties.isHeat == true && it.properties.isPowerCrush == true }

        // Then
        result as Result.Success
        assertThat(result.data).hasSize(1)
        assertThat(result.data.first().id).isEqualTo("f23")
        assertThat(result.data.first().properties.isHeat == true).isEqualTo(true)
        assertThat(result.data.first().properties.isPowerCrush == true).isEqualTo(true)
    }

    @Test
    fun `invoke can filter moves with OR condition`() = runTest {
        // Given
        val charName = "Jin"
        val moves = listOf(
            createTestMove(id = "df2", isHeat = true, isHoming = false),
            createTestMove(id = "uf3", isHeat = false, isHoming = true),
            createTestMove(id = "bf23", isHeat = true, isHoming = true),
            createTestMove(id = "1", isHeat = false, isHoming = false)
        )
        mockDb.mockResponse = Result.Success(moves)

        // When - Filter for moves that are heat OR homing
        val result = useCase.invoke(charName) { it.properties.isHeat == true || it.properties.isHoming == true }

        // Then
        result as Result.Success
        assertThat(result.data).hasSize(3)
        assertThat(result.data.map { it.id }).isEqualTo(listOf("df2", "uf3", "bf23"))
    }

    @Test
    fun `invoke can filter moves with all three properties`() = runTest {
        // Given
        val charName = "UltraChar"
        val moves = listOf(
            createTestMove(id = "super", isHeat = true, isPowerCrush = true, isHoming = true),
            createTestMove(id = "df2", isHeat = true, isPowerCrush = true, isHoming = false),
            createTestMove(id = "1", isHeat = false, isPowerCrush = false, isHoming = false)
        )
        mockDb.mockResponse = Result.Success(moves)

        // When - Filter for moves with all three properties
        val result = useCase.invoke(charName) { it.properties.isHeat == true && it.properties.isPowerCrush == true && it.properties.isHoming == true }

        // Then
        result as Result.Success
        assertThat(result.data).hasSize(1)
        assertThat(result.data.first().id).isEqualTo("super")
    }
    //endregion

    //region Success Cases - Custom Predicates
    @Test
    fun `invoke can filter by custom predicate on damage`() = runTest {
        // Given
        val charName = "Jin"
        val moves = listOf(
            createTestMove(id = "df2", damage = "15"),
            createTestMove(id = "ewgf", damage = "18"),
            createTestMove(id = "1", damage = "5"),
            createTestMove(id = "bf23", damage = null)
        )
        mockDb.mockResponse = Result.Success(moves)

        // When - Filter for moves with damage field not null
        val result = useCase.invoke(charName) { it.damage != null }

        // Then
        result as Result.Success
        assertThat(result.data).hasSize(3)
        assertThat(result.data.map { it.id }).isEqualTo(listOf("df2", "ewgf", "1"))
    }

    @Test
    fun `invoke can filter by custom predicate on move name`() = runTest {
        // Given
        val charName = "Jin"
        val moves = listOf(
            createTestMove(id = "df2", name = "Wind Hook Fist"),
            createTestMove(id = "bf23", name = "Spinning Backfist"),
            createTestMove(id = "1", name = "Jab"),
            createTestMove(id = "unnamed", name = null)
        )
        mockDb.mockResponse = Result.Success(moves)

        // When - Filter for named moves
        val result = useCase.invoke(charName) { it.name != null }

        // Then
        result as Result.Success
        assertThat(result.data).hasSize(3)
        assertThat(result.data.none { it.name == null }).isEqualTo(true)
    }

    @Test
    fun `invoke can filter by custom predicate on notes`() = runTest {
        // Given
        val charName = "Jin"
        val moves = listOf(
            createTestMove(id = "df2", notes = listOf("Launcher", "Mid")),
            createTestMove(id = "1", notes = listOf("High")),
            createTestMove(id = "db4", notes = listOf())
        )
        mockDb.mockResponse = Result.Success(moves)

        // When - Filter for moves with notes
        val result = useCase.invoke(charName) { it.notes.isNotEmpty() }

        // Then
        result as Result.Success
        assertThat(result.data).hasSize(2)
        assertThat(result.data.map { it.id }).isEqualTo(listOf("df2", "1"))
    }
    //endregion

    //region Error Cases
    @Test
    fun `invoke returns error when database returns error`() = runTest {
        // Given
        val charName = "Jin"
        mockDb.mockResponse = Result.Error(WavuError.UNKNOWN_CHARACTER)

        // When
        val result = useCase.invoke(charName) { it.properties.isHeat == true }

        // Then
        assertThat(result).isNotNull()
        result as Result.Error
        assertThat(result.error).isEqualTo(WavuError.UNKNOWN_CHARACTER)
    }

    @Test
    fun `invoke returns error when character not found`() = runTest {
        // Given
        val charName = "NonExistentCharacter"
        mockDb.mockResponse = Result.Error(WavuError.UNKNOWN_CHARACTER)

        // When
        val result = useCase.invoke(charName) { it.properties.isPowerCrush == true }

        // Then
        result as Result.Error
        assertThat(result.error).isEqualTo(WavuError.UNKNOWN_CHARACTER)
    }

    @Test
    fun `invoke returns error when database operation fails`() = runTest {
        // Given
        val charName = "Jin"
        mockDb.mockResponse = Result.Error(WavuError.DOWNLOAD_ERROR)

        // When
        val result = useCase.invoke(charName) { it.properties.isHoming == true }

        // Then
        result as Result.Error
        assertThat(result.error).isEqualTo(WavuError.DOWNLOAD_ERROR)
    }
    //endregion

    //region Edge Cases
    @Test
    fun `invoke handles empty move list from database`() = runTest {
        // Given
        val charName = "Jin"
        mockDb.mockResponse = Result.Success(listOf())

        // When
        val result = useCase.invoke(charName) { it.properties.isHeat == true }

        // Then
        result as Result.Success
        assertThat(result.data).isEmpty()
    }

    @Test
    fun `invoke returns all moves when predicate always returns true`() = runTest {
        // Given
        val charName = "Jin"
        val moves = listOf(
            createTestMove(id = "1"),
            createTestMove(id = "2"),
            createTestMove(id = "3")
        )
        mockDb.mockResponse = Result.Success(moves)

        // When
        val result = useCase.invoke(charName) { true }

        // Then
        result as Result.Success
        assertThat(result.data).hasSize(3)
    }

    @Test
    fun `invoke returns empty list when predicate always returns false`() = runTest {
        // Given
        val charName = "Jin"
        val moves = listOf(
            createTestMove(id = "1"),
            createTestMove(id = "2"),
            createTestMove(id = "3")
        )
        mockDb.mockResponse = Result.Success(moves)

        // When
        val result = useCase.invoke(charName) { false }

        // Then
        result as Result.Success
        assertThat(result.data).isEmpty()
    }

    @Test
    fun `invoke preserves move order from database`() = runTest {
        // Given
        val charName = "Jin"
        val moves = listOf(
            createTestMove(id = "df2", isHeat = true),
            createTestMove(id = "bf23", isHeat = true),
            createTestMove(id = "f4", isHeat = true)
        )
        mockDb.mockResponse = Result.Success(moves)

        // When
        val result = useCase.invoke(charName) { it.properties.isHeat == true }

        // Then
        result as Result.Success
        assertThat(result.data.map { it.id }).isEqualTo(listOf("df2", "bf23", "f4"))
    }
    //endregion

    @Test
    fun `invoke with mixed case character name converts to lowercase and filters correctly`() = runTest {
        // Given
        val mockDB = MockMoveListDB()
        val useCase = FetchMovesWithPropertyUseCase(mockDB)

        val move1 = createTestMove(id = "1", isHeat = true)
        val move2 = createTestMove(id = "2", isHeat = false)
        val move3 = createTestMove(id = "3", isHeat = true)

        val moveList = listOf(move1, move2, move3)

        mockDB.mockResponse = Result.Success(moveList)

        // When - Call with mixed case "NaMe"
        val result = useCase.invoke(
            charName = "NaMe",
            predicate = { it.properties.isHeat == true }
        )

        // Then
        assertTrue(result is Result.Success)
        assertEquals(2, result.data.size)
        assertTrue(result.data.all { it.properties.isHeat == true })
        assertTrue(result.data.any { it.id == "1" })
        assertTrue(result.data.any { it.id == "3" })
    }

    private fun createTestMove(
        id: String,
        name: String? = null,
        damage: String? = null,
        notes: List<String> = listOf(),
        isHeat: Boolean = false,
        isPowerCrush: Boolean = false,
        isHoming: Boolean = false,
    ) = Move(
        charName = "TestChar",
        id = id,
        input = id,
        level = null,
        name = name,
        parent = null,
        damage = damage,
        startup = null,
        recoveryOnWhiff = null,
        totalFrames = null,
        crushes = listOf(),
        onBlock = null,
        onHit = null,
        onCH = null,
        notes = notes,
        aliases = listOf(),
        image = null,
        videoId = null,
        alt = null,
        properties = Move.Properties(
            isHeat = isHeat,
            isPowerCrush = isPowerCrush,
            isHoming = isHoming,
        ),
    )

    private class MockMoveListDB : MoveListDB {
        var mockResponse: Result<List<Move>, WavuError>? = null

        override suspend fun fetchMoveListFor(charName: String): Result<List<Move>, WavuError> {
            return mockResponse ?: Result.Error(WavuError.UNKNOWN_CHARACTER)
        }

        override suspend fun fetchMoveDataFor(
            charName: String,
            moveQuery: String
        ): Result<Move, WavuError> {
            // Not used in FetchMovesWithPropertyUseCase
            return Result.Error(WavuError.UNKNOWN_MOVE)
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

        override suspend fun getLastInsertTimeStamp(): Result<Instant?, WavuError> {
            // Not used in current tests
            return Result.Success(null)
        }
    }
}