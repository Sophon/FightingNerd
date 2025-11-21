package io.github.sophon.dreamcancel

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.flatMap
import io.github.sophon.core.domain.map
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
import io.github.sophon.dreamcancel.data.DreamCancelWikiDataSource
import io.github.sophon.dreamcancel.data.DreamCancelWikiDataSourceImpl
import io.github.sophon.dreamcancel.data.WikiImageUrlResolver
import io.github.sophon.dreamcancel.data.toDomain
import io.github.sophon.dreamcancel.domain.DreamCancelFeatureInfo
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

fun dreamCancelModule() = module { 
    singleOf(::DreamCancelWikiDataSourceImpl).bind<DreamCancelWikiDataSource>()
    singleOf(::DreamCancelWikiClient).bind<WikiClient>()
    single { DreamCancelFeatureInfo }
    factoryOf(::WikiImageUrlResolver)
    
    factory<WikiClient>(named("dreamcancel")) { params ->
        val gameId: String = params.get()
        val characterListDB: CharacterListDB = params.get()
        val moveListDB: MoveListDB = params.get()
        val source: DreamCancelWikiDataSource = get()
        val wikiImageUrlResolver: WikiImageUrlResolver = get()

        DreamCancelWikiClient(
            gameId = gameId,
            
            downloadOrFetchUseCase = DownloadOrFetchUseCase { table ->
                source.downloadData(table?.moves.orEmpty())
                    .flatMap { dto ->
                        wikiImageUrlResolver.resolveHitboxUrl(dto)
                            .map { dto.toDomain(imageUrlMap = it, gameId = gameId) }
                    }
            },

            cacheCharacterListUseCase = CacheCharacterListUseCase { characterList ->
                characterListDB.insertCharacterList(characterList)
            },
            fetchCharacterListUseCase = FetchCharacterListUseCase {
                characterListDB.fetchCharacterList()
            },
            fetchCharacterUseCase = FetchCharacterUseCase { charName ->
                characterListDB.fetchCharacterDataFor(charName)
            },

            cacheMoveListUseCase = CacheMoveListUseCase { character, moveList ->
                moveListDB.insertMoveList(character.queryName, moveList)
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