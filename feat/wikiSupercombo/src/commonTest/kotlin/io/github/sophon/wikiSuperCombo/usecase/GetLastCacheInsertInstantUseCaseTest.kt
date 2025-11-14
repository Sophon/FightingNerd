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
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GetLastCacheInsertInstantUseCaseTest {
    //region Success Cases
    @Test
    fun `invoke returns instant when last cache insert exists`() = runTest {
        // given
        val expectedInstant = Instant.parse("2024-01-15T10:30:00Z")
        val fakeDb = FakeMoveListDB(shouldSucceed = true, timestampToReturn = expectedInstant)
        val useCase = GetLastCacheInsertInstantUseCase(fakeDb)

        // when
        val result = useCase.invoke()

        // then
        assertTrue(result is Result.Success)
        assertEquals(expectedInstant, result.data)
    }

    @Test
    fun `invoke returns null when no cache insert exists`() = runTest {
        // given
        val fakeDb = FakeMoveListDB(shouldSucceed = true, timestampToReturn = null)
        val useCase = GetLastCacheInsertInstantUseCase(fakeDb)

        // when
        val result = useCase.invoke()

        // then
        assertTrue(result is Result.Success)
        assertNull(result.data)
    }
    //endregion

    //region Error Cases
    @Test
    fun `invoke returns database error when database query fails`() = runTest {
        // given
        val fakeDb = FakeMoveListDB(shouldSucceed = false, errorToReturn = WikiError.DATABASE_ERROR)
        val useCase = GetLastCacheInsertInstantUseCase(fakeDb)

        // when
        val result = useCase.invoke()

        // then
        assertTrue(result is Result.Error)
        assertEquals(WikiError.DATABASE_ERROR, result.error)
    }
    //endregion

    //region Test Doubles
    private class FakeMoveListDB(
        private val shouldSucceed: Boolean,
        private val timestampToReturn: Instant? = null,
        private val errorToReturn: WikiError = WikiError.DATABASE_ERROR
    ) : MoveListDB {
        override suspend fun fetchMoveListFor(charName: String): Result<List<Move>, WikiError> {
            throw NotImplementedError()
        }

        override suspend fun fetchMoveDataFor(charName: String, moveQuery: String): Result<Move, WikiError> {
            throw NotImplementedError()
        }

        override suspend fun insertMoveList(charName: String, moveList: List<Move>): EmptyResult<WikiError> {
            throw NotImplementedError()
        }

        override suspend fun wipe(): EmptyResult<WikiError> {
            throw NotImplementedError()
        }

        override suspend fun getLastInsertTimeStamp(): Result<Instant?, WikiError> {
            return if (shouldSucceed) {
                Result.Success(timestampToReturn)
            } else {
                Result.Error(errorToReturn)
            }
        }
    }
    //endregion
}