package io.github.sophon.wikiwavu.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.model.Character
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class FetchCharacterUseCaseTest {
    //region Test Setup
    private class FakeCharacterListDB : CharacterListDB {
        var resultToReturn: Result<Character, WikiError>? = null

        override suspend fun insertCharacterList(characterList: List<Character>): Result<Unit, WikiError> {
            throw NotImplementedError()
        }

        override suspend fun fetchCharacterList(): Result<List<Character>, WikiError> {
            throw NotImplementedError()
        }

        override suspend fun wipe(): Result<Unit, WikiError> {
            throw NotImplementedError()
        }

        override suspend fun fetchCharacterDataFor(charName: String): Result<Character, WikiError> {
            return resultToReturn ?: throw IllegalStateException("resultToReturn not set")
        }
    }

    private fun createTestCharacter(name: String = "Jin") = Character(
        id = name.lowercase(),
        displayName = name,
        queryName = name,
        wikiUrl = "https://wiki.example.com/$name",
        aliasList = emptyList(),
        images = null,
        sf6Properties = null
    )
    //endregion

    //region Tests
    @Test
    fun `invoke - given character exists - when fetching - then returns success with character`() = runTest {
        // Given
        val fakeDb = FakeCharacterListDB()
        val expectedCharacter = createTestCharacter("Kazuya")
        fakeDb.resultToReturn = Result.Success(expectedCharacter)
        val useCase = FetchCharacterUseCase(fakeDb)

        // When
        val result = useCase.invoke("Kazuya")

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        assertThat((result as Result.Success).data).isEqualTo(expectedCharacter)
    }

    @Test
    fun `invoke - given character not found - when fetching - then returns error`() = runTest {
        // Given
        val fakeDb = FakeCharacterListDB()
        val expectedError = WikiError.UnknownCharacter("Character not found")
        fakeDb.resultToReturn = Result.Error(expectedError)
        val useCase = FetchCharacterUseCase(fakeDb)

        // When
        val result = useCase.invoke("Unknown")

        // Then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat((result as Result.Error).error).isEqualTo(expectedError)
    }

    @Test
    fun `invoke - given database error - when fetching - then returns error`() = runTest {
        // Given
        val fakeDb = FakeCharacterListDB()
        val expectedError = WikiError.DatabaseError("Database connection failed")
        fakeDb.resultToReturn = Result.Error(expectedError)
        val useCase = FetchCharacterUseCase(fakeDb)

        // When
        val result = useCase.invoke("Jin")

        // Then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat((result as Result.Error).error).isEqualTo(expectedError)
    }
    //endregion
}