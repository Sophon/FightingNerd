package usecase

import BotError
import com.example.wikiwavu.WavuError
import com.example.wikiwavu.WavuWikiClient
import com.example.core.domain.Result
import com.example.wikiwavu.domain.model.Move

class GetPowerCrushMovesUseCase(
    private val wiki: WavuWikiClient,
) {
    suspend fun invoke(charName: String): Result<List<Move>, BotError> {
        return when (val result = wiki.getPowerCrushMoves(charName)) {
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> {
                Result.Error(
                    when (result.error) {
                        WavuError.UNKNOWN_CHARACTER -> BotError.UNKNOWN_CHARACTER
                        WavuError.UNKNOWN_MOVE -> BotError.UNKNOWN_MOVE
                        WavuError.DOWNLOAD_ERROR -> BotError.DOWNLOAD_ERROR
                        WavuError.CHARACTER_LIST_NOT_FOUND,
                        WavuError.CHARACTER_SERIALIZATION_ERROR,
                            -> BotError.CHARACTER_LIST_FILE_ERROR
                    }
                )
            }
        }
    }
}