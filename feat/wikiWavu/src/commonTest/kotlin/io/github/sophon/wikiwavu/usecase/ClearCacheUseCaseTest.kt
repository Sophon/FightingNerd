package io.github.sophon.wikiwavu.usecase

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ClearCacheUseCaseTest {
    //region Success Cases
    @Test
    fun `invoke returns success when both databases wipe successfully`() {
        // given
        val fakeCharListDB = FakeCharacterListDB(shouldSucceed = true)
        val fakeMoveListDB = FakeMoveListDB(shouldSucceed = true)
        val useCase = ClearCacheUseCase(fakeCharListDB, fakeMoveListDB)

        // when
        val result = runBlocking { useCase.invoke() }

        // then
        assertTrue(result is Result.Success)
        assertTrue(fakeCharListDB.wipeWasCalled)
        assertTrue(fakeMoveListDB.wipeWasCalled)
    }
    //endregion

    //region Error Cases
    @Test
    fun `invoke returns error when character database wipe fails`() {
        // given
        val fakeCharListDB = FakeCharacterListDB(shouldSucceed = false, errorToReturn = WikiError.DatabaseError(""))
        val fakeMoveListDB = FakeMoveListDB(shouldSucceed = true)
        val useCase = ClearCacheUseCase(fakeCharListDB, fakeMoveListDB)

        // when
        val result = runBlocking { useCase.invoke() }

        // then
        assertTrue(result is Result.Error)
        assertTrue(result.error is WikiError.DatabaseError)
        assertTrue(fakeCharListDB.wipeWasCalled)
        assertTrue(fakeMoveListDB.wipeWasCalled)
    }

    @Test
    fun `invoke returns error when move database wipe fails`() {
        // given
        val fakeCharListDB = FakeCharacterListDB(shouldSucceed = true)
        val fakeMoveListDB = FakeMoveListDB(shouldSucceed = false, errorToReturn = WikiError.DatabaseError(""))
        val useCase = ClearCacheUseCase(fakeCharListDB, fakeMoveListDB)

        // when
        val result = runBlocking { useCase.invoke() }

        // then
        assertTrue(result is Result.Error)
        assertTrue(result.error is WikiError.DatabaseError)
        assertTrue(fakeCharListDB.wipeWasCalled)
        assertTrue(fakeMoveListDB.wipeWasCalled)
    }

    @Test
    fun `invoke returns character database error when both wipes fail`() {
        // given
        val fakeCharListDB = FakeCharacterListDB(shouldSucceed = false, errorToReturn = WikiError.DatabaseError(""))
        val fakeMoveListDB = FakeMoveListDB(shouldSucceed = false, errorToReturn = WikiError.DownloadError(""))
        val useCase = ClearCacheUseCase(fakeCharListDB, fakeMoveListDB)

        // when
        val result = runBlocking { useCase.invoke() }

        // then
        assertTrue(result is Result.Error)
        assertTrue(result.error is WikiError.DatabaseError)
        assertTrue(fakeCharListDB.wipeWasCalled)
        assertTrue(fakeMoveListDB.wipeWasCalled)
    }
    //endregion

    //region Test Doubles
    private class FakeCharacterListDB(
        private val shouldSucceed: Boolean,
        private val errorToReturn: WikiError = WikiError.DatabaseError("")
    ) : CharacterListDB {
        var wipeWasCalled = false

        override suspend fun insertCharacterList(characterList: List<Character>): EmptyResult<WikiError> {
            throw NotImplementedError()
        }

        override suspend fun fetchCharacterList(): Result<List<Character>, WikiError> {
            throw NotImplementedError()
        }

        override suspend fun wipe(): EmptyResult<WikiError> {
            wipeWasCalled = true
            return if (shouldSucceed) {
                Result.Success(Unit)
            } else {
                Result.Error(errorToReturn)
            }
        }

        override suspend fun fetchCharacterDataFor(charName: String): Result<Character, WikiError> {
            throw NotImplementedError()
        }
    }

    private class FakeMoveListDB(
        private val shouldSucceed: Boolean,
        private val errorToReturn: WikiError = WikiError.DatabaseError("")
    ) : MoveListDB {
        var wipeWasCalled = false

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
            wipeWasCalled = true
            return if (shouldSucceed) {
                Result.Success(Unit)
            } else {
                Result.Error(errorToReturn)
            }
        }

        override suspend fun getLastInsertTimeStamp(): Result<Instant?, WikiError> {
            throw NotImplementedError()
        }
    }
    //endregion
}