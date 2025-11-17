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

class FetchCharacterListUseCaseTest {
    //region Success Cases
    @Test
    fun `invoke returns character list when database returns non-empty list`() {
        // given
        val characterList = listOf(
            Character(
                id = "ryu",
                displayName = "Ryu",
                queryName = "Ryu",
                wikiUrl = "https://wiki.supercombo.gg/w/Street_Fighter_6/Ryu"
            ),
            Character(
                id = "ken",
                displayName = "Ken",
                queryName = "Ken",
                wikiUrl = "https://wiki.supercombo.gg/w/Street_Fighter_6/Ken"
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
        assertTrue(result.error is WikiError.DatabaseError)
    }

    @Test
    fun `invoke returns database error when database query fails`() {
        // given
        val fakeDb = FakeCharacterListDB(shouldSucceed = false, errorToReturn = WikiError.DatabaseError(""))
        val useCase = FetchCharacterListUseCase(fakeDb)

        // when
        val result = runBlocking { useCase.invoke() }

        // then
        assertTrue(result is Result.Error)
        assertTrue(result.error is WikiError.DatabaseError)
    }

    @Test
    fun `invoke returns download error when database fetch fails with download error`() {
        // given
        val fakeDb = FakeCharacterListDB(shouldSucceed = false, errorToReturn = WikiError.DownloadError(""))
        val useCase = FetchCharacterListUseCase(fakeDb)

        // when
        val result = runBlocking { useCase.invoke() }

        // then
        assertTrue(result is Result.Error)
        assertTrue(result.error is WikiError.DownloadError)
    }
    //endregion

    //region Test Doubles
    private class FakeCharacterListDB(
        private val shouldSucceed: Boolean,
        private val characterListToReturn: List<Character> = emptyList(),
        private val errorToReturn: WikiError = WikiError.DatabaseError("")
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