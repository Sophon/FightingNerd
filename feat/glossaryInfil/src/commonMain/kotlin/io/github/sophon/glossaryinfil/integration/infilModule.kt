package io.github.sophon.glossaryinfil.integration

import io.github.sophon.glossaryinfil.data.InfilGlossaryDataSource
import io.github.sophon.glossaryinfil.data.InfilGlossaryDataSourceImpl
import io.github.sophon.glossaryinfil.domain.InfilGlossaryClientImpl
import io.github.sophon.glossaryinfil.usecase.CacheGlossaryUseCase
import io.github.sophon.glossaryinfil.usecase.DownloadGlossaryUseCase
import io.github.sophon.glossaryinfil.usecase.FetchDataForTermUseCase
import io.github.sophon.glossaryinfil.usecase.GetFeatureInfoUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val infilModule = module {
    singleOf(::InfilGlossaryDataSourceImpl).bind<InfilGlossaryDataSource>()
    singleOf(::InfilGlossaryClientImpl).bind<InfilGlossaryClient>()

    singleOf(::DownloadGlossaryUseCase)
    singleOf(::CacheGlossaryUseCase)
    singleOf(::FetchDataForTermUseCase)
    singleOf(::GetFeatureInfoUseCase)
}