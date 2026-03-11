package io.github.sophon

import io.github.sophon.data.PlayerRepo
import io.github.sophon.data.local.PlayerRepoImpl
import io.github.sophon.data.remote.EwgfDataSource
import io.github.sophon.data.remote.EwgfDataSourceImpl
import io.github.sophon.domain.EwgfFeatureInfo
import io.github.sophon.usecase.DeletePlayerUseCase
import io.github.sophon.usecase.DownloadPlayerDataUseCase
import io.github.sophon.usecase.RegisterPlayerUseCase
import io.github.sophon.usecase.UpdatePolarisIdUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun ewgfModule(apiToken: String) = module {
    includes(ewgfPlatformModule)

    single { EwgfFeatureInfo }

    single<EwgfDataSource> {
        EwgfDataSourceImpl(
            apiToken = apiToken,
            httpClient = get(),
        )
    }

    singleOf(::EwgfClientImpl).bind<EwgfClient>()
    singleOf(::PlayerRepoImpl).bind<PlayerRepo>()

    singleOf(::RegisterPlayerUseCase)
    singleOf(::DownloadPlayerDataUseCase)
    singleOf(::UpdatePolarisIdUseCase)
    singleOf(::DeletePlayerUseCase)
}

expect val ewgfPlatformModule: Module