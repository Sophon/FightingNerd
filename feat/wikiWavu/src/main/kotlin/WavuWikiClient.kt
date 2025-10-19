import com.example.core.domain.EmptyResult
import com.example.core.domain.Result
import com.example.core.domain.Service
import com.example.core.domain.Source
import com.example.core.domain.onError
import io.github.aakira.napier.Napier
import model.Move
import usecase.CacheMoveListUseCase
import usecase.DownloadMoveListUseCase
import usecase.FetchCharacterListUseCase
import usecase.FetchMoveDataUseCase

interface WavuWikiClient: Service {
    suspend fun downloadCompleteMoveList(): EmptyResult<WavuError>
    suspend fun frameDataFor(charName: String, moveQuery: String): Result<Move, WavuError>
}

internal class WavuWikiClientImpl(
    private val fetchCharacterListUseCase: FetchCharacterListUseCase,
    private val downloadMoveListUseCase: DownloadMoveListUseCase,
    private val cacheMoveListUseCase: CacheMoveListUseCase,
    private val fetchMoveDataUseCase: FetchMoveDataUseCase,
): WavuWikiClient {
    override suspend fun downloadCompleteMoveList(): EmptyResult<WavuError> {
        return when (val result = fetchCharacterListUseCase.invoke()) {
            is Result.Success -> {
                result.data.characterList.forEach { character ->
                    when (val moveListResult = downloadMoveListUseCase.invoke(character.name)) {
                        is Result.Success -> {
                            cacheMoveListUseCase.invoke(character, moveListResult.data)
                            Napier.d(tag = TAG) {
                                "${moveListResult.data.size} moves for ${character.name} (${character.alias}) added"
                            }
                        }
                        is Result.Error -> {
                            Napier.e(tag = TAG) { "Error: ${moveListResult.error} for $character" }
                            moveListResult.error
                        }
                    }
                }

                Result.Success(Unit)
            }
            is Result.Error -> {
                Result.Error(result.error)
            }
        }
    }

    override suspend fun frameDataFor(
        charName: String,
        moveQuery: String
    ): Result<Move, WavuError> {
        return fetchMoveDataUseCase.invoke(charName, moveQuery)
            .onError { Napier.e(tag = TAG) { it.toString() } }
    }

    override fun source(): Source {
        return Source(
            name = SERVICE_NAME,
            iconUrl = "https://i.imgur.com/0cnTzNk.png"
        )
    }
}


private const val TAG = "WavuWikiClient"