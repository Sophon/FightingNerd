package io.github.sophon.wikiwavu.usecase

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.model.Move
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FetchMoveListUseCaseTest {
    //region Success Cases
    @Test
    fun `invoke returns move list when database returns non-empty list`() {
        // given
        val charName = "kazuya"
        val moveList = listOf(
            Move(
                charName = "kazuya",
                id = "1",
                input = "df+2",
                damage = "15",
                startup = "i13"
            ),
            Move(
                charName = "kazuya",
                id = "2",
                input = "EWGF",
                damage = "18",
                startup = "i13"
            )
        )
        val fakeDb = FakeMoveListDB(shouldSucceed = true, moveListToReturn = moveList)
        val useCase = FetchMoveListUseCase(fakeDb)

        // when
        val result = runBlocking { useCase.invoke(charName) }

        // then
        assertTrue(result is Result.Success)
        assertEquals(moveList, result.data)
        assertEquals(charName, fakeDb.lastQueriedCharName)
    }
    //endregion

    //region Error Cases
    @Test
    fun `invoke returns database error when database returns empty list`() {
        // given
        val charName = "jin"
        val emptyList = emptyList<Move>()
        val fakeDb = FakeMoveListDB(shouldSucceed = true, moveListToReturn = emptyList)
        val useCase = FetchMoveListUseCase(fakeDb)

        // when
        val result = runBlocking { useCase.invoke(charName) }

        // then
        assertTrue(result is Result.Error)
        assertTrue(result.error is WikiError.DatabaseError)
    }

    @Test
    fun `invoke returns database error when database query fails`() {
        // given
        val charName = "paul"
        val fakeDb = FakeMoveListDB(shouldSucceed = false, errorToReturn = WikiError.DatabaseError(""))
        val useCase = FetchMoveListUseCase(fakeDb)

        // when
        val result = runBlocking { useCase.invoke(charName) }

        // then
        assertTrue(result is Result.Error)
        assertTrue(result.error is WikiError.DatabaseError)
    }

    @Test
    fun `invoke returns unknown character error when character not found`() {
        // given
        val charName = "unknown"
        val fakeDb = FakeMoveListDB(shouldSucceed = false, errorToReturn = WikiError.UnknownCharacter(charName))
        val useCase = FetchMoveListUseCase(fakeDb)

        // when
        val result = runBlocking { useCase.invoke(charName) }

        // then
        assertTrue(result is Result.Error)
        assertTrue(result.error is WikiError.UnknownCharacter)
    }
    //endregion

    //region Test Doubles
    private class FakeMoveListDB(
        private val shouldSucceed: Boolean,
        private val moveListToReturn: List<Move> = emptyList(),
        private val errorToReturn: WikiError = WikiError.DatabaseError("")
    ) : MoveListDB {
        var lastQueriedCharName: String? = null

        override suspend fun fetchMoveListFor(charName: String): Result<List<Move>, WikiError> {
            lastQueriedCharName = charName
            return if (shouldSucceed) {
                Result.Success(moveListToReturn)
            } else {
                Result.Error(errorToReturn)
            }
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
            throw NotImplementedError()
        }
    }
    //endregion
}