package io.github.sophon.wikiwavu.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.domain.Result
import io.github.sophon.wikiwavu.CHAR_LIST
import io.github.sophon.wikiwavu.WavuError
import io.github.sophon.wikiwavu.infrastructure.FileReader
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.BeforeTest
import kotlin.test.Test

class DownloadCharacterListUseCaseTest {
    private lateinit var useCase: DownloadCharacterListUseCase
    private lateinit var fakeFileReader: FakeFileReader
    private val json = Json { ignoreUnknownKeys = true }

    @BeforeTest
    fun setup() {
        fakeFileReader = FakeFileReader()
        useCase = DownloadCharacterListUseCase(
            fileReader = fakeFileReader,
            json = json
        )
    }

    @Test
    fun `invoke returns Success when file content is valid`() = runTest {
        // Given
        val validJson = """
    [
        {
          "name": "Jin",
          "portrait": "https://i.imgur.com/ucx6sUa.png",
          "wavu_page": "https://wavu.wiki/t/Jin",
          "alias": ["jim"]
        },
        {
          "name": "Kazuya",
          "portrait": "https://i.imgur.com/HhPyKVn.png",
          "wavu_page": "https://wavu.wiki/t/Kazuya",
          "alias": ["kaz", "masku"]
        }
    ]
""".trimIndent()
        fakeFileReader.fileContent = validJson

        // When
        val result = useCase.invoke()

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val characterList = (result as Result.Success).data
        assertThat(characterList.size).isEqualTo(2)
        assertThat(characterList[0].name).isEqualTo("Jin")
        assertThat(characterList[0].alias).isEqualTo(listOf("jim")) // Fixed to match the JSON
    }

    @Test
    fun `invoke returns CHARACTER_SERIALIZATION_ERROR when JSON is malformed`() = runTest {
        // Given
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
        fakeFileReader.fileContent = """{"characters": []}"""

        // When
        useCase.invoke()

        // Then
        assertThat(fakeFileReader.lastPathUsed).isEqualTo("res/$CHAR_LIST")
    }
}

// Test Fake - put in commonTest
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