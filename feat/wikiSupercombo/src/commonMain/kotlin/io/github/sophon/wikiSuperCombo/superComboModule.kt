package io.github.sophon.wikiSuperCombo

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.asEmptyDataResult
import io.github.sophon.core.domain.flatMap
import io.github.sophon.core.domain.map
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
import io.github.sophon.wikiSuperCombo.data.SuperComboDataSource
import io.github.sophon.wikiSuperCombo.data.SuperComboDataSourceImpl
import io.github.sophon.wikiSuperCombo.data.UrlResolver
import io.github.sophon.wikiSuperCombo.data.toDomain
import io.github.sophon.wikiSuperCombo.domain.SuperComboFeatureInfo
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

fun superComboModule() = module {
    singleOf(::SuperComboDataSourceImpl).bind<SuperComboDataSource>()
    singleOf(::SuperComboWikiClient).bind<WikiClient>()
    single { SuperComboFeatureInfo }
    factoryOf(::UrlResolver)

    factory<WikiClient>(named("supercombo")) { params ->
        val gameId: String = params.get()
        val characterListDB: CharacterListDB = params.get()
        val moveListDB: MoveListDB = params.get()
        val source: SuperComboDataSource = get()
        val urlResolver: UrlResolver = get()

        SuperComboWikiClient(
            gameId = gameId,

            superComboFeatureInfo = get(),

            downloadCharacterListUseCase = DownloadCharacterListUseCase { queryTable ->
                source.downloadCharacterList(queryTable.character)
                    .flatMap { dto ->
                        urlResolver.resolveImageUrls(dto)
                            .map { dto.toDomain(it) }
                    }
            },
            cacheCharacterListUseCase = CacheCharacterListUseCase { characterList ->
                characterListDB.insertCharacterList(characterList)
            },
            fetchCharacterUseCase = FetchCharacterUseCase { charName ->
                characterListDB.fetchCharacterDataFor(charName)
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

            downloadMoveListUseCase = DownloadMoveListUseCase { queryTable, charName ->
                source.downloadMoveList(queryTable.moves, charName)
                    .flatMap { dto ->
                        urlResolver.resolveHitboxUrl(dto)
                            .map { dto.toDomain(it) }
                    }
            },
            cacheMoveListUseCase = CacheMoveListUseCase { character, moveList ->
                moveListDB.insertMoveList(
                    charName = character.id.lowercase(),
                    moveList = moveList,
                )
                    .asEmptyDataResult()
                    .flatMap {
                        character.aliasList.fold(Result.Success(Unit) as EmptyResult<WikiError>) { acc, alias ->
                            acc.flatMap {
                                moveListDB.insertMoveList(
                                    charName = alias,
                                    moveList = moveList,
                                ).asEmptyDataResult()
                            }
                        }
                    }
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