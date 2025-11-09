package io.github.sophon.wikiwavu.usecase

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.domain.DataError
import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.wikiwavu.data.CharacterDto
import io.github.sophon.wikiwavu.data.CharacterListResponseDto
import io.github.sophon.wikiwavu.data.WavuWikiDataSource
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DownloadCharacterListUseCaseTest {
    //region Test Doubles
    private class FakeWavuWikiDataSource : WavuWikiDataSource {
        var characterListResult: Result<CharacterListResponseDto, DataError.Remote>? = null

        override suspend fun downloadCharacterList(): Result<CharacterListResponseDto, DataError.Remote> {
            return characterListResult ?: error("Result not set")
        }

        override suspend fun downloadMoveListFor(charName: String) = error("Not implemented")
    }
    //endregion

    //region Success Cases
    @Test
    fun `invoke with standard character returns success with mapped domain model`() = runTest {
        // Given
        val dto = CharacterListResponseDto(
            characters = listOf(
                CharacterDto(
                    id = "alisa",
                    displayName = "Alisa",
                    wavuName = "Alisa",
                    aliasList = listOf("ali", "als"),
                    images = CharacterDto.Images(
                        largePng = "https://tekkendocs.com/t8/avatars/alisa-brand-512.png",
                        officialLargePng = "https://tekkendocs.com/t8/avatars/alisa-512.png"
                    )
                )
            )
        )
        val dataSource = FakeWavuWikiDataSource().apply {
            characterListResult = Result.Success(dto)
        }
        val useCase = DownloadCharacterListUseCase(dataSource)

        // When
        val result = useCase.invoke()

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val characters = (result as Result.Success).data
        assertThat(characters).hasSize(1)
        assertThat(characters[0].id).isEqualTo("alisa")
        assertThat(characters[0].displayName).isEqualTo("Alisa")
        assertThat(characters[0].wikiUrl).isEqualTo("https://wavu.wiki/t/Alisa")
        assertThat(characters[0].aliasList).isEqualTo(listOf("ali", "als"))
        assertThat(characters[0].images?.iconUrl).isEqualTo("https://tekkendocs.com/t8/avatars/alisa-512.png")
    }

    @Test
    fun `invoke with character with no aliases returns success with empty alias list`() = runTest {
        // Given
        val dto = CharacterListResponseDto(
            characters = listOf(
                CharacterDto(
                    id = "jun",
                    displayName = "Jun",
                    wavuName = "Jun",
                    aliasList = listOf(),
                    images = CharacterDto.Images(
                        largePng = "https://tekkendocs.com/t8/avatars/jun-brand-512.png",
                        officialLargePng = "https://tekkendocs.com/t8/avatars/jun-512.png"
                    )
                )
            )
        )
        val dataSource = FakeWavuWikiDataSource().apply {
            characterListResult = Result.Success(dto)
        }
        val useCase = DownloadCharacterListUseCase(dataSource)

        // When
        val result = useCase.invoke()

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val characters = (result as Result.Success).data
        assertThat(characters).hasSize(1)
        assertThat(characters[0].id).isEqualTo("jun")
        assertThat(characters[0].aliasList).hasSize(0)
    }

    @Test
    fun `invoke with character with multiple aliases returns success with all aliases`() = runTest {
        // Given
        val dto = CharacterListResponseDto(
            characters = listOf(
                CharacterDto(
                    id = "devil-jin",
                    displayName = "Devil Jin",
                    wavuName = "Devil Jin",
                    aliasList = listOf("dvj", "dj", "deviljin", "djin"),
                    images = CharacterDto.Images(
                        largePng = "https://tekkendocs.com/t8/avatars/devil-jin-brand-512.png",
                        officialLargePng = "https://tekkendocs.com/t8/avatars/devil-jin-512.png"
                    )
                )
            )
        )
        val dataSource = FakeWavuWikiDataSource().apply {
            characterListResult = Result.Success(dto)
        }
        val useCase = DownloadCharacterListUseCase(dataSource)

        // When
        val result = useCase.invoke()

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val characters = (result as Result.Success).data
        assertThat(characters).hasSize(1)
        assertThat(characters[0].id).isEqualTo("devil-jin")
        assertThat(characters[0].aliasList).isEqualTo(listOf("dvj", "dj", "deviljin", "djin"))
    }

    @Test
    fun `invoke with character name containing space returns success with url-encoded wiki url`() = runTest {
        // Given
        val dto = CharacterListResponseDto(
            characters = listOf(
                CharacterDto(
                    id = "armor-king",
                    displayName = "Armor King",
                    wavuName = "Armor King",
                    aliasList = listOf("ak"),
                    images = CharacterDto.Images(
                        largePng = "https://tekkendocs.com/t8/avatars/armor-king-brand-512.png",
                        officialLargePng = "https://tekkendocs.com/t8/avatars/armor-king-512.png"
                    )
                )
            )
        )
        val dataSource = FakeWavuWikiDataSource().apply {
            characterListResult = Result.Success(dto)
        }
        val useCase = DownloadCharacterListUseCase(dataSource)

        // When
        val result = useCase.invoke()

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val characters = (result as Result.Success).data
        assertThat(characters).hasSize(1)
        assertThat(characters[0].wikiUrl).isEqualTo("https://wavu.wiki/t/Armor_King")
    }

    @Test
    fun `invoke with multiple characters returns success with all characters mapped`() = runTest {
        // Given
        val dto = CharacterListResponseDto(
            characters = listOf(
                CharacterDto(
                    id = "king",
                    displayName = "King",
                    wavuName = "King",
                    aliasList = listOf(),
                    images = CharacterDto.Images(
                        largePng = "https://tekkendocs.com/t8/avatars/king-brand-512.png",
                        officialLargePng = "https://tekkendocs.com/t8/avatars/king-512.png"
                    )
                ),
                CharacterDto(
                    id = "paul",
                    displayName = "Paul",
                    wavuName = "Paul",
                    aliasList = listOf(),
                    images = CharacterDto.Images(
                        largePng = "https://tekkendocs.com/t8/avatars/paul-brand-512.png",
                        officialLargePng = "https://tekkendocs.com/t8/avatars/paul-512.png"
                    )
                )
            )
        )
        val dataSource = FakeWavuWikiDataSource().apply {
            characterListResult = Result.Success(dto)
        }
        val useCase = DownloadCharacterListUseCase(dataSource)

        // When
        val result = useCase.invoke()

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val characters = (result as Result.Success).data
        assertThat(characters).hasSize(2)
        assertThat(characters[0].id).isEqualTo("king")
        assertThat(characters[1].id).isEqualTo("paul")
    }
    //endregion

    //region Error Cases
    @Test
    fun `invoke with no internet error returns download error`() = runTest {
        // Given
        val dataSource = FakeWavuWikiDataSource().apply {
            characterListResult = Result.Error(DataError.Remote.NO_INTERNET)
        }
        val useCase = DownloadCharacterListUseCase(dataSource)

        // When
        val result = useCase.invoke()

        // Then
        assertThat(result).isInstanceOf(Result.Error::class)
        val error = (result as Result.Error).error
        assertThat(error).isEqualTo(WikiError.DOWNLOAD_ERROR)
    }

    @Test
    fun `invoke with request timeout error returns download error`() = runTest {
        // Given
        val dataSource = FakeWavuWikiDataSource().apply {
            characterListResult = Result.Error(DataError.Remote.REQUEST_TIMEOUT)
        }
        val useCase = DownloadCharacterListUseCase(dataSource)

        // When
        val result = useCase.invoke()

        // Then
        assertThat(result).isInstanceOf(Result.Error::class)
        val error = (result as Result.Error).error
        assertThat(error).isEqualTo(WikiError.DOWNLOAD_ERROR)
    }
    //endregion
}