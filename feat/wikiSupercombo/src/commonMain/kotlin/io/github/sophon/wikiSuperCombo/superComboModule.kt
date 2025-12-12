package io.github.sophon.wikiSuperCombo

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.asEmptyDataResult
import io.github.sophon.core.domain.flatMap
import io.github.sophon.core.domain.map
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
import io.github.sophon.wikiSuperCombo.data.SuperComboDataSource
import io.github.sophon.wikiSuperCombo.data.SuperComboDataSourceImpl
import io.github.sophon.wikiSuperCombo.data.WikiImageUrlResolver
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
    factoryOf(::WikiImageUrlResolver)

    factory<WikiClient>(named(WikiClientFeature.SuperCombo.id)) { params ->
        val gameId: String = params.get()
        val characterListDB: CharacterListDB = params.get()
        val moveListDB: MoveListDB = params.get()
        val source: SuperComboDataSource = get()
        val wikiImageUrlResolver: WikiImageUrlResolver = get()

        SuperComboWikiClient(
            gameId = gameId,

            superComboFeatureInfo = get(),

            downloadCharacterListUseCase = DownloadCharacterListUseCase { queryTable ->
                source.downloadCharacterList(queryTable.character)
                    .flatMap { dto ->
                        wikiImageUrlResolver.resolveCharImageUrls(dto)
                            .map { dto.toDomain(gameId, it) }
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

            downloadMoveListUseCase = DownloadMoveListUseCase { queryTable, characterData ->
                source.downloadMoveList(queryTable.moves, characterData)
                    .flatMap { dto ->
                        wikiImageUrlResolver.resolveMoveUrl(dto)
                            .map { dto.toDomain(gameId, characterData, it) }
                    }
            },
            cacheMoveListUseCase = CacheMoveListUseCase { character, moveList ->
                moveListDB.insertMoveList(character, moveList)
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