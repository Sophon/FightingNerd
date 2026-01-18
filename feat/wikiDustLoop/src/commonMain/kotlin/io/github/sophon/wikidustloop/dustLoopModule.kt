package io.github.sophon.wikidustloop

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.asEmptyDataResult
import io.github.sophon.core.domain.flatMap
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
import io.github.sophon.wikidustloop.data.DustLoopDataSource
import io.github.sophon.wikidustloop.data.DustLoopDataSourceImpl
import io.github.sophon.wikidustloop.data.ImageUrlResolver
import io.github.sophon.wikidustloop.data.toDomain
import io.github.sophon.wikidustloop.domain.DustLoopFeatureInfo
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun dustLoopModule() = module {
    singleOf(::DustLoopDataSourceImpl).bind<DustLoopDataSource>()
    singleOf(::DustLoopWikiClient).bind<WikiClient>()
    single { DustLoopFeatureInfo }
    factoryOf(::ImageUrlResolver)

    factory<WikiClient>(named(WikiClientFeature.DustLoop.id)) { params ->
        val gameId: String = params.get()
        val game = Game.fromId(gameId)
        val characterListDB: CharacterListDB = params.get()
        val moveListDB: MoveListDB = params.get()
        val source: DustLoopDataSource = get()
        val imageUrlResolver: ImageUrlResolver = get()

        DustLoopWikiClient(
            gameId = gameId,

            dustLoopFeatureInfo = get(),

            downloadCharacterListUseCase = DownloadCharacterListUseCase { queryTable ->
                source.downloadCharacterList(queryTable.character)
                    .flatMap { dto ->
                        imageUrlResolver.resolveImageUrls(dto)
                            .map { dto.toDomain(imageUrlMap = it, gameId = gameId) }
                    }
            },
            cacheCharacterListUseCase = CacheCharacterListUseCase { characterList ->
                characterListDB.insertCharacterList(characterList)
            },
            fetchCharacterListUseCase = FetchCharacterListUseCase {
                when (val result = characterListDB.fetchCharacterList()) {
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
            fetchCharacterUseCase = FetchCharacterUseCase { charName ->
                characterListDB.fetchCharacterDataFor(charName)
            },

            downloadMoveListUseCase = DownloadMoveListUseCase { queryTable, characterData ->
                source.downloadMoveList(queryTable.moves, characterData)
                    .flatMap { dto ->
                        imageUrlResolver.resolveHitboxUrl(dto)
                            .map { dto.toDomain(gameId, characterData, it) }
                    }
            },
            cacheMoveListUseCase = CacheMoveListUseCase { character, moveList ->
                moveListDB.insertMoveList(game, character, moveList)
                    .asEmptyDataResult()
            },
            fetchMoveUseCase = FetchMoveUseCase { charName, moveQuery ->
                moveListDB.fetchMoveDataFor(charName, moveQuery)
            },
            fetchMoveListUseCase = FetchMoveListUseCase { charName ->
                moveListDB.fetchMoveListFor(charName)
            },

            getLastCacheInsertInstantUseCase = GetLastCacheInsertInstantUseCase {
                moveListDB.getLastInsertTimeStamp()
            },
            clearCacheUseCase = ClearCacheUseCase {
                val charResult = characterListDB.wipe()
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