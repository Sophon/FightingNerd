package io.github.sophon.dreamcancel.domain

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.CharacterRepo
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.data.MoveRepo
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.data.toDomainError
import io.github.sophon.core.wiki.domain.BaseWikiClient
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.dreamcancel.data.DreamCancelWikiDataSource
import io.github.sophon.dreamcancel.data.remote.DreamCancelDataCache
import io.github.sophon.dreamcancel.integration.DreamCancelFeatureInfo
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class DreamCancelWikiClient(
    game: Game,
    characterDB: CharacterListDB,
    moveDB: MoveListDB,
    @Suppress("unused") private val source: DreamCancelWikiDataSource,
    private val dataCache: DreamCancelDataCache,
    characterRepo: CharacterRepo,
    moveRepo: MoveRepo,
) : BaseWikiClient(
    game = game,
    featureInfo = DreamCancelFeatureInfo.featureInfo,
    characterDB = characterDB,
    moveDB = moveDB,
    characterRepo = characterRepo,
    moveRepo = moveRepo,
    infoLogger = { Napier.i(tag = TAG) { it } },
    debugLogger = { Napier.d(tag = TAG) { it } },
) {
    override val supportedGameSet = DreamCancelFeatureInfo.featureInfo.supportedGameSet

    override suspend fun downloadCharacterList(): Result<List<Character>, WikiError> {
        val result = dataCache.getOrFetch()
            .map { map ->
                val characterList = map.keys.toList()
                characterList
            }
            .mapError { it.toDomainError() }
        return result
    }

    override suspend fun downloadMoveListFor(character: Character): Result<List<Move>, WikiError> {
        val result = dataCache.getOrFetch()
            .map { map ->
                val moveList = map
                    .filterKeys { it.remoteQueryId == character.remoteQueryId }
                    .values
                    .flatten()
                moveList
            }
            .mapError { it.toDomainError() }
        return result
    }

    override suspend fun onClearCache() {
        dataCache.clear()
    }

    override fun getFiltersFor(game: Game): Set<Filter> {
        require(game in supportedGameSet) {
            "${game.id} not supported. Supported: $supportedGameSet"
        }

        return emptySet()
    }


    private companion object {
        const val TAG = "DreamCancelWikiClient"
    }
}
