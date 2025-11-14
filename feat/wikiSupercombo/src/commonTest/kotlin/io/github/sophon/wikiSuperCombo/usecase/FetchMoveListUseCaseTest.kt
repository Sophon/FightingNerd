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

class FetchMoveListUseCaseTest {
    //region Success Cases
    @Test
    fun `invoke returns move list when found in database`() = runTest {
        // given
        val charName = "ryu"
        val moveList = listOf(
            Move(
                charName = "ryu",
                id = "1",
                input = "5lp",
                damage = "300",
                startup = "4"
            ),
            Move(
                charName = "ryu",
                id = "2",
                input = "236p",
                damage = "600",
                startup = "13",
                name = "Hadoken"
            )
        )
        val fakeDb = FakeMoveListDB(shouldSucceed = true, moveListToReturn = moveList)
        val useCase = FetchMoveListUseCase(fakeDb)

        // when
        val result = useCase.invoke(charName)

        // then
        assertTrue(result is Result.Success)
        assertEquals(moveList, result.data)
        assertEquals(charName, fakeDb.lastQueriedCharName)
    }

    @Test
    fun `invoke returns empty list when character has no moves`() = runTest {
        // given
        val charName = "ken"
        val emptyList = emptyList<Move>()
        val fakeDb = FakeMoveListDB(shouldSucceed = true, moveListToReturn = emptyList)
        val useCase = FetchMoveListUseCase(fakeDb)

        // when
        val result = useCase.invoke(charName)

        // then
        assertTrue(result is Result.Success)
        assertTrue(result.data.isEmpty())
    }
    //endregion

    //region Error Cases
    @Test
    fun `invoke returns unknown character error when character not found`() = runTest {
        // given
        val charName = "unknown"
        val fakeDb = FakeMoveListDB(shouldSucceed = false, errorToReturn = WikiError.UNKNOWN_CHARACTER)
        val useCase = FetchMoveListUseCase(fakeDb)

        // when
        val result = useCase.invoke(charName)

        // then
        assertTrue(result is Result.Error)
        assertEquals(WikiError.UNKNOWN_CHARACTER, result.error)
    }

    @Test
    fun `invoke returns database error when database query fails`() = runTest {
        // given
        val charName = "chun-li"
        val fakeDb = FakeMoveListDB(shouldSucceed = false, errorToReturn = WikiError.DATABASE_ERROR)
        val useCase = FetchMoveListUseCase(fakeDb)

        // when
        val result = useCase.invoke(charName)

        // then
        assertTrue(result is Result.Error)
        assertEquals(WikiError.DATABASE_ERROR, result.error)
    }
    //endregion

    //region Test Doubles
    private class FakeMoveListDB(
        private val shouldSucceed: Boolean,
        private val moveListToReturn: List<Move> = emptyList(),
        private val errorToReturn: WikiError = WikiError.DATABASE_ERROR
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