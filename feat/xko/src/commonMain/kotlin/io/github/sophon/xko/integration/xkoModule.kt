package io.github.sophon.xko.integration

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.feature.Game
import io.github.sophon.core.feature.WikiClientFeature
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.core.wiki.usecase.CacheCharacterListUseCase
import io.github.sophon.core.wiki.usecase.CacheMoveListUseCase
import io.github.sophon.core.wiki.usecase.ClearCacheUseCase
import io.github.sophon.core.wiki.usecase.DownloadOrFetchUseCase
import io.github.sophon.core.wiki.usecase.FetchCharacterListUseCase
import io.github.sophon.core.wiki.usecase.FetchCharacterUseCase
import io.github.sophon.core.wiki.usecase.FetchMoveListUseCase
import io.github.sophon.core.wiki.usecase.FetchMoveUseCase
import io.github.sophon.core.wiki.usecase.GetLastCacheInsertInstantUseCase
import io.github.sophon.xko.data.XkoWikiDataSource
import io.github.sophon.xko.data.XkoWikiDataSourceImpl
import io.github.sophon.xko.data.toDomain
import io.github.sophon.xko.integration.XkoFeatureInfo
import io.github.sophon.xko.domain.XkoWikiClient
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun xkoModule() = module {
    singleOf(::XkoWikiDataSourceImpl).bind<XkoWikiDataSource>()
    singleOf(::XkoWikiClient).bind<WikiClient>()
    single { XkoFeatureInfo }

    factory<WikiClient>(named(WikiClientFeature.Xko.id)) { params ->
        val gameId: String = params.get()
        val game = Game.fromId(gameId)
        val charListDB: CharacterListDB = params.get()
        val moveListDB: MoveListDB = params.get()
        val source: XkoWikiDataSource = get()

        XkoWikiClient(
            gameId = gameId,

            downloadOrFetchUseCase = DownloadOrFetchUseCase { table ->
                source.downloadMoveList()
                    .map { it.toDomain() }
            },

            cacheCharacterListUseCase = CacheCharacterListUseCase { characterList ->
                charListDB.insertCharacterList(characterList)
            },
            fetchCharacterListUseCase = FetchCharacterListUseCase {
                charListDB.fetchCharacterList()
            },
            fetchCharacterUseCase = FetchCharacterUseCase { charName ->
                charListDB.fetchCharacterDataFor(charName)
            },

            cacheMoveListUseCase = CacheMoveListUseCase { character, moveList ->
                moveListDB.insertMoveList(game, character, moveList)
            },
            fetchMoveListUseCase = FetchMoveListUseCase { charName ->
                moveListDB.fetchMoveListFor(charName)
            },
            fetchMoveUseCase = FetchMoveUseCase { charName, moveQuery ->
                moveListDB.fetchMoveDataFor(charName, moveQuery)
            },

            getLastCacheInsertInstantUseCase = GetLastCacheInsertInstantUseCase {
                moveListDB.getLastInsertTimeStamp()
            },
            clearCacheUseCase = ClearCacheUseCase {
                val charResult = charListDB.wipe()
                val moveResult = moveListDB.wipe()

                when {
                    charResult is Result.Error -> charResult
                    moveResult is Result.Error -> moveResult
                    else -> Result.Success(Unit)
                }
            },
        )
    }
}