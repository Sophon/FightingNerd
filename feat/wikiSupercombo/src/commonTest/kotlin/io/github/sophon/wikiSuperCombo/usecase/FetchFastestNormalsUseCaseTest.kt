package io.github.sophon.wikiSuperCombo.usecase

import assertk.assertThat
import assertk.assertions.*
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.model.Move
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test

class FetchFastestNormalsUseCaseTest {
    private lateinit var fakeDb: FakeMoveListDB
    private lateinit var useCase: FetchFastestNormalsUseCase

    @BeforeTest
    fun setup() {
        fakeDb = FakeMoveListDB()
        useCase = FetchFastestNormalsUseCase(fakeDb)
    }

    //region Returns fastest normals when multiple moves have same startup
    @Test
    fun `given multiple normals with same fastest startup when invoked then returns all fastest moves`() = runTest {
        // Given
        val moves = listOf(
            createMove(input = "5LP", startup = "4"),
            createMove(input = "2LP", startup = "4"),
            createMove(input = "5LK", startup = "5"),
            createMove(input = "2LK", startup = "5")
        )
        fakeDb.resultToReturn = Result.Success(moves)

        // When
        val result = useCase.invoke("ken")

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val data = (result as Result.Success).data
        assertThat(data).hasSize(2)
        assertThat(data.map { it.input }).containsExactlyInAnyOrder("5LP", "2LP")
    }
    //endregion
    //region Returns single fastest normal
    @Test
    fun `given single fastest normal when invoked then returns only that move`() = runTest {
        // Given
        val moves = listOf(
            createMove(input = "5LP", startup = "4"),
            createMove(input = "2LP", startup = "5"),
            createMove(input = "5LK", startup = "6")
        )
        fakeDb.resultToReturn = Result.Success(moves)

        // When
        val result = useCase.invoke("ken")

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val data = (result as Result.Success).data
        assertThat(data).hasSize(1)
        assertThat(data.first().input).isEqualTo("5LP")
        assertThat(data.first().startup).isEqualTo("4")
    }
    //endregion
    //region Filters only 5X and 2X inputs with length 3
    @Test
    fun `given moves with various inputs when invoked then filters only normals starting with 5 or 2`() = runTest {
        // Given
        val moves = listOf(
            createMove(input = "5LP", startup = "4"),
            createMove(input = "2LP", startup = "4"),
            createMove(input = "214KK", startup = "9"),
            createMove(input = "5MK~MK", startup = "11"),
            createMove(input = "j.HP", startup = "9"),
            createMove(input = "3HP", startup = "10")
        )
        fakeDb.resultToReturn = Result.Success(moves)

        // When
        val result = useCase.invoke("ken")

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val data = (result as Result.Success).data
        assertThat(data.map { it.input }).containsExactlyInAnyOrder("5LP", "2LP")
    }

    @Test
    fun `given moves with wrong length inputs when invoked then excludes them`() = runTest {
        // Given
        val moves = listOf(
            createMove(input = "5LP", startup = "4"),
            createMove(input = "5LPLK", startup = "5"),
            createMove(input = "5L", startup = "3"),
            createMove(input = "2MK", startup = "7")
        )
        fakeDb.resultToReturn = Result.Success(moves)

        // When
        val result = useCase.invoke("ken")

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val data = (result as Result.Success).data
        assertThat(data.map { it.input }).containsExactlyInAnyOrder("5LP")
    }
    //endregion
    //region Returns empty list when no normals found
    @Test
    fun `given no matching normals when invoked then returns empty list`() = runTest {
        // Given
        val moves = listOf(
            createMove(input = "214KK", startup = "9"),
            createMove(input = "j.HP", startup = "9"),
            createMove(input = "623PP", startup = "6")
        )
        fakeDb.resultToReturn = Result.Success(moves)

        // When
        val result = useCase.invoke("ken")

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val data = (result as Result.Success).data
        assertThat(data).isEmpty()
    }

    @Test
    fun `given empty move list when invoked then returns empty list`() = runTest {
        // Given
        fakeDb.resultToReturn = Result.Success(emptyList())

        // When
        val result = useCase.invoke("ken")

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val data = (result as Result.Success).data
        assertThat(data).isEmpty()
    }
    //endregion
    //region Handles null startup values
    @Test
    fun `given moves with null startup when invoked then treats them as slowest`() = runTest {
        // Given
        val moves = listOf(
            createMove(input = "5LP", startup = "4"),
            createMove(input = "2LP", startup = null),
            createMove(input = "5LK", startup = "5")
        )
        fakeDb.resultToReturn = Result.Success(moves)

        // When
        val result = useCase.invoke("ken")

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val data = (result as Result.Success).data
        assertThat(data).hasSize(1)
        assertThat(data.first().input).isEqualTo("5LP")
        assertThat(data.first().startup).isEqualTo("4")
    }

    @Test
    fun `given all normals have null startup when invoked then returns all normals`() = runTest {
        // Given
        val moves = listOf(
            createMove(input = "5LP", startup = null),
            createMove(input = "2LP", startup = null),
            createMove(input = "5LK", startup = null)
        )
        fakeDb.resultToReturn = Result.Success(moves)

        // When
        val result = useCase.invoke("ken")

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val data = (result as Result.Success).data
        assertThat(data).hasSize(3)
    }
    //endregion
    //region Propagates database errors
    @Test
    fun `given database error when invoked then returns error`() = runTest {
        // Given
        val error = WikiError.DownloadError("")
        fakeDb.resultToReturn = Result.Error(error)

        // When
        val result = useCase.invoke("ken")

        // Then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat((result as Result.Error).error).isEqualTo(error)
    }
    //endregion

    private fun createMove(
        charName: String = "ken",
        id: String = "test_id",
        input: String,
        startup: String?,
        damage: String? = "100",
        onBlock: String? = null,
        onHit: String? = null,
        onCH: String? = null,
        name: String? = null,
        recovery: String? = null
    ) = Move(
        charName = charName,
        id = id,
        input = input,
        damage = damage,
        startup = startup,
        onBlock = onBlock,
        onHit = onHit,
        onCH = onCH,
        name = name,
        recovery = recovery
    )

    private class FakeMoveListDB : MoveListDB {
        var resultToReturn: Result<List<Move>, WikiError> = Result.Success(emptyList())

        override suspend fun fetchMoveListFor(charName: String): Result<List<Move>, WikiError> {
            return resultToReturn
        }

        override suspend fun fetchMoveDataFor(charName: String, moveQuery: String): Result<Move, WikiError> {
            error("Not implemented for these tests")
        }

        override suspend fun insertMoveList(charName: String, moveList: List<Move>): EmptyResult<WikiError> {
            error("Not implemented for these tests")
        }

        override suspend fun wipe(): EmptyResult<WikiError> {
            error("Not implemented for these tests")
        }

        override suspend fun getLastInsertTimeStamp(): Result<Instant?, WikiError> {
            error("Not implemented for these tests")
        }
    }
}