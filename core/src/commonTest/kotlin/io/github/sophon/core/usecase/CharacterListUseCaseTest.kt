package io.github.sophon.core.usecase

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.data.WikiDataSource
import io.github.sophon.core.domain.DataError
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.model.Character
import io.github.sophon.core.domain.usecase.DownloadCharacterListUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DownloadCharacterListUseCaseTest {
    // region Success Cases
    @Test
    fun `invoke returns success with mapped characters when data source succeeds`() = runTest {
        // Given
        val mockResponse = createTestCharacterListDto()
        val mockDataSource = MockWikiDataSource<TestCharacterListDto, Unit>()
        mockDataSource.characterListResponse = Result.Success(mockResponse)

        val useCase = DownloadCharacterListUseCase<TestCharacterListDto, DataError.Remote>(
            source = mockDataSource,
            toDomain = { this.toDomain() },
            toDomainError = { this }
        )

        // When
        val result = useCase.invoke()

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val characters = (result as Result.Success).data
        assertThat(characters).hasSize(3)
        assertThat(characters[0].id).isEqualTo("alisa")
        assertThat(characters[0].displayName).isEqualTo("Alisa")
        assertThat(characters[1].id).isEqualTo("jin")
        assertThat(characters[1].displayName).isEqualTo("Jin")
        assertThat(characters[2].id).isEqualTo("kazuya")
        assertThat(characters[2].displayName).isEqualTo("Kazuya")
    }

    @Test
    fun `invoke maps character aliases correctly`() = runTest {
        // Given
        val mockResponse = TestCharacterListDto(
            characters = listOf(
                TestCharacterDto(
                    id = "devil-jin",
                    displayName = "Devil Jin",
                    wavuName = "Devil Jin",
                    aliasList = listOf("dvj", "dj", "deviljin", "djin"),
                    images = TestImagesDto(
                        largePng = "https://example.com/dvj.png",
                        officialLargePng = "https://example.com/dvj-official.png"
                    )
                )
            )
        )
        val mockDataSource = MockWikiDataSource<TestCharacterListDto, Unit>()
        mockDataSource.characterListResponse = Result.Success(mockResponse)

        val useCase = DownloadCharacterListUseCase(
            source = mockDataSource,
            toDomain = { this.toDomain() },
            toDomainError = { this }
        )

        // When
        val result = useCase.invoke()

        // Then
        result as Result.Success
        val character = result.data.first()
        assertThat(character.aliasList).hasSize(4)
        assertThat(character.aliasList).isEqualTo(listOf("dvj", "dj", "deviljin", "djin"))
    }

    @Test
    fun `invoke maps character images correctly`() = runTest {
        // Given
        val mockResponse = TestCharacterListDto(
            characters = listOf(
                TestCharacterDto(
                    id = "jin",
                    displayName = "Jin",
                    wavuName = "Jin",
                    aliasList = listOf("jim"),
                    images = TestImagesDto(
                        largePng = "https://tekkendocs.com/t8/avatars/jin-brand-512.png",
                        officialLargePng = "https://tekkendocs.com/t8/avatars/jin-512.png"
                    )
                )
            )
        )
        val mockDataSource = MockWikiDataSource<TestCharacterListDto, Unit>()
        mockDataSource.characterListResponse = Result.Success(mockResponse)

        val useCase = DownloadCharacterListUseCase(
            source = mockDataSource,
            toDomain = { this.toDomain() },
            toDomainError = { this }
        )

        // When
        val result = useCase.invoke()

        // Then
        result as Result.Success
        val character = result.data.first()
        assertThat(character.images?.bannerUrl).isEqualTo("https://tekkendocs.com/t8/avatars/jin-brand-512.png")
        assertThat(character.images?.iconUrl).isEqualTo("https://tekkendocs.com/t8/avatars/jin-512.png")
    }

    @Test
    fun `invoke handles empty alias list correctly`() = runTest {
        // Given
        val mockResponse = TestCharacterListDto(
            characters = listOf(
                TestCharacterDto(
                    id = "king",
                    displayName = "King",
                    wavuName = "King",
                    aliasList = emptyList(),
                    images = TestImagesDto(
                        largePng = "https://example.com/king.png",
                        officialLargePng = "https://example.com/king-official.png"
                    )
                )
            )
        )
        val mockDataSource = MockWikiDataSource<TestCharacterListDto, Unit>()
        mockDataSource.characterListResponse = Result.Success(mockResponse)

        val useCase = DownloadCharacterListUseCase(
            source = mockDataSource,
            toDomain = { this.toDomain() },
            toDomainError = { this }
        )

        // When
        val result = useCase.invoke()

        // Then
        result as Result.Success
        val character = result.data.first()
        assertThat(character.aliasList).hasSize(0)
    }

    @Test
    fun `invoke returns empty list when response has no characters`() = runTest {
        // Given
        val mockResponse = TestCharacterListDto(characters = emptyList())
        val mockDataSource = MockWikiDataSource<TestCharacterListDto, Unit>()
        mockDataSource.characterListResponse = Result.Success(mockResponse)

        val useCase = DownloadCharacterListUseCase(
            source = mockDataSource,
            toDomain = { this.toDomain() },
            toDomainError = { this }
        )

        // When
        val result = useCase.invoke()

        // Then
        result as Result.Success
        assertThat(result.data).hasSize(0)
    }
    // endregion

    // region Error Cases
    @Test
    fun `invoke returns error when data source fails with UNKNOWN error`() = runTest {
        // Given
        val mockDataSource = MockWikiDataSource<TestCharacterListDto, Unit>()
        mockDataSource.characterListResponse = Result.Error(DataError.Remote.UNKNOWN)

        val useCase = DownloadCharacterListUseCase(
            source = mockDataSource,
            toDomain = { this.toDomain() },
            toDomainError = { this }
        )

        // When
        val result = useCase.invoke()

        // Then
        assertThat(result).isInstanceOf(Result.Error::class)
        val error = (result as Result.Error).error
        assertThat(error).isEqualTo(DataError.Remote.UNKNOWN)
    }

    @Test
    fun `invoke returns error when data source fails with NO_INTERNET error`() = runTest {
        // Given
        val mockDataSource = MockWikiDataSource<TestCharacterListDto, Unit>()
        mockDataSource.characterListResponse = Result.Error(DataError.Remote.NO_INTERNET)

        val useCase = DownloadCharacterListUseCase(
            source = mockDataSource,
            toDomain = { this.toDomain() },
            toDomainError = { this }
        )

        // When
        val result = useCase.invoke()

        // Then
        assertThat(result).isInstanceOf(Result.Error::class)
        val error = (result as Result.Error).error
        assertThat(error).isEqualTo(DataError.Remote.NO_INTERNET)
    }

    @Test
    fun `invoke returns error when data source fails with REQUEST_TIMEOUT error`() = runTest {
        // Given
        val mockDataSource = MockWikiDataSource<TestCharacterListDto, Unit>()
        mockDataSource.characterListResponse = Result.Error(DataError.Remote.REQUEST_TIMEOUT)

        val useCase = DownloadCharacterListUseCase(
            source = mockDataSource,
            toDomain = { this.toDomain() },
            toDomainError = { this }
        )

        // When
        val result = useCase.invoke()

        // Then
        assertThat(result).isInstanceOf(Result.Error::class)
        val error = (result as Result.Error).error
        assertThat(error).isEqualTo(DataError.Remote.REQUEST_TIMEOUT)
    }

    @Test
    fun `invoke returns error when data source fails with SERVER_ERROR`() = runTest {
        // Given
        val mockDataSource = MockWikiDataSource<TestCharacterListDto, Unit>()
        mockDataSource.characterListResponse = Result.Error(DataError.Remote.SERVER_ERROR)

        val useCase = DownloadCharacterListUseCase(
            source = mockDataSource,
            toDomain = { this.toDomain() },
            toDomainError = { this }
        )

        // When
        val result = useCase.invoke()

        // Then
        assertThat(result).isInstanceOf(Result.Error::class)
        val error = (result as Result.Error).error
        assertThat(error).isEqualTo(DataError.Remote.SERVER_ERROR)
    }
    // endregion

    // region Test Data
    private fun createTestCharacterListDto() = TestCharacterListDto(
        characters = listOf(
            TestCharacterDto(
                id = "alisa",
                displayName = "Alisa",
                wavuName = "Alisa",
                aliasList = listOf("ali", "als"),
                images = TestImagesDto(
                    largePng = "https://tekkendocs.com/t8/avatars/alisa-brand-512.png",
                    officialLargePng = "https://tekkendocs.com/t8/avatars/alisa-512.png"
                )
            ),
            TestCharacterDto(
                id = "jin",
                displayName = "Jin",
                wavuName = "Jin",
                aliasList = listOf("jim"),
                images = TestImagesDto(
                    largePng = "https://tekkendocs.com/t8/avatars/jin-brand-512.png",
                    officialLargePng = "https://tekkendocs.com/t8/avatars/jin-512.png"
                )
            ),
            TestCharacterDto(
                id = "kazuya",
                displayName = "Kazuya",
                wavuName = "Kazuya",
                aliasList = listOf("kaz", "masku"),
                images = TestImagesDto(
                    largePng = "https://tekkendocs.com/t8/avatars/kazuya-brand-512.png",
                    officialLargePng = "https://tekkendocs.com/t8/avatars/kazuya-512.png"
                )
            )
        )
    )
    // endregion

    // region Test DTOs
    data class TestCharacterListDto(
        val characters: List<TestCharacterDto>
    )

    data class TestCharacterDto(
        val id: String,
        val displayName: String,
        val wavuName: String,
        val aliasList: List<String>,
        val images: TestImagesDto
    )

    data class TestImagesDto(
        val largePng: String,
        val officialLargePng: String
    )
    // endregion

    private fun TestCharacterListDto.toDomain(): List<Character> {
        return characters.map { dto ->
            Character(
                id = dto.id,
                displayName = dto.displayName,
                wikiUrl = "https://wavu.wiki/t/${dto.wavuName}",
                aliasList = dto.aliasList,
                images = Character.Images(
                    bannerUrl = dto.images.largePng,
                    iconUrl = dto.images.officialLargePng
                )
            )
        }
    }

    private class MockWikiDataSource<C, M> : WikiDataSource<C, M> {
        var characterListResponse: Result<C, DataError.Remote>? = null

        override suspend fun downloadCharacterList(): Result<C, DataError.Remote> {
            return characterListResponse ?: Result.Error(DataError.Remote.UNKNOWN)
        }

        override suspend fun downloadMoveListFor(charName: String): Result<M, DataError.Remote> {
            throw NotImplementedError("Not needed for these tests")
        }
    }
}