package io.github.sophon.wikiwavu.usecase

import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.Result
import io.github.sophon.wikiwavu.CHAR_LIST
import io.github.sophon.wikiwavu.MOVE_URL
import io.github.sophon.wikiwavu.WavuError
import io.github.sophon.wikiwavu.data.CharacterListResponseDto
import io.github.sophon.wikiwavu.data.TekkenDocsDataSource
import io.github.sophon.wikiwavu.domain.model.Character
import io.github.sophon.wikiwavu.infrastructure.FileReader
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

/**
 * Technically, the source is not Wavu Wiki.
 * But the appeals to make a table for Character have not been successful.
 */
internal class DownloadCharacterListUseCase(
    private val source: TekkenDocsDataSource,
    private val fileReader: FileReader,
    private val json: Json,
) {
    internal suspend fun invoke(): Result<List<Character>, WavuError> {
        return when (val result = source.downloadCharacterList()) {
            is Result.Success -> Result.Success(result.data.toDomain())
            is Result.Error -> {
                Napier.e(tag = TAG) { "Failed to download: ${result.error}" }
                loadFromLocalFile()
            }
        }
    }

    private suspend fun loadFromLocalFile(): Result<List<Character>, WavuError> {
        return try {
            val fileContent = fileReader.readFile(path = "res/$CHAR_LIST")
            val response = json.decodeFromString<CharacterListResponseDto>(fileContent)  // ← Decode to wrapper
            Result.Success(response.toDomain())
        } catch (e: SerializationException) {
            Napier.e(tag = TAG) { "Serialization error: $e" }
            Result.Error(WavuError.CHARACTER_SERIALIZATION_ERROR)
        } catch (e: Exception) {
            Napier.e(tag = TAG) { "File not found: $e" }
            Result.Error(WavuError.CHARACTER_LIST_NOT_FOUND)
        }
    }

    private fun CharacterListResponseDto.toDomain(): List<Character> {
        return characters.map { dto ->
            Character(
                id = dto.id,
                displayName = dto.displayName,
                wikiUrl = MOVE_URL + dto.wavuName.replace(" ", "_"),
                aliasList = dto.aliasList,
                images = Character.Images(
                    url = dto.images?.largePng,
                    officialUrl = dto.images?.officialLargePng,
                )
            )
        }
    }
}

private const val TAG = "FetchCharactersListUseCase"