package io.github.sophon.wikiwavu.integration

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.asEmptyDataResult
import io.github.sophon.core.domain.map
import io.github.sophon.core.feature.Game
import io.github.sophon.core.feature.WikiClientFeature
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.core.wiki.usecase.CacheCharacterListUseCase
import io.github.sophon.core.wiki.usecase.CacheMoveListUseCase
import io.github.sophon.core.wiki.usecase.ClearCacheUseCase
import io.github.sophon.core.wiki.usecase.DownloadCharacterListUseCase
import io.github.sophon.core.wiki.usecase.DownloadMoveListUseCase
import io.github.sophon.core.wiki.usecase.FetchCharacterListUseCase
import io.github.sophon.core.wiki.usecase.FetchCharacterUseCase
import io.github.sophon.core.wiki.usecase.FetchMoveListUseCase
import io.github.sophon.core.wiki.usecase.FetchMoveUseCase
import io.github.sophon.core.wiki.usecase.GetLastCacheInsertInstantUseCase
import io.github.sophon.wikiwavu.data.WavuWikiDataSource
import io.github.sophon.wikiwavu.data.WavuWikiDataSourceImpl
import io.github.sophon.wikiwavu.data.toDomain
import io.github.sophon.wikiwavu.integration.WavuFeatureInfo
import io.github.sophon.wikiwavu.domain.WavuWikiClient
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun wavuModule() = module {
    singleOf(::WavuWikiDataSourceImpl).bind<WavuWikiDataSource>()
    singleOf(::WavuWikiClient).bind<WikiClient>()
    single { WavuFeatureInfo }

    factory {

    }

    factory<WikiClient>(named(WikiClientFeature.Wavu.id)) { params ->
        val gameId: String = params.get()
        val game = Game.fromId(gameId)
        val charListDB: CharacterListDB = params.get()
        val moveListDB: MoveListDB = params.get()
        val source: WavuWikiDataSource = get()

        WavuWikiClient(
            gameId = gameId,

            wavuFeatureInfo = get(),

            downloadCharacterListUseCase = DownloadCharacterListUseCase { queryTable ->
                source.downloadCharacterList().map { dto -> dto.toDomain() }
            },
            cacheCharacterListUseCase = CacheCharacterListUseCase {
                charListDB.insertCharacterList(it)
            },
            fetchCharacterListUseCase = FetchCharacterListUseCase {
                charListDB.fetchCharacterList()
            },
            fetchCharacterUseCase = FetchCharacterUseCase {
                charListDB.fetchCharacterDataFor(it)
            },

            downloadMoveListUseCase = DownloadMoveListUseCase { queryTable, characterData ->
                source.downloadMoveList(queryTable.moves, characterData)
                    .map { dto ->  dto.toDomain(characterData) }
            },
            cacheMoveListUseCase = CacheMoveListUseCase { character, moveList ->
                moveListDB.insertMoveList(game, character, moveList)
                    .asEmptyDataResult()
            },
            fetchMoveListUseCase = FetchMoveListUseCase { charName ->
                when (val result = moveListDB.fetchMoveListFor(charName)) {
                    is Result.Success -> {
                        if (result.data.isEmpty()) {
                            Result.Error(WikiError.DatabaseError("Empty"))
                        } else {
                            Result.Success(result.data)
                        }
                    }
                    is Result.Error -> Result.Error(result.error)
                }
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
            }
        )
    }
}
