package io.github.sophon.core.wiki.domain

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.CharacterRepo
import io.github.sophon.core.wiki.data.MoveRepo
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.data.toDomainError
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.CharacterId
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.model.WikiClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
abstract class BaseWikiClient(
    protected val game: Game,
    override val featureInfo: FeatureInfo,
    private val characterRepo: CharacterRepo,
    private val moveRepo: MoveRepo,
    private val infoLogger: (String) -> Unit = {},
    private val debugLogger: (String) -> Unit = {},
) : WikiClient {
    override suspend fun refreshData(): EmptyResult<WikiError> {
        //TODO: handle per-request timeout and host-unresponsive short-circuit
        val charResult = characterRepo.refreshCharacterList()
        if (charResult is Result.Error) {
            val error = Result.Error(charResult.error.toDomainError())
            return error
        }

        val characterList = characterRepo.subscribeToCharacterList().first()
        infoLogger("${game.id}: ${characterList.size} characters downloaded")

        for (character in characterList) {
            moveRepo.refreshMoveList(character)
                .onSuccess { moveListSize ->
                    debugLogger("${character.id} (${game.id}): $moveListSize moves downloaded")
                }
                .onError {
                    val error = Result.Error(it.toDomainError())
                    return error
                }
        }
        val result = Result.Success(Unit)
        return result
    }

    override fun subscribeToCharacterList(): Flow<List<Character>> {
        return characterRepo.subscribeToCharacterList()
    }

    override fun subscribeToMoveList(characterId: CharacterId): Flow<List<Move>> {
        return moveRepo.subscribeToMoveList(characterId.value)
    }

    final override suspend fun getLastUpdateTimeStamp(): Result<Instant?, WikiError> {
        val result = moveRepo.getLastUpdateTimestamp().mapError { it.toDomainError() }
        return result
    }

    final override suspend fun clearCache(): EmptyResult<WikiError> {
        val charResult = characterRepo.wipeData().mapError { it.toDomainError() }
        val moveResult = moveRepo.wipeData().mapError { it.toDomainError() }
        val result = when {
            charResult is Result.Error -> charResult
            moveResult is Result.Error -> moveResult
            else -> {
                onClearCache()
                Result.Success(Unit)
            }
        }
        return result
    }

    protected open suspend fun onClearCache() { /* no-op by default */ }
}
