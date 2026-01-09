package io.github.sophon.wikimizuumi

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.flatMap
import io.github.sophon.core.domain.map
import io.github.sophon.core.feature.WikiClientFeature
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.core.wiki.usecase.CacheCharacterListUseCase
import io.github.sophon.core.wiki.usecase.CacheMoveListUseCase
import io.github.sophon.core.wiki.usecase.ClearCacheUseCase
import io.github.sophon.core.wiki.usecase.DownloadCharacterListUseCase
import io.github.sophon.core.wiki.usecase.DownloadMoveListUseCase
import io.github.sophon.core.wiki.usecase.DownloadOrFetchUseCase
import io.github.sophon.core.wiki.usecase.FetchCharacterListUseCase
import io.github.sophon.core.wiki.usecase.FetchCharacterUseCase
import io.github.sophon.core.wiki.usecase.FetchMoveListUseCase
import io.github.sophon.core.wiki.usecase.FetchMoveUseCase
import io.github.sophon.core.wiki.usecase.GetLastCacheInsertInstantUseCase
import io.github.sophon.wikimizuumi.data.MizuumiWikiDataSource
import io.github.sophon.wikimizuumi.data.MizuumiWikiDataSourceImpl
import io.github.sophon.wikimizuumi.data.WikiImageUrlResolver
import io.github.sophon.wikimizuumi.data.toDomainAll
import io.github.sophon.wikimizuumi.data.toDomain
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun mizuumiModule() = module {
    singleOf(::MizuumiWikiDataSourceImpl).bind<MizuumiWikiDataSource>()
    singleOf(::MizuumiWikiClient).bind<WikiClient>()
    single { MizuumiFeatureInfo }
    factoryOf(::WikiImageUrlResolver)

    factory<WikiClient>(named(WikiClientFeature.Mizuumi.id)) { params ->
        val gameId: String = params.get()
        val characterListDB: CharacterListDB = params.get()
        val moveListDB: MoveListDB = params.get()
        val source: MizuumiWikiDataSource = get()
        val wikiImageUrlResolver: WikiImageUrlResolver = get()

        MizuumiWikiClient(
            gameId = gameId,

            downloadOrFetchUseCase = DownloadOrFetchUseCase { table ->
                source.downloadData(table?.moves.orEmpty())
                    .flatMap { dto ->
                        wikiImageUrlResolver.resolveHitboxUrl(dto)
                            .map { dto.toDomainAll(imageUrlMap = it, gameId = gameId) }
                    }
            },

            downloadCharacterListUseCase = DownloadCharacterListUseCase { table ->
                source.downloadCharacterList(table.character)
                    .map { dto ->
                        dto.toDomain(gameId = gameId)
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

            downloadMoveListUseCase = DownloadMoveListUseCase { table, characterData ->
                source.downloadMoveList(table.moves, characterData)
                    .flatMap { dto ->
                        wikiImageUrlResolver.resolveHitboxUrl(dto)
                            .map { dto.toDomain(characterData, imageUrlMap = it) }
                    }
            },
            cacheMoveListUseCase = CacheMoveListUseCase { character, moveList ->
                moveListDB.insertMoveList(character, moveList)
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