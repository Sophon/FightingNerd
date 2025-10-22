package usecase

import BotError
import WavuError
import WavuWikiClient
import com.example.core.domain.Result
import domain.model.Move

class GetHeatMovesUseCase(
    private val wiki: WavuWikiClient,
) {
    suspend fun invoke(charName: String): Result<List<Move>, BotError> {
        return when (val result = wiki.getHeatMoves(charName)) {
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