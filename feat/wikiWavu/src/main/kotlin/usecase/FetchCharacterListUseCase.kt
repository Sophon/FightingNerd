package usecase

import CHAR_LIST
import WavuError
import com.example.core.domain.Result
import domain.model.CharacterList
import io.github.aakira.napier.Napier
import kotlinx.io.files.FileNotFoundException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.File

class FetchCharacterListUseCase(
    private val json: Json,
) {
    internal fun invoke(): Result<CharacterList, WavuError> {
        val charListFile = File("res/${CHAR_LIST}")

        return try {
            val fileContent = charListFile.readText()
            val charList = json.decodeFromString<CharacterList>(fileContent)
            Result.Success(charList)
        } catch (e: FileNotFoundException) {
            Napier.e(tag = TAG) { e.toString() }
            Result.Error(WavuError.CHARACTER_LIST_NOT_FOUND)
        } catch (e: SerializationException) {
            Napier.e(tag = TAG) { e.toString() }
            Result.Error(WavuError.CHARACTER_SERIALIZATION_ERROR)
        }
    }
}

private const val TAG = "FetchCharactersListUseCase"