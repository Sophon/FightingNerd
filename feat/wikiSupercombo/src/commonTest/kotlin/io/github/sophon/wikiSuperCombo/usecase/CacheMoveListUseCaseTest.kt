package io.github.sophon.wikiSuperCombo.usecase

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CacheMoveListUseCaseTest {
    //region Success Cases
    @Test
    fun `invoke returns success when caching moves for character without aliases`() {
        // given
        val character = Character(
            id = "Ryu",
            displayName = "Ryu",
            queryName = "ryu",
            wikiUrl = "https://wiki.supercombo.gg/w/Street_Fighter_6/Ryu",
            aliasList = emptyList()
        )
        val moveList = listOf(
            Move(
                charName = "ryu",
                id = "1",
                input = "5LP",
                damage = "4"
            )
        )
        val fakeDb = FakeMoveListDB(shouldSucceed = true)
        val useCase = CacheMoveListUseCase(fakeDb)

        // when
        val result = runBlocking { useCase.invoke(character, moveList) }

        // then
        assertTrue(result is Result.Success)
        assertEquals(listOf("ryu"), fakeDb.insertedCharNames)
        assertEquals(moveList, fakeDb.lastInsertedMoveList)
    }

    @Test
    fun `invoke returns success when caching moves for character with aliases`() {
        // given
        val character = Character(
            id = "Ryu",
            displayName = "Ryu",
            queryName = "ryu",
            wikiUrl = "https://wiki.supercombo.gg/w/Street_Fighter_6/Ryu",
            aliasList = listOf("ryu-master", "ryu-sf6")
        )
        val moveList = listOf(
            Move(
                charName = "ryu",
                id = "1",
                input = "5LP",
                damage = "4"
            )
        )
        val fakeDb = FakeMoveListDB(shouldSucceed = true)
        val useCase = CacheMoveListUseCase(fakeDb)

        // when
        val result = runBlocking { useCase.invoke(character, moveList) }

        // then
        assertTrue(result is Result.Success)
        assertEquals(listOf("ryu", "ryu-master", "ryu-sf6"), fakeDb.insertedCharNames)
    }

    @Test
    fun `invoke returns success when caching empty move list`() {
        // given
        val character = Character(
            id = "Ken",
            displayName = "Ken",
            queryName = "ken",
            wikiUrl = "https://wiki.supercombo.gg/w/Street_Fighter_6/Ken"
        )
        val emptyMoveList = emptyList<Move>()
        val fakeDb = FakeMoveListDB(shouldSucceed = true)
        val useCase = CacheMoveListUseCase(fakeDb)

        // when
        val result = runBlocking { useCase.invoke(character, emptyMoveList) }

        // then
        assertTrue(result is Result.Success)
        assertEquals(listOf("ken"), fakeDb.insertedCharNames)
    }
    //endregion

    //region Error Cases
    @Test
    fun `invoke returns database error when main character insert fails`() {
        // given
        val character = Character(
            id = "Chun-Li",
            displayName = "Chun-Li",
            queryName = "chun-li",
            wikiUrl = "https://wiki.supercombo.gg/w/Street_Fighter_6/Chun-Li"
        )
        val moveList = listOf(
            Move(
                charName = "chun-li",
                id = "1",
                input = "5LP",
                damage = "4"
            )
        )
        val fakeDb = FakeMoveListDB(shouldSucceed = false, errorToReturn = WikiError.DatabaseError(""))
        val useCase = CacheMoveListUseCase(fakeDb)

        // when
        val result = runBlocking { useCase.invoke(character, moveList) }

        // then
        assertTrue(result is Result.Error)
        assertTrue(result.error is WikiError.DatabaseError)
    }

    @Test
    fun `invoke returns database error when alias insert fails`() {
        // given
        val character = Character(
            id = "Guile",
            displayName = "Guile",
            queryName = "guile",
            wikiUrl = "https://wiki.supercombo.gg/w/Street_Fighter_6/Guile",
            aliasList = listOf("guile-alt")
        )
        val moveList = listOf(
            Move(
                charName = "guile",
                id = "1",
                input = "5LP",
                damage = "4"
            )
        )
        val fakeDb = FakeMoveListDB(
            shouldSucceed = false,
            errorToReturn = WikiError.DatabaseError(""),
            failAfterInsertCount = 1
        )
        val useCase = CacheMoveListUseCase(fakeDb)

        // when
        val result = runBlocking { useCase.invoke(character, moveList) }

        // then
        assertTrue(result is Result.Error)
        assertTrue(result.error is WikiError.DatabaseError)
        assertEquals(listOf("guile"), fakeDb.insertedCharNames)
    }
    //endregion

    //region Test Doubles
    private class FakeMoveListDB(
        private val shouldSucceed: Boolean,
        private val errorToReturn: WikiError = WikiError.DatabaseError(""),
        private val failAfterInsertCount: Int = 0
    ) : MoveListDB {
        val insertedCharNames = mutableListOf<String>()
        var lastInsertedMoveList: List<Move>? = null
        private var insertCount = 0

        override suspend fun fetchMoveListFor(charName: String): Result<List<Move>, WikiError> {
            throw NotImplementedError()
        }

        override suspend fun fetchMoveDataFor(charName: String, moveQuery: String): Result<Move, WikiError> {
            throw NotImplementedError()
        }

        override suspend fun insertMoveList(charName: String, moveList: List<Move>): EmptyResult<WikiError> {
            insertCount++
            return if (shouldSucceed || (failAfterInsertCount > 0 && insertCount <= failAfterInsertCount)) {
                insertedCharNames.add(charName)
                lastInsertedMoveList = moveList
                Result.Success(Unit)
            } else {
                Result.Error(errorToReturn)
            }
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