package io.github.sophon.wikiwavu.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.domain.DataError
import io.github.sophon.core.domain.Result
import io.github.sophon.wikiwavu.CHAR_LIST
import io.github.sophon.wikiwavu.WavuError
import io.github.sophon.wikiwavu.data.CharacterListResponseDto
import io.github.sophon.wikiwavu.data.TekkenDocsDataSource
import io.github.sophon.wikiwavu.infrastructure.FileReader
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.BeforeTest
import kotlin.test.Test

class DownloadCharacterListUseCaseTest {
    private lateinit var useCase: DownloadCharacterListUseCase
    private lateinit var fakeFileReader: FakeFileReader
    private lateinit var fakeSource: FakeTekkenDocsDataSource
    private val json = Json { ignoreUnknownKeys = true }

    @BeforeTest
    fun setup() {
        fakeFileReader = FakeFileReader()
        fakeSource = FakeTekkenDocsDataSource()
        useCase = DownloadCharacterListUseCase(
            fileReader = fakeFileReader,
            json = json,
            source = fakeSource,
        )
    }

    @Test
    fun `invoke returns Success when file content is valid`() = runTest {
        // Given
        fakeSource.result = Result.Error(DataError.Remote.REQUEST_TIMEOUT)
        val validJson = """
        {
          "characters": [
            {
              "id": "jin",
              "displayName": "Jin",
              "wavuName": "Jin",
              "aliasList": ["jim"],
              "images": {
                "largePng": "https://tekkendocs.com/t8/avatars/jin-brand-512.png",
                "officialLargePng": "https://tekkendocs.com/t8/avatars/jin-512.png"
              }
            },
            {
              "id": "kazuya",
              "displayName": "Kazuya",
              "wavuName": "Kazuya",
              "aliasList": ["kaz", "masku"],
              "images": {
                "largePng": "https://tekkendocs.com/t8/avatars/kazuya-brand-512.png",
                "officialLargePng": "https://tekkendocs.com/t8/avatars/kazuya-512.png"
              }
            }
          ]
        }
        """.trimIndent()
        fakeFileReader.fileContent = validJson

        // When
        val result = useCase.invoke()

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val characterList = (result as Result.Success).data
        assertThat(characterList.size).isEqualTo(2)
        assertThat(characterList[0].displayName).isEqualTo("Jin")
        assertThat(characterList[0].aliasList).isEqualTo(listOf("jim"))
    }

    @Test
    fun `invoke returns CHARACTER_SERIALIZATION_ERROR when JSON is malformed`() = runTest {
        // Given
        fakeSource.result = Result.Error(DataError.Remote.REQUEST_TIMEOUT)
        fakeFileReader.fileContent = "{ invalid json }"

        // When
        val result = useCase.invoke()

        // Then
        assertThat(result).isInstanceOf(Result.Error::class)
        val error = (result as Result.Error).error
        assertThat(error).isEqualTo(WavuError.CHARACTER_SERIALIZATION_ERROR)
    }

    @Test
    fun `invoke returns CHARACTER_LIST_NOT_FOUND when file reader throws exception`() = runTest {
        // Given
        fakeFileReader.shouldThrow = true
        fakeSource.result = Result.Error(DataError.Remote.SERVER_ERROR)

        // When
        val result = useCase.invoke()

        // Then
        assertThat(result).isInstanceOf(Result.Error::class)
        val error = (result as Result.Error).error
        assertThat(error).isEqualTo(WavuError.CHARACTER_LIST_NOT_FOUND)
    }

    @Test
    fun `invoke verifies correct file path is used`() = runTest {
        // Given
        fakeSource.result = Result.Error(DataError.Remote.REQUEST_TIMEOUT)
        fakeFileReader.fileContent = """{"characters": []}"""

        // When
        useCase.invoke()

        // Then
        assertThat(fakeFileReader.lastPathUsed).isEqualTo("res/$CHAR_LIST")
    }
}

// region Test Fake - put in commonTest
class FakeFileReader : FileReader {
    var fileContent: String = ""
    var shouldThrow: Boolean = false
    var lastPathUsed: String? = null

    override suspend fun readFile(path: String): String {
        lastPathUsed = path
        if (shouldThrow) throw Exception("File not found")
        return fileContent
    }
}

internal class FakeTekkenDocsDataSource : TekkenDocsDataSource {
    var result: Result<CharacterListResponseDto, DataError.Remote> = Result.Success(
        CharacterListResponseDto(emptyList())
    )

    override suspend fun downloadCharacterList(): Result<CharacterListResponseDto, DataError.Remote> {
        return result
    }
}
//endregion