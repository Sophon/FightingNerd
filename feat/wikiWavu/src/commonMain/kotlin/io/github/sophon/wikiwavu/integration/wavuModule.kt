package io.github.sophon.wikiwavu.integration

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.asEmptyDataResult
import io.github.sophon.core.architecture.map
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.featureConfig.model.WikiClientFeature
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.core.wiki.usecase.CacheCharacterListUseCase
import io.github.sophon.core.wiki.usecase.CacheMoveListUseCase
import io.github.sophon.core.wiki.usecase.CheckHasCachedMoveListUseCase
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

            downloadCharacterListUseCase = DownloadCharacterListUseCase {
                source.downloadCharacterList().map { dto -> dto.toDomain() }
            },
            cacheCharacterListUseCase = CacheCharacterListUseCase(charListDB::insertCharacterList),
            fetchCharacterListUseCase = FetchCharacterListUseCase(charListDB::fetchCharacterList),
            fetchCharacterUseCase = FetchCharacterUseCase(charListDB::fetchCharacterDataFor),

            downloadMoveListUseCase = DownloadMoveListUseCase { queryTable, characterData ->
                source.downloadMoveList(queryTable.moves, characterData)
                    .map { dto ->  dto.toDomain(characterData) }
            },
            cacheMoveListUseCase = CacheMoveListUseCase { character, moveList ->
                moveListDB.insertMoveList(game, character, moveList)
                    .asEmptyDataResult()
            },
            fetchMoveListUseCase = FetchMoveListUseCase(moveListDB::fetchMoveListFor),
            fetchMoveUseCase = FetchMoveUseCase(moveListDB::fetchMoveDataFor),

            getLastCacheInsertInstantUseCase = GetLastCacheInsertInstantUseCase(moveListDB::getLastInsertTimeStamp),
            clearCacheUseCase = ClearCacheUseCase {
                val charResult = charListDB.wipe()
                val moveResult = moveListDB.wipe()

                when {
                    charResult is Result.Error -> charResult
                    moveResult is Result.Error -> moveResult
                    else -> Result.Success(Unit)
                }
            },
            checkHasCachedMoveListUseCase = CheckHasCachedMoveListUseCase(moveListDB::hasMovesCachedFor)
        )
    }
}
