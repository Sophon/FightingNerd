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
import io.github.sophon.core.wiki.model.RefreshEvent
import io.github.sophon.core.wiki.model.WikiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
abstract class BaseWikiClient(
    protected val game: Game,
    override val featureInfo: FeatureInfo,
    private val characterRepo: CharacterRepo,
    private val moveRepo: MoveRepo,
    private val scope: CoroutineScope,
    private val infoLogger: (String) -> Unit = {},
    private val debugLogger: (String) -> Unit = {},
) : WikiClient {
    private val refreshMutex = Mutex()
    private var currentSession: RefreshSession? = null

    final override fun refreshData(): Flow<RefreshEvent> {
        val flow = flow {
            val session = refreshMutex.withLock {
                currentSession ?: startRefreshSession().also { currentSession = it }
            }
            val terminated = session.events.transformWhile { event ->
                emit(event)
                event !is RefreshEvent.Finished
            }
            emitAll(terminated)
        }
        return flow
    }

    override fun subscribeToCharacterList(): Flow<List<Character>> {
        val flow = characterRepo.subscribeToCharacterList()
            .onEach { list ->
                if (list.isEmpty()) {
                    refreshData().launchIn(scope)
                }
            }
        return flow
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


    private fun startRefreshSession(): RefreshSession {
        val session = RefreshSession()
        scope.launch {
            try {
                performRefresh(session)
            } finally {
                refreshMutex.withLock {
                    if (currentSession === session) {
                        currentSession = null
                    }
                }
            }
        }
        return session
    }

    private suspend fun performRefresh(session: RefreshSession) {
        //TODO: handle per-request timeout and host-unresponsive short-circuit
        val charResult = characterRepo.refreshCharacterList()
        if (charResult is Result.Error) {
            session.events.emit(RefreshEvent.Failed(charResult.error.toDomainError()))
            session.events.emit(RefreshEvent.Finished(successCount = 0))
            return
        }

        val characterList = characterRepo.subscribeToCharacterList().first()
        infoLogger("${game.id}: ${characterList.size} characters downloaded")

        var successCount = 0
        for (character in characterList) {
            moveRepo.refreshMoveList(character)
                .onSuccess { moveListSize ->
                    debugLogger("${character.id} (${game.id}): $moveListSize moves downloaded")
                    successCount++
                }
                .onError { moveError ->
                    session.events.emit(RefreshEvent.Failed(moveError.toDomainError()))
                }
        }
        session.events.emit(RefreshEvent.Finished(successCount))
    }

    private class RefreshSession {
        val events = MutableSharedFlow<RefreshEvent>(replay = REPLAY_CAP)
    }

    private companion object {
        private const val REPLAY_CAP = 128
    }
}
