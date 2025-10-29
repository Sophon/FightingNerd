package com.example.wikiwavu.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.example.core.domain.Result
import com.example.wikiwavu.CHAR_LIST
import com.example.wikiwavu.WavuError
import com.example.wikiwavu.infrastructure.FileReader
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
    fun `invoke returns Success when file content is valid`() {
        // Given
        val validJson = """
        {
            "characterList": [
                {
                    "name": "Jin",
                    "portrait": "https://i.imgur.com/example1.png",
                    "wavu_page": "https://wavu.wiki/t/Jin",
                    "alias": ["devil"]
                },
                {
                    "name": "Kazuya",
                    "portrait": "https://i.imgur.com/example2.png",
                    "wavu_page": "https://wavu.wiki/t/Kazuya",
                    "alias": []
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
        assertThat(characterList.characterList.size).isEqualTo(2)
        assertThat(characterList.characterList[0].name).isEqualTo("Jin")
        assertThat(characterList.characterList[0].alias).isEqualTo(listOf("devil"))
    }

    @Test
    fun `invoke returns CHARACTER_SERIALIZATION_ERROR when JSON is malformed`() {
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
    fun `invoke returns CHARACTER_LIST_NOT_FOUND when file reader throws exception`() {
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
    fun `invoke verifies correct file path is used`() {
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

    override fun readFile(path: String): String {
        lastPathUsed = path
        if (shouldThrow) throw Exception("File not found")
        return fileContent
    }
}