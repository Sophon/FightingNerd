package usecase

import BotError
import WavuError
import WavuWikiClient
import com.example.core.domain.Result
import com.example.core.util.dropFirstAndJoin
import model.Move

internal class SearchFrameDataUseCase(
    private val wavuWikiClient: WavuWikiClient,
) {
    suspend fun invoke(query: String): Result<Move, BotError> {
        val parsedQuery = parseQuery(query)
        if (parsedQuery == null) return Result.Error(BotError.INVALID_QUERY)

        return when (
            val result = wavuWikiClient.frameDataFor(charName = parsedQuery.charName, moveQuery = parsedQuery.move)
        ) {
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> Result.Error(
                when (result.error) {
                    WavuError.UNKNOWN_CHARACTER -> BotError.UNKNOWN_CHARACTER
                    WavuError.UNKNOWN_MOVE -> BotError.UNKNOWN_MOVE
                    WavuError.CHARACTER_LIST_NOT_FOUND,
                    WavuError.CHARACTER_SERIALIZATION_ERROR,
                        -> BotError.CHARACTER_LIST_FILE_ERROR
                }
            )
        }
    }

    private fun parseQuery(query: String): ParsedQuery? {
        if (query.split(" ").size < 2) return null

        val charName = query.substringBefore(' ')
        val move = query.dropFirstAndJoin(' ')

        return ParsedQuery(charName, move)
    }

    private data class ParsedQuery(
        val charName: String,
        val move: String,
    )
}