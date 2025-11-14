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

class FetchCharacterListUseCaseTest {
    //region Success Cases
    @Test
    fun `invoke returns character list when database returns non-empty list`() {
        // given
        val characterList = listOf(
            Character(
                id = "1",
                displayName = "Kazuya",
                queryName = "kazuya",
                wikiUrl = "https://wavu.wiki/t/Kazuya"
            ),
            Character(
                id = "2",
                displayName = "Jin",
                queryName = "jin",
                wikiUrl = "https://wavu.wiki/t/Jin"
            )
        )
        val fakeDb = FakeCharacterListDB(shouldSucceed = true, characterListToReturn = characterList)
        val useCase = FetchCharacterListUseCase(fakeDb)

        // when
        val result = runBlocking { useCase.invoke() }

        // then
        assertTrue(result is Result.Success)
        assertEquals(characterList, result.data)
    }
    //endregion

    //region Error Cases
    @Test
    fun `invoke returns database error when database returns empty list`() {
        // given
        val emptyList = emptyList<Character>()
        val fakeDb = FakeCharacterListDB(shouldSucceed = true, characterListToReturn = emptyList)
        val useCase = FetchCharacterListUseCase(fakeDb)

        // when
        val result = runBlocking { useCase.invoke() }

        // then
        assertTrue(result is Result.Error)
        assertEquals(WikiError.DATABASE_ERROR, result.error)
    }

    @Test
    fun `invoke returns database error when database query fails`() {
        // given
        val fakeDb = FakeCharacterListDB(shouldSucceed = false, errorToReturn = WikiError.DATABASE_ERROR)
        val useCase = FetchCharacterListUseCase(fakeDb)

        // when
        val result = runBlocking { useCase.invoke() }

        // then
        assertTrue(result is Result.Error)
        assertEquals(WikiError.DATABASE_ERROR, result.error)
    }

    @Test
    fun `invoke returns download error when database fetch fails with download error`() {
        // given
        val fakeDb = FakeCharacterListDB(shouldSucceed = false, errorToReturn = WikiError.DOWNLOAD_ERROR)
        val useCase = FetchCharacterListUseCase(fakeDb)

        // when
        val result = runBlocking { useCase.invoke() }

        // then
        assertTrue(result is Result.Error)
        assertEquals(WikiError.DOWNLOAD_ERROR, result.error)
    }
    //endregion

    //region Test Doubles
    private class FakeCharacterListDB(
        private val shouldSucceed: Boolean,
        private val characterListToReturn: List<Character> = emptyList(),
        private val errorToReturn: WikiError = WikiError.DATABASE_ERROR
    ) : CharacterListDB {
        override suspend fun insertCharacterList(characterList: List<Character>): EmptyResult<WikiError> {
            throw NotImplementedError()
        }

        override suspend fun fetchCharacterList(): Result<List<Character>, WikiError> {
            return if (shouldSucceed) {
                Result.Success(characterListToReturn)
            } else {
                Result.Error(errorToReturn)
            }
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