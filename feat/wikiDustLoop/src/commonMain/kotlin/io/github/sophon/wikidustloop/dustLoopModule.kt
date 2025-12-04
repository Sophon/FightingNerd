package io.github.sophon.wikidustloop

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.flatMap
import io.github.sophon.core.domain.map
import io.github.sophon.core.feature.WikiClientFeature
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.core.wiki.usecase.CacheCharacterListUseCase
import io.github.sophon.core.wiki.usecase.DownloadCharacterListUseCase
import io.github.sophon.core.wiki.usecase.FetchCharacterListUseCase
import io.github.sophon.core.wiki.usecase.FetchCharacterUseCase
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

fun dustLoopModule() = module {
    singleOf(::DustLoopDataSourceImpl).bind<DustLoopDataSource>()
    singleOf(::DustLoopWikiClient).bind<WikiClient>()
    single { DustLoopFeatureInfo }
    factoryOf(::ImageUrlResolver)

    factory<WikiClient>(named(WikiClientFeature.DustLoop.id)) { params ->
        val gameId: String = params.get()
        val characterListDB: CharacterListDB = params.get()
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
            }
        )
    }
}