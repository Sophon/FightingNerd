package io.github.sophon.wikimizuumi.integration

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.featureConfig.model.WikiClientFeature
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.model.WikiClient
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
import io.github.sophon.wikimizuumi.data.toDomain
import io.github.sophon.wikimizuumi.data.toDomainAll
import io.github.sophon.wikimizuumi.domain.MizuumiWikiClient
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
        val game = Game.fromId(gameId)
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
                            .flatMap { hitboxUrlMap ->
                                wikiImageUrlResolver.resolveImageUrls(gameId, dto)
                                    .map { imageUrlMap ->
                                        dto.toDomainAll(
                                            gameId = gameId,
                                            imageUrlMap = imageUrlMap,
                                            hitboxUrlMap = hitboxUrlMap
                                        )
                                    }
                            }
                    }
            },

            downloadCharacterListUseCase = DownloadCharacterListUseCase { table ->
                source.downloadCharacterList(table.character)
                    .flatMap { dto ->
                        wikiImageUrlResolver.resolveImageUrls(dto)
                            .map { dto.toDomain(gameId = gameId, imageUrlMap = it) }
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
                            .map {
                                dto.toDomain(characterData, imageUrlMap = it, gameId = gameId)
                            }
                    }
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