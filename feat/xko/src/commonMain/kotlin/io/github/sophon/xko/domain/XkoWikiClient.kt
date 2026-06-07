package io.github.sophon.xko.domain

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.BaseWikiClient
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.usecase.CachedDownloadUseCase
import io.github.sophon.xko.data.XkoWikiDataSource
import io.github.sophon.xko.data.toDomain
import io.github.sophon.xko.integration.XkoFeatureInfo
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class XkoWikiClient(
    private val game: Game,
    private val source: XkoWikiDataSource,
    characterDB: CharacterListDB,
    moveDB: MoveListDB,
): BaseWikiClient(
    game = game,
    featureInfo = XkoFeatureInfo.featureInfo,
    characterDB = characterDB,
    moveDB = moveDB,
) {
    private val cachedDownloadUseCase = CachedDownloadUseCase {
        //TODO: resolve char images and hitboxes
        source.downloadMoveList().map { dto -> dto.toDomain() }
    }

    override suspend fun downloadCharacterList(): Result<List<Character>, WikiError> {
        val result = cachedDownloadUseCase.invoke().map { map ->
            val characterList = map.keys.toList()
            Napier.i(tag = TAG) { "${game.id}: ${characterList.size} downloaded" }
            characterList
        }
        return result
    }

    override suspend fun downloadMoveListFor(character: Character): Result<List<Move>, WikiError> {
        val result = cachedDownloadUseCase.invoke()
            .map { map ->
                val moveList = map
                    .filterKeys { it.remoteQueryId == character.remoteQueryId }
                    .values
                    .flatten()
                Napier.d(tag = TAG) { "${character.id} (${game.id}): ${moveList.size} moves downloaded" }
                moveList
            }
        return result
    }

    override suspend fun onClearCache() {
        cachedDownloadUseCase.clearCache()
    }


    private companion object {
        const val TAG = "XkoWikiClient"
    }
}