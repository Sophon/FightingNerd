package io.github.sophon.wikiSuperCombo.usecase

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.model.Move
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FetchMoveUseCaseTest {
    //region Success Cases
    @Test
    fun `invoke returns move when found in database`() = runTest {
        // given
        val charName = "ryu"
        val moveQuery = "5lp"
        val move = Move(
            charName = "ryu",
            id = "1",
            input = "5lp",
            damage = "300",
            startup = "4",
            name = "Jab"
        )
        val fakeDb = FakeMoveListDB(shouldSucceed = true, moveToReturn = move)
        val useCase = FetchMoveUseCase(fakeDb)

        // when
        val result = useCase.invoke(charName, moveQuery)

        // then
        assertTrue(result is Result.Success)
        assertEquals(move, result.data)
        assertEquals(charName, fakeDb.lastQueriedCharName)
        assertEquals(moveQuery, fakeDb.lastQueriedMoveQuery)
    }
    //endregion

    //region Error Cases
    @Test
    fun `invoke returns unknown character error when character not found`() = runTest {
        // given
        val charName = "unknown"
        val moveQuery = "5lp"
        val fakeDb = FakeMoveListDB(shouldSucceed = false, errorToReturn = WikiError.UNKNOWN_CHARACTER)
        val useCase = FetchMoveUseCase(fakeDb)

        // when
        val result = useCase.invoke(charName, moveQuery)

        // then
        assertTrue(result is Result.Error)
        assertEquals(WikiError.UNKNOWN_CHARACTER, result.error)
    }

    @Test
    fun `invoke returns unknown move error when move not found`() = runTest {
        // given
        val charName = "ken"
        val moveQuery = "invalid"
        val fakeDb = FakeMoveListDB(shouldSucceed = false, errorToReturn = WikiError.UNKNOWN_MOVE)
        val useCase = FetchMoveUseCase(fakeDb)

        // when
        val result = useCase.invoke(charName, moveQuery)

        // then
        assertTrue(result is Result.Error)
        assertEquals(WikiError.UNKNOWN_MOVE, result.error)
    }

    @Test
    fun `invoke returns database error when database query fails`() = runTest {
        // given
        val charName = "chun-li"
        val moveQuery = "236p"
        val fakeDb = FakeMoveListDB(shouldSucceed = false, errorToReturn = WikiError.DATABASE_ERROR)
        val useCase = FetchMoveUseCase(fakeDb)

        // when
        val result = useCase.invoke(charName, moveQuery)

        // then
        assertTrue(result is Result.Error)
        assertEquals(WikiError.DATABASE_ERROR, result.error)
    }
    //endregion

    //region Test Doubles
    private class FakeMoveListDB(
        private val shouldSucceed: Boolean,
        private val moveToReturn: Move? = null,
        private val errorToReturn: WikiError = WikiError.DATABASE_ERROR
    ) : MoveListDB {
        var lastQueriedCharName: String? = null
        var lastQueriedMoveQuery: String? = null

        override suspend fun fetchMoveListFor(charName: String): Result<List<Move>, WikiError> {
            throw NotImplementedError()
        }

        override suspend fun fetchMoveDataFor(charName: String, moveQuery: String): Result<Move, WikiError> {
            lastQueriedCharName = charName
            lastQueriedMoveQuery = moveQuery
            return if (shouldSucceed) {
                moveToReturn?.let { Result.Success(it) }
                    ?: Result.Error(WikiError.UNKNOWN_MOVE)
            } else {
                Result.Error(errorToReturn)
            }
        }

        override suspend fun insertMoveList(charName: String, moveList: List<Move>): EmptyResult<WikiError> {
            throw NotImplementedError()
        }

        override suspend fun wipe(): EmptyResult<WikiError> {
            throw NotImplementedError()
        }

        override suspend fun getLastInsertTimeStamp(): Result<Instant?, WikiError> {
            throw NotImplementedError()
        }
    }
    //endregion
}