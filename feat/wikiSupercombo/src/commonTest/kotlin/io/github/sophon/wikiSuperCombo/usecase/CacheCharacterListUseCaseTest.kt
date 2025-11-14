package io.github.sophon.wikiSuperCombo.usecase

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
                displayName = "Ryu",
                queryName = "ryu",
                wikiUrl = "https://wiki.supercombo.gg/w/Street_Fighter_6/Ryu"
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
                displayName = "Ken",
                queryName = "ken",
                wikiUrl = "https://wiki.supercombo.gg/w/Street_Fighter_6/Ken"
            )
        )
        val fakeDb = FakeCharacterListDB(shouldSucceed = false, errorToReturn = WikiError.DATABASE_ERROR)
        val useCase = CacheCharacterListUseCase(fakeDb)

        // when
        val result = runBlocking { useCase.invoke(characterList) }

        // then
        assertTrue(result is Result.Error)
        assertEquals(WikiError.DATABASE_ERROR, result.error)
    }
    //endregion

    //region Test Doubles
    private class FakeCharacterListDB(
        private val shouldSucceed: Boolean,
        private val errorToReturn: WikiError = WikiError.DATABASE_ERROR
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