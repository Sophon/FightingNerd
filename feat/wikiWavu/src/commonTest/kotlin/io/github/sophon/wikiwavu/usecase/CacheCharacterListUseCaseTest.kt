package io.github.sophon.wikiwavu.usecase

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.model.Character
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CacheCharacterListUseCaseTest {
    //region Success Cases
    @Test
    fun `invoke returns success when database insertion succeeds`() {
        // given
        val characterList = listOf(
            Character(
                id = "1",
                displayName = "Kazuya",
                queryName = "kazuya",
                wikiUrl = "https://wavu.wiki/t/Kazuya"
            )
        )
        val fakeDb = FakeCharacterListDB(shouldSucceed = true)
        val useCase = CacheCharacterListUseCase(fakeDb)

        // when
        val result = runBlocking { useCase.invoke(characterList) }

        // then
        assertTrue(result is Result.Success)
        assertEquals(characterList, fakeDb.lastInsertedList)
    }

    @Test
    fun `invoke returns success when caching empty list`() {
        // given
        val emptyList = emptyList<Character>()
        val fakeDb = FakeCharacterListDB(shouldSucceed = true)
        val useCase = CacheCharacterListUseCase(fakeDb)

        // when
        val result = runBlocking { useCase.invoke(emptyList) }

        // then
        assertTrue(result is Result.Success)
        assertEquals(emptyList, fakeDb.lastInsertedList)
    }
    //endregion

    //region Error Cases
    @Test
    fun `invoke returns database error when database insertion fails`() {
        // given
        val characterList = listOf(
            Character(
                id = "1",
                displayName = "Jin",
                queryName = "jin",
                wikiUrl = "https://wavu.wiki/t/Jin"
            )
        )
        val fakeDb = FakeCharacterListDB(shouldSucceed = false, errorToReturn = WikiError.DatabaseError(""))
        val useCase = CacheCharacterListUseCase(fakeDb)

        // when
        val result = runBlocking { useCase.invoke(characterList) }

        // then
        assertTrue(result is Result.Error)
        assertTrue(result.error is WikiError.DatabaseError)
    }
    //endregion

    //region Test Doubles
    private class FakeCharacterListDB(
        private val shouldSucceed: Boolean,
        private val errorToReturn: WikiError = WikiError.DatabaseError("")
    ) : CharacterListDB {
        var lastInsertedList: List<Character>? = null

        override suspend fun insertCharacterList(characterList: List<Character>): EmptyResult<WikiError> {
            lastInsertedList = characterList
            return if (shouldSucceed) {
                Result.Success(Unit)
            } else {
                Result.Error(errorToReturn)
            }
        }

        override suspend fun fetchCharacterList(): Result<List<Character>, WikiError> {
            throw NotImplementedError()
        }

        override suspend fun wipe(): EmptyResult<WikiError> {
            throw NotImplementedError()
        }

        override suspend fun fetchCharacterDataFor(charName: String): Result<Character, WikiError> {
            throw NotImplementedError()
        }
    }
    //endregion
}