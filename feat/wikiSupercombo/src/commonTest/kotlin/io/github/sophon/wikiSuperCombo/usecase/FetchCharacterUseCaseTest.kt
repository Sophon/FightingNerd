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

class FetchCharacterUseCaseTest {
    //region Success Cases
    @Test
    fun `invoke returns character when found in database`() {
        // given
        val charName = "ryu"
        val character = Character(
            id = "ryu",
            displayName = "Ryu",
            queryName = "Ryu",
            wikiUrl = "https://wiki.supercombo.gg/w/Street_Fighter_6/Ryu"
        )
        val fakeDb = FakeCharacterListDB(shouldSucceed = true, characterToReturn = character)
        val useCase = FetchCharacterUseCase(fakeDb)

        // when
        val result = runBlocking { useCase.invoke(charName) }

        // then
        assertTrue(result is Result.Success)
        assertEquals(character, result.data)
        assertEquals(charName, fakeDb.lastQueriedCharName)
    }
    //endregion

    //region Error Cases
    @Test
    fun `invoke returns unknown character error when character not found`() {
        // given
        val charName = "unknown"
        val fakeDb = FakeCharacterListDB(shouldSucceed = false, errorToReturn = WikiError.UnknownCharacter(""))
        val useCase = FetchCharacterUseCase(fakeDb)

        // when
        val result = runBlocking { useCase.invoke(charName) }

        // then
        assertTrue(result is Result.Error)
        assertTrue(result.error is WikiError.UnknownCharacter)
    }

    @Test
    fun `invoke returns database error when database query fails`() {
        // given
        val charName = "ken"
        val fakeDb = FakeCharacterListDB(shouldSucceed = false, errorToReturn = WikiError.DatabaseError(""))
        val useCase = FetchCharacterUseCase(fakeDb)

        // when
        val result = runBlocking { useCase.invoke(charName) }

        // then
        assertTrue(result is Result.Error)
        assertTrue(result.error is WikiError.DatabaseError)
    }
    //endregion

    //region Test Doubles
    private class FakeCharacterListDB(
        private val shouldSucceed: Boolean,
        private val characterToReturn: Character? = null,
        private val errorToReturn: WikiError = WikiError.DatabaseError("")
    ) : CharacterListDB {
        var lastQueriedCharName: String? = null

        override suspend fun insertCharacterList(characterList: List<Character>): EmptyResult<WikiError> {
            throw NotImplementedError()
        }

        override suspend fun fetchCharacterList(): Result<List<Character>, WikiError> {
            throw NotImplementedError()
        }

        override suspend fun wipe(): EmptyResult<WikiError> {
            throw NotImplementedError()
        }

        override suspend fun fetchCharacterDataFor(charName: String): Result<Character, WikiError> {
            lastQueriedCharName = charName
            return if (shouldSucceed) {
                characterToReturn?.let { Result.Success(it) }
                    ?: Result.Error(WikiError.UnknownCharacter(charName))
            } else {
                Result.Error(errorToReturn)
            }
        }
    }
    //endregion
}