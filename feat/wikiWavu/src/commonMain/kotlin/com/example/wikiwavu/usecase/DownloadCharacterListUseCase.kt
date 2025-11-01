package com.example.wikiwavu.usecase

import io.github.sophon.core.domain.Result
import com.example.wikiwavu.CHAR_LIST
import com.example.wikiwavu.WavuError
import com.example.wikiwavu.domain.model.Character
import com.example.wikiwavu.infrastructure.FileReader
import io.github.aakira.napier.Napier
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

/**
 * TODO: ideally we first attempt to download the github json
 *
 * 1. [Char list](https://raw.githubusercontent.com/pbruvoll/tekkendocs/refs/heads/main/utils/wavu-importer/src/resources/character_list.json)
 * 2. read from the /res/characters.json
 */
class DownloadCharacterListUseCase(
    private val fileReader: FileReader,
    private val json: Json,
) {
    internal suspend fun invoke(): Result<List<Character>, WavuError> {
        return try {
            val fileContent = fileReader.readFile(path = "res/${CHAR_LIST}")
            val charList = json.decodeFromString<List<Character>>(fileContent)
            Result.Success(charList)
        } catch (e: SerializationException) {
            Napier.e(tag = TAG) { e.toString() }
            Result.Error(WavuError.CHARACTER_SERIALIZATION_ERROR)
        } catch (e: Exception) {
            Napier.e(tag = TAG) { e.toString() }
            Result.Error(WavuError.CHARACTER_LIST_NOT_FOUND)
        }
    }
}

private const val TAG = "FetchCharactersListUseCase"